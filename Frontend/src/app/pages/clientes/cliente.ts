import { Empleado } from "../empleados/empleado";

export class Cliente {
  idCliente: number;
  nombre: string;
  apellidos: string;
  dni: string;
  email: string;
  cups: string;
  companiaContratada: string;
  fechaSubidaContrato: string;
  empleado: Empleado;
  telefono: number;
  iban: string; 
  direccion: string;
  logo: string;
}
