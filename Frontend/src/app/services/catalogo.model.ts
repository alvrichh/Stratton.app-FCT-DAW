export interface Comercializadora {
  idComercializadora: number;
  companiaContratada: string;
  plan?: string;
  logo?: string;
}

export interface Asesoria {
  idAsesoria: number;
  nombre: string;
  direccion: string;
  descripcion?: string;
  valoracion: number;
  totalEmpleados: number;
  totalClientes: number;
}
