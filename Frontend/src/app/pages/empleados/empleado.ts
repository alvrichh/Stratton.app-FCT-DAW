import { Cliente } from "../clientes/cliente";

export class Empleado {
    id:number;
    nombre:string;
    apellidos:string;
    email:string;
    username:string;
    password:string;
    role: string[]; // Asumiendo que los roles se manejarán como una lista de strings
    asesoria: string;
    clientes: Cliente[]; // Ajustar a un array de Cliente
    foto: "https://i.pinimg.com/originals/7a/b8/2a/7ab82a20276d7691aa649a90494da0be.jpg";
}   
