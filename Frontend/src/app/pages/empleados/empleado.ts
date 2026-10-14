import { Cliente } from '../clientes/cliente';

export type Rol = 'USER' | 'MANAGER' | 'ADMIN';

export const ROLES_DISPONIBLES: { valor: Rol; etiqueta: string }[] = [
  { valor: 'USER', etiqueta: 'Empleado (USER)' },
  { valor: 'MANAGER', etiqueta: 'Jefe de equipo (MANAGER)' },
  { valor: 'ADMIN', etiqueta: 'Administrador (ADMIN)' },
];

/** Empleado tal como lo devuelve la API (EmpleadoDTO). */
export interface Empleado {
  id: number;
  nombre: string;
  apellidos: string;
  email: string;
  username: string;
  /** Rol principal. */
  role: string;
  roles: string[];
  asesoriaId?: number;
  asesoria?: string;
  totalClientes: number;
  clientes: Cliente[];
}

/** Datos del formulario de alta/edición de empleado (EmpleadoRequest). */
export interface EmpleadoRequest {
  nombre: string;
  apellidos: string;
  username: string;
  email: string;
  password?: string;
  roles: Rol[];
  asesoriaId?: number | null;
}

export function empleadoVacio(): EmpleadoRequest {
  return { nombre: '', apellidos: '', username: '', email: '', password: '', roles: ['USER'], asesoriaId: null };
}
