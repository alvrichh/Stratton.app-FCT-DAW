export class Empleado {
    id:number;
    nombre:string;
    apellido:string;
    email:string;
    usuario:string;
    password:string;
    roles: string[]; // Asumiendo que los roles se manejarán como una lista de strings
}
