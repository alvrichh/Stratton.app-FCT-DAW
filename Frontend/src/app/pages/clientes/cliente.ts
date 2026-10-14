import { EstadoSuministro, Suministro } from './suministro';

/** Cliente tal como lo devuelve la API (ClienteDTO). */
export interface Cliente {
  idCliente: number;
  nombre: string;
  apellidos: string;
  dni: string;
  email: string;
  telefono: string;
  direccion?: string;
  iban?: string;
  fechaSubidaContrato?: string;
  fechaNacimiento?: string;
  /** CUPS del suministro principal. */
  cups?: string;
  companiaContratada?: string;
  comercializadoraId?: number;
  logo?: string;
  empleadoId?: number;
  empleadoNombre?: string;
  asesoriaId?: number;
  asesoriaNombre?: string;
  suministros: Suministro[];
}

/** Datos del formulario de alta/edición de cliente (ClienteRequest). */
export interface ClienteRequest {
  nombre: string;
  apellidos: string;
  dni: string;
  email: string;
  telefono: string;
  direccion?: string;
  iban?: string;
  fechaSubidaContrato?: string;
  fechaNacimiento?: string;
  cups?: string;
  companiaContratada?: string;
  comercializadoraId?: number;
  estado?: EstadoSuministro;
  potencia?: number | null;
  empleadoId?: number;
}

export function clienteVacio(): ClienteRequest {
  return {
    nombre: '',
    apellidos: '',
    dni: '',
    email: '',
    telefono: '',
    direccion: '',
    iban: '',
    fechaSubidaContrato: new Date().toISOString().substring(0, 10),
    fechaNacimiento: '',
    cups: '',
    companiaContratada: '',
    estado: 'PENDIENTE',
    potencia: null,
  };
}

/** Patrones de validación compartidos con el backend. */
export const PATRON_DNI = '^(\\d{8}[A-Za-z]|[XYZxyz]\\d{7}[A-Za-z])$';
export const PATRON_TELEFONO = '^[6789]\\d{8}$';
export const PATRON_IBAN = '^ES\\d{2}(\\s?\\d{4}){5}$';
export const PATRON_CUPS = '^ES\\d{16}[A-Za-z]{2}(\\d[FPRCXYZfprcxyz])?$';
