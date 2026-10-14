/** Estado de un suministro en la comercializadora. */
export type EstadoSuministro = 'ACTIVO' | 'PENDIENTE' | 'INACTIVO';

export const ESTADOS_SUMINISTRO: EstadoSuministro[] = ['ACTIVO', 'PENDIENTE', 'INACTIVO'];

/** Punto de suministro (CUPS) de un cliente, tal como lo devuelve la API. */
export interface Suministro {
  cups: string;
  direccion: string;
  estado: EstadoSuministro;
  mensaje?: string;
  potencia?: number;
  clienteId?: number;
  clienteNombre?: string;
  comercializadoraId?: number;
  companiaContratada?: string;
  logo?: string;
}

/** Datos para crear o editar un suministro. */
export interface SuministroRequest {
  cups: string;
  direccion: string;
  estado?: EstadoSuministro;
  mensaje?: string;
  potencia?: number | null;
  companiaContratada?: string;
  comercializadoraId?: number;
}
