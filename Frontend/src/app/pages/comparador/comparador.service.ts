import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/** Oferta propia con la que se compara. */
export interface OfertaComparador {
  nombre: string;
  precioEnergia: number;
  precioPotenciaP1: number;
  precioPotenciaP2: number;
}

/**
 * Datos de la factura (2.0TD) que se recalculan. Potencia: P1 punta, P2 valle.
 * Consumo y energía: P1 punta, P2 llano, P3 valle. Precios sin impuestos.
 */
export interface DatosFactura {
  comercializadora: string | null;
  tarifa: string | null;
  dias: number | null;
  potenciaP1Kw: number | null;
  potenciaP2Kw: number | null;
  consumoP1Kwh: number | null;
  consumoP2Kwh: number | null;
  consumoP3Kwh: number | null;
  precioPotenciaP1: number | null;
  precioPotenciaP2: number | null;
  precioEnergiaP1: number | null;
  precioEnergiaP2: number | null;
  precioEnergiaP3: number | null;
  alquilerContadorDia: number | null;
  bonoSocialDia: number | null;
  totalFactura: number | null;
  consumoAnualKwh: number | null;
}

export interface Desglose {
  potencia: number;
  energia: number;
  bonoSocial: number;
  impuestoElectrico: number;
  alquilerContador: number;
  iva: number;
  total: number;
}

export interface ResultadoComparativa {
  actual: Desglose;
  oferta: Desglose;
  ahorro: number;
  ahorroPorcentaje: number;
  ahorroAnual: number;
  consumoTotalKwh: number;
  dias: number;
  totalFacturaOriginal: number | null;
  nombreOferta: string;
  precioEnergiaOferta: number;
}

/** Factura vacía, con el alquiler regulado del contador monofásico (0,81 €/mes). */
export function facturaVacia(): DatosFactura {
  return {
    comercializadora: null, tarifa: null, dias: null,
    potenciaP1Kw: null, potenciaP2Kw: null,
    consumoP1Kwh: null, consumoP2Kwh: null, consumoP3Kwh: null,
    precioPotenciaP1: null, precioPotenciaP2: null,
    precioEnergiaP1: null, precioEnergiaP2: null, precioEnergiaP3: null,
    alquilerContadorDia: 0.02663, bonoSocialDia: null,
    totalFactura: null, consumoAnualKwh: null,
  };
}

/**
 * Comparador público de facturas de luz (no necesita sesión). La factura se lee en el navegador
 * (ver lector-factura.ts); al backend solo llegan los consumos y precios para calcular.
 */
@Injectable({ providedIn: 'root' })
export class ComparadorService {

  private baseURL = `${environment.apiUrl}/v1/comparador`;

  constructor(private http: HttpClient) { }

  oferta(): Observable<OfertaComparador> {
    return this.http.get<OfertaComparador>(`${this.baseURL}/oferta`);
  }

  calcular(datos: DatosFactura): Observable<ResultadoComparativa> {
    return this.http.post<ResultadoComparativa>(`${this.baseURL}/calcular`, datos);
  }
}
