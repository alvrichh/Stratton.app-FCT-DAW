// cliente.ts

import { Empleado } from "../empleados/empleado";


export class Cliente {
  idCliente: number;
  nombre: string;
  apellidos: string;
  dni: string;
  email: string;
  cups: string; // CUPS (Código Universal del Punto de Suministro)
  companiaContratada: string; // Compañía contratada por el cliente
  fechaSubidaContrato: string; // Fecha de subida del contrato
  empleado: Empleado; // Propiedad para almacenar el empleado asociado
}
