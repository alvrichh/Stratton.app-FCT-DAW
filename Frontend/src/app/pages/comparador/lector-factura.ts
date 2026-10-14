import { DatosFactura } from './comparador.service';

/**
 * Lectura de facturas de luz SIN servicios externos: a partir del texto de la factura (sacado del
 * PDF o por OCR, ver texto-factura.ts) busca con patrones los datos para recalcularla.
 *
 * Las facturas españolas 2.0TD siguen el mismo esquema regulado aunque cada compañía lo maquete
 * a su manera ("Pot. Punta 5,750 kW x 0,102759 Eur/kW x 56 días", "Punta ... 335,00 kWh"...),
 * así que se buscan esas expresiones en cualquier orden. Lo que no se encuentra se deja vacío y
 * el cliente lo completa en el paso de revisión. El texto de un OCR trae errores típicos (barras y
 * comas perdidas, "kKWh"...) que también se contemplan.
 */
export function leerFactura(textoOriginal: string): Partial<DatosFactura> {
  const texto = normalizar(textoOriginal);
  const lineas = texto.split('\n').map(l => l.trim()).filter(Boolean);
  const d: Partial<DatosFactura> = {};

  d.comercializadora = comercializadora(lineas);
  // Nombre de la tarifa, cortando donde empieza otra etiqueta de la columna de al lado
  d.tarifa = buscar(texto, /(?:contrato de mercado libre|producto contratado|tarifa contratada|producto)\s*:\s*([^\n]{3,60}?)(?=\s+(?:segmento|referencia|cups|peaje|distribuidora|potencias?|n[ºo°]|fin de|\S+\s*:)|\s*$)/im)
    ?.replace(/[\s|;:,.-]+$/, '').trim() || null;

  // Días facturados: "(56 días)" o "56 días" junto al periodo de facturación
  d.dias = entero(buscar(texto, /periodo de facturaci[oó]n[^\n]*?\(?\s*(\d{1,3})\s*d[ií]as/i))
    ?? entero(buscar(texto, /\(\s*(\d{1,3})\s*d[ií]as\s*\)/i))
    ?? entero(buscar(texto, /x\s*(\d{1,3})\s*d[ií]as/i));

  // Término de potencia: "<punta|P1> 5,750 kW x 0,102759 €/kW[·día|año] x 56 días"
  // (la barra de €/kW es opcional porque el OCR a veces la pierde: "EurkW")
  const potencia = (periodo: string) => {
    const m = new RegExp(`(?:${periodo})[^\\n]*?(${NUM})\\s*kw\\s*x\\s*(${NUM})\\s*${EUR}\\s*/?\\s*kw\\s*(?:[·/ -]?\\s*(d[ií]a|a[ñn]o|mes))?`, 'i').exec(texto);
    if (!m) return null;
    const precio = numero(m[2]);
    const unidad = (m[3] ?? 'día').toLowerCase();
    return {
      kw: numero(m[1]),
      precio: precio === null ? null : unidad.startsWith('a') ? precio / 365 : unidad === 'mes' ? precio * 12 / 365 : precio,
    };
  };
  const p1 = potencia('punta|p1');
  const p2 = potencia('valle|p2');
  d.potenciaP1Kw = p1?.kw ?? numero(buscar(texto, /potencias? contratadas?[^\n]*?(?:punta|p1)\s*:?\s*(NUM)\s*kw/i));
  d.potenciaP2Kw = p2?.kw ?? numero(buscar(texto, /potencias? contratadas?[^\n]*?(?:valle|p2)\s*:?\s*(NUM)\s*kw/i)) ?? d.potenciaP1Kw;
  d.precioPotenciaP1 = redondear(p1?.precio);
  d.precioPotenciaP2 = redondear(p2?.precio);

  // Consumo por periodo: "Punta|Llano|Valle (o P1|P2|P3)" seguido SOLO de números hasta el final de la
  // línea (lecturas del contador y, la última, el consumo). Las facturas a dos columnas mezclan texto de
  // la otra columna al principio de la línea, por eso no se exige que la línea empiece por el periodo.
  const consumo = (periodo: string) => {
    const patron = new RegExp(`(?:^|\\s)(?:${periodo})\\s*:?\\s+((?:(?:${NUM})\\s+)*(?:${NUM}))\\s*(?:kwh)?\\s*$`, 'i');
    for (const l of lineas) {
      const m = patron.exec(l);
      if (m) {
        const nums = m[1].trim().split(/\s+/);
        return numero(nums[nums.length - 1]);
      }
    }
    return null;
  };
  let periodos = [consumo('punta|p1'), consumo('llano|p2'), consumo('valle|p3')];

  // Término de energía: "<periodo> 335 kWh x 0,1943 €/kWh" o un único "1.129,643 kWh x 0,194345 €/kWh"
  const precioEnergia = (periodo: string) =>
    numero(buscar(texto, new RegExp(`(?:${periodo})[^\\n]*?(?:${NUM})\\s*kwh\\s*x\\s*(${NUM})\\s*${EUR_KWH}`, 'i')));
  // Línea del consumo facturado "N kWh x P €/kWh" con N distinto de cero (se ignoran promociones a 0,000 kWh)
  const lineaUnica = new RegExp(`(?:consumo|energ[ií]a)[^\\n]*?\\b(?!0+(?:,0+)?\\s*kwh)(${NUM})\\s*kwh\\s*x\\s*(${NUM})\\s*${EUR_KWH}`, 'i').exec(texto);
  const unico = lineaUnica ? numero(lineaUnica[2]) : null;
  const porPeriodo = precioEnergia('punta|p1');
  d.precioEnergiaP1 = porPeriodo ?? unico;
  d.precioEnergiaP2 = precioEnergia('llano|p2') ?? unico;
  d.precioEnergiaP3 = precioEnergia('valle|p3') ?? unico;

  // Consumo total facturado: "Consumo total N kWh" (el OCR a veces lee "ConsumoTotal ... kKWh") o,
  // con precio único, los kWh de esa línea
  const consumoTotal = numero(buscar(texto, /consumo\s*total[^\n]*?(NUM)\s*k{1,2}wh/i))
    ?? (porPeriodo === null && lineaUnica ? numero(lineaUnica[1]) : null);
  if (periodos.every(p => p === null) && consumoTotal !== null) {
    periodos = [consumoTotal, 0, 0];
  } else if (consumoTotal !== null && periodos.every(p => p !== null)) {
    periodos = cuadrarConElTotal(periodos as number[], consumoTotal);
  }
  [d.consumoP1Kwh, d.consumoP2Kwh, d.consumoP3Kwh] = periodos;

  // Otros conceptos por día
  d.alquilerContadorDia = numero(buscar(texto, new RegExp(`alquiler[^\\n]*?(?:\\d+\\s*d[ií]as\\s*x\\s*)?(${NUM})\\s*${EUR}\\s*/?\\s*d[ií]a`, 'i')));
  d.bonoSocialDia = numero(buscar(texto, new RegExp(`bono social[^\\n]*?(?:\\d+\\s*d[ií]as\\s*x\\s*)?(${NUM})\\s*${EUR}\\s*/?\\s*d[ií]a`, 'i')));

  d.totalFactura = numero(buscar(texto, /total(?:\s+(?:factura|importe|a pagar))?\s*:?\s*(NUM)\s*(?:€|eur)/i));
  // "kWh" con la h opcional: el OCR la pierde a veces
  d.consumoAnualKwh = numero(buscar(texto, /consumo (?:del )?[uú]ltimo a[ñn]o\s*:?\s*(NUM)\s*kwh?/i));

  return Object.fromEntries(Object.entries(d).filter(([, v]) => v !== null && v !== undefined)) as Partial<DatosFactura>;
}

/** Número en formato español o inglés: 1.129,643 · 0,194345 · 0.194344 · 275,24 */
const NUM = '\\d{1,3}(?:\\.\\d{3})+(?:,\\d+)?|\\d+(?:[.,]\\d+)?';
const EUR = '(?:€|eur(?:os)?)';
/** "€/kWh" con la barra opcional (el OCR la pierde a veces) */
const EUR_KWH = `${EUR}\\s*/?\\s*kwh`;

const COMERCIALIZADORAS = [
  'Endesa', 'Iberdrola', 'Naturgy', 'TotalEnergies', 'Repsol', 'Plenitude', 'Niba', 'Gana Energía', 'Fox Energía',
  'CHC Energía', 'Holaluz', 'Octopus Energy', 'Audax', 'Factor Energía', 'Som Energia', 'Curenergía', 'Energía XXI',
];

/**
 * Compañía que factura: la primera del catálogo que aparece, saltando las líneas de la distribuidora
 * ("Distribuidora: IBERDROLA DISTRIB ELEC SAU"), que es la dueña de la red y no la que factura.
 */
function comercializadora(lineas: string[]): string | null {
  for (const l of lineas) {
    const sinTildes = quitarTildes(l).toLowerCase();
    if (/distribuidora|distrib\b/.test(sinTildes)) continue;
    let mejor: { nombre: string; pos: number } | null = null;
    for (const nombre of COMERCIALIZADORAS) {
      const pos = sinTildes.indexOf(quitarTildes(nombre).toLowerCase());
      if (pos >= 0 && (!mejor || pos < mejor.pos)) {
        mejor = { nombre, pos };
      }
    }
    if (mejor) return mejor.nombre;
  }
  return null;
}

/**
 * Hace cuadrar los consumos por periodo con el total facturado:
 * - OCR: una coma decimal perdida multiplica el valor por 100 ("363,00" leído como "36300") o por 10;
 *   si al corregir un solo periodo la suma cuadra con el total, se da por buena la corrección;
 * - decimales: la suma de periodos puede quedar a menos de 1 kWh del total (1.129 frente a 1.129,643);
 *   la diferencia se añade al primer periodo.
 */
function cuadrarConElTotal(periodos: number[], total: number): number[] {
  const suma = (v: number[]) => v.reduce((a, b) => a + b, 0);
  let p = periodos.slice();
  if (Math.abs(suma(p) - total) >= 1) {
    busqueda: for (let i = 0; i < p.length; i++) {
      for (const factor of [100, 10]) {
        const prueba = p.slice();
        prueba[i] = p[i] / factor;
        if (Math.abs(suma(prueba) - total) < 1) {
          p = prueba;
          break busqueda;
        }
      }
    }
  }
  const diferencia = total - suma(p);
  if (diferencia > 0 && diferencia < 1) {
    p[0] = redondear(p[0] + diferencia, 3) as number;
  }
  return p;
}

/** Une espacios, quita los separadores de puntos ("....") y unifica símbolos. */
function normalizar(t: string): string {
  return t
    .replace(/\r/g, '')
    .replace(/\.{3,}/g, ' ')
    .replace(/[ \t ]+/g, ' ')
    .replace(/×/g, 'x')
    .replace(/\s*\/\s*/g, '/');
}

function buscar(texto: string, patron: RegExp): string | null {
  const fuente = patron.source.replace(/NUM/g, NUM);
  const m = new RegExp(fuente, patron.flags).exec(texto);
  return m ? m[1] : null;
}

function numero(s: string | null | undefined): number | null {
  if (!s) return null;
  let t = s.trim();
  if (/^\d{1,3}(\.\d{3})+(,\d+)?$/.test(t)) {
    t = t.replace(/\./g, '').replace(',', '.');
  } else {
    t = t.replace(',', '.');
  }
  const n = Number(t);
  return Number.isFinite(n) ? n : null;
}

function entero(s: string | null): number | null {
  const n = numero(s);
  return n === null ? null : Math.round(n);
}

function redondear(n: number | null | undefined, decimales = 6): number | null {
  if (n === null || n === undefined) return null;
  const f = 10 ** decimales;
  return Math.round(n * f) / f;
}

function quitarTildes(t: string): string {
  return t.normalize('NFD').replace(/[̀-ͯ]/g, '');
}
