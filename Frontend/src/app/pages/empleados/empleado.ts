export class Empleado {
    id:number;
    nombre:string;
    apellidos:string;
    email:string;
    username:string;
    password:string;
    role: string[]; // Asumiendo que los roles se manejarán como una lista de strings
    asesoria: string;
    clientes: [];
}   
