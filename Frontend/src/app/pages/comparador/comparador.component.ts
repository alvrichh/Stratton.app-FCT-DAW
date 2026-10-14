import { Component, NgZone, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Alertas } from '../../core/alertas';
import {
  ComparadorService, DatosFactura, Desglose, OfertaComparador, ResultadoComparativa, facturaVacia,
} from './comparador.service';
import { leerFactura } from './lector-factura';
import { FaseLectura, extraerTexto } from './texto-factura';

type Paso = 'subir' | 'revisar' | 'resultado';

/** Tamaño máximo de la factura (se lee en el navegador; evita bloquearlo con archivos enormes). */
const MAX_BYTES = 15 * 1024 * 1024;
/** Datos sin los que no se puede calcular (los que el cliente tiene que completar si faltan). */
const OBLIGATORIOS: (keyof DatosFactura)[] = [
  'dias', 'potenciaP1Kw', 'potenciaP2Kw', 'precioPotenciaP1', 'precioPotenciaP2',
  'consumoP1Kwh', 'consumoP2Kwh', 'consumoP3Kwh', 'precioEnergiaP1', 'precioEnergiaP2', 'precioEnergiaP3',
];
/** WhatsApp de contacto (el mismo de wa.link/44bcfa del pie de página). */
const WHATSAPP = '34679556404';

/**
 * Comparador público de facturas de luz:
 * 1. el cliente sube su factura (PDF o foto) y se lee EN SU NAVEGADOR (texto del PDF u OCR),
 *    sin enviarla a ningún servidor ni API externa,
 * 2. revisa y corrige los datos (o los introduce a mano),
 * 3. ve su factura recalculada con la oferta propia, con los mismos impuestos en los dos lados.
 */
@Component({
  selector: 'app-comparador',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './comparador.component.html',
  styleUrl: './comparador.component.css'
})
export class ComparadorComponent implements OnInit {

  paso: Paso = 'subir';
  oferta?: OfertaComparador;
  datos: DatosFactura = facturaVacia();
  resultado?: ResultadoComparativa;

  leyendo = false;
  /** Fase de la lectura (texto del PDF u OCR) y progreso del OCR en %. */
  fase: FaseLectura | null = null;
  progreso = 0;
  calculando = false;
  arrastrando = false;
  /** Aviso cuando la lectura automática falla y hay que completar los datos a mano. */
  aviso = '';
  /** Si la factura tiene un único precio de energía para todas las horas. */
  precioUnico = true;

  readonly lineas: { clave: keyof Desglose; nombre: string }[] = [
    { clave: 'potencia', nombre: 'Potencia' },
    { clave: 'energia', nombre: 'Energía' },
    { clave: 'bonoSocial', nombre: 'Financiación del bono social' },
    { clave: 'impuestoElectrico', nombre: 'Impuesto eléctrico' },
    { clave: 'alquilerContador', nombre: 'Alquiler del contador' },
    { clave: 'iva', nombre: 'IVA' },
  ];

