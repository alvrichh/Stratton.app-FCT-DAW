// cliente.ts
import { Empleado } from './empleado';

export class Cliente {
  id: number;
  nombre: string;
  apellidos: string;
  dni: string;
  email: string;
  comercializadora: string;
  telefono: string;
  numeroCUPS: string;
  empleado: Empleado; // Propiedad para almacenar el empleado asociado
}
