/**
 * Saca el texto de una factura en el propio navegador (el archivo no sale del dispositivo):
 * - PDF digital (lo habitual en las facturas de las compañías): texto directo con pdf.js, exacto y rápido;
 * - foto, o PDF escaneado sin texto: OCR con Tesseract.js en español.
 * Las dos librerías se cargan solo cuando se usa el comparador (import dinámico).
 */

/** Páginas que se leen como máximo (los datos de facturación están en las primeras). */
const MAX_PAGINAS = 3;
/** Por debajo de estos caracteres se considera que el PDF es una imagen escaneada. */
const MIN_TEXTO_PDF = 200;

export type FaseLectura = 'pdf' | 'ocr';

type CrearWorker = typeof import('tesseract.js').createWorker;

export async function extraerTexto(archivo: File, alCambiarFase?: (fase: FaseLectura, progreso?: number) => void): Promise<string> {
  const esPdf = archivo.type === 'application/pdf' || archivo.name.toLowerCase().endsWith('.pdf');
  if (!esPdf) {
    alCambiarFase?.('ocr', 0);
    return reconocer([archivo], p => alCambiarFase?.('ocr', p));
  }

  alCambiarFase?.('pdf');
  const pdfjs = await import('pdfjs-dist');
  pdfjs.GlobalWorkerOptions.workerSrc = 'assets/pdfjs/pdf.worker.min.mjs';
  const pdf = await pdfjs.getDocument({ data: await archivo.arrayBuffer() }).promise;
  const paginas = Math.min(pdf.numPages, MAX_PAGINAS);

  let texto = '';
  for (let n = 1; n <= paginas; n++) {
    const pagina = await pdf.getPage(n);
    const contenido = await pagina.getTextContent();
    texto += lineasDePdf(contenido.items as ElementoTexto[]) + '\n';
  }
  if (texto.replace(/\s/g, '').length >= MIN_TEXTO_PDF) {
    return texto;
  }

  // PDF escaneado: se dibuja cada página y se pasa por OCR
  alCambiarFase?.('ocr', 0);
  const imagenes: Blob[] = [];
  for (let n = 1; n <= paginas; n++) {
    const pagina = await pdf.getPage(n);
    const vista = pagina.getViewport({ scale: 2 });
    const lienzo = document.createElement('canvas');
    lienzo.width = vista.width;
    lienzo.height = vista.height;
    await pagina.render({ canvasContext: lienzo.getContext('2d')!, viewport: vista }).promise;
    imagenes.push(await new Promise<Blob>(ok => lienzo.toBlob(b => ok(b!), 'image/png')));
  }
  return reconocer(imagenes, p => alCambiarFase?.('ocr', p));
}

interface ElementoTexto {
  str: string;
  transform: number[];
}

/**
 * pdf.js devuelve trozos de texto sueltos con su posición: se agrupan por altura (y) para
 * reconstruir las líneas, y dentro de cada línea se ordenan de izquierda a derecha.
 */
export function lineasDePdf(items: ElementoTexto[]): string {
  const filas: { y: number; trozos: { x: number; s: string }[] }[] = [];
  for (const it of items) {
    if (!it.str?.trim()) continue;
    const x = it.transform[4];
    const y = it.transform[5];
    let fila = filas.find(f => Math.abs(f.y - y) < 3);
    if (!fila) {
      fila = { y, trozos: [] };
      filas.push(fila);
    }
    fila.trozos.push({ x, s: it.str });
  }
  return filas
    .sort((a, b) => b.y - a.y)
    .map(f => f.trozos.sort((a, b) => a.x - b.x).map(t => t.s.trim()).join(' '))
    .join('\n');
}

async function reconocer(imagenes: Blob[], progreso: (p: number) => void): Promise<string> {
  // tesseract.js es CommonJS (module.exports = { createWorker, ... }): según el bundler sus funciones
  // quedan como exportaciones con nombre o colgando de "default"; se admiten las dos formas
  const modulo = await import('tesseract.js') as unknown as Record<string, unknown>;
  const porDefecto = modulo['default'] as Record<string, unknown> | undefined;
  const createWorker = (modulo['createWorker'] ?? porDefecto?.['createWorker']) as CrearWorker | undefined;
  if (!createWorker) {
    throw new Error('Tesseract.js no disponible (exporta: ' + Object.keys(modulo).join(', ') + ')');
  }
  let actual = 0;
  const trabajador = await createWorker('spa', 1, {
    logger: m => {
      if (m.status === 'recognizing text') {
        progreso(Math.round(((actual + m.progress) / imagenes.length) * 100));
      }
    },
  });
  try {
    let texto = '';
    for (const imagen of imagenes) {
      const { data } = await trabajador.recognize(imagen);
      texto += data.text + '\n';
      actual++;
    }
    return texto;
  } finally {
    await trabajador.terminate();
  }
}