  private readonly euros = new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' });
  // useGrouping 'always': en español Intl no agrupa los miles de los números de 4 cifras (1129 → 1.129)
  private readonly numero = new Intl.NumberFormat('es-ES', { maximumFractionDigits: 2, useGrouping: 'always' } as unknown as Intl.NumberFormatOptions);
  private readonly precios = new Intl.NumberFormat('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 6 });

  constructor(private comparador: ComparadorService, private zone: NgZone) { }

  ngOnInit(): void {
    this.comparador.oferta().subscribe({
      next: o => this.oferta = o,
      error: () => this.oferta = undefined,
    });
  }

  eur(n: number | null | undefined): string {
    return this.euros.format(n ?? 0);
  }

  num(n: number | null | undefined): string {
    return this.numero.format(n ?? 0);
  }

  /** Precios unitarios (€/kWh, €/kW·día) con al menos dos decimales: 0,10 y no 0,1. */
  precio(n: number | null | undefined): string {
    return this.precios.format(n ?? 0);
  }

  // ---------- Paso 1: subir la factura ----------

  alSoltar(evento: DragEvent): void {
    evento.preventDefault();
    this.arrastrando = false;
    const archivo = evento.dataTransfer?.files?.[0];
    if (archivo) {
      this.leer(archivo);
    }
  }

  alElegir(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const archivo = input.files?.[0];
    if (archivo) {
      this.leer(archivo);
    }
    input.value = '';
  }

  async leer(archivo: File): Promise<void> {
    if (archivo.size > MAX_BYTES) {
      Alertas.info('Archivo demasiado grande', 'La factura no puede superar 15 MB.');
      return;
    }
    this.leyendo = true;
    this.fase = null;
    this.progreso = 0;
    try {
      const texto = await extraerTexto(archivo, (fase, progreso) => this.zone.run(() => {
        this.fase = fase;
        this.progreso = progreso ?? 0;
      }));
      const leidos = leerFactura(texto);
      this.zone.run(() => this.mostrarLeidos(leidos));
    } catch (e) {
      console.error('No se pudo leer la factura', e);
      this.zone.run(() => this.manual('No hemos podido leer este archivo. Introduce los datos de tu factura a mano.'));
    } finally {
      this.zone.run(() => this.leyendo = false);
    }
  }

  private mostrarLeidos(leidos: Partial<DatosFactura>): void {
    this.datos = { ...facturaVacia(), ...leidos };
    this.precioUnico = this.datos.precioEnergiaP1 === this.datos.precioEnergiaP2
      && this.datos.precioEnergiaP2 === this.datos.precioEnergiaP3;
    const faltan = OBLIGATORIOS.filter(c => this.datos[c] === null || this.datos[c] === undefined).length;
    if (faltan === OBLIGATORIOS.length) {
      this.aviso = 'No hemos encontrado los datos en esta factura. Cópialos del detalle de la factura.';
    } else if (faltan > 0) {
      this.aviso = `Hemos leído casi todo, pero faltan ${faltan} dato${faltan > 1 ? 's' : ''}: complétalo${faltan > 1 ? 's' : ''} con tu factura.`;
    } else {
      this.aviso = '';
    }
    this.paso = 'revisar';
  }

  manual(aviso = ''): void {
    this.aviso = aviso;
    this.datos = facturaVacia();
    this.precioUnico = true;
    this.paso = 'revisar';
  }

  // ---------- Paso 2: revisar ----------

  /** Con precio único, el de punta se copia a llano y valle. */
  sincronizarPrecio(): void {
    if (this.precioUnico) {
      this.datos.precioEnergiaP2 = this.datos.precioEnergiaP1;
      this.datos.precioEnergiaP3 = this.datos.precioEnergiaP1;
    }
  }

  calcular(): void {
    this.sincronizarPrecio();
    this.calculando = true;
    this.comparador.calcular(this.datos).subscribe({
      next: r => {
        this.calculando = false;
        this.resultado = r;
        this.paso = 'resultado';
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: e => {
        this.calculando = false;
        Alertas.error(e, 'Revisa los datos de la factura');
      },
    });
  }

  // ---------- Paso 3: resultado ----------

  volver(): void {
    this.paso = 'revisar';
  }

  empezar(): void {
    this.resultado = undefined;
    this.datos = facturaVacia();
    this.aviso = '';
    this.paso = 'subir';
  }

  /** Mensaje para WhatsApp con el resultado, para que el asesor tenga el contexto. */
  get enlaceWhatsapp(): string {
    const r = this.resultado;
    const texto = r
      ? `Hola, he usado el comparador: con la ${r.nombreOferta} pagaría ${this.eur(r.oferta.total)} en lugar de `
        + `${this.eur(r.actual.total)} (${r.dias} días, ${this.num(r.consumoTotalKwh)} kWh). Quiero más información.`
      : 'Hola, quiero información sobre vuestra tarifa de luz.';
    return `https://wa.me/${WHATSAPP}?text=${encodeURIComponent(texto)}`;
  }

  /** Campo obligatorio vacío (para resaltarlo tras la lectura). */
  falta(campo: keyof DatosFactura): boolean {
    return this.datos[campo] === null || this.datos[campo] === undefined || (this.datos[campo] as unknown) === '';
  }
}
