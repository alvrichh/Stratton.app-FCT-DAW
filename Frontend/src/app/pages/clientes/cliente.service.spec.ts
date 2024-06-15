import { Injectable } from '@angular/core';
import { Cliente } from './cliente';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private clientes: Cliente[] = [];

  constructor() { }

  registrarCliente(cliente: Cliente): Observable<Cliente> {
    cliente.idCliente = this.clientes.length + 1; // Generar un ID sencillo
    this.clientes.push(cliente);
    return of(cliente);
  }

  getClientes(): Observable<Cliente[]> {
    return of(this.clientes);
  }

  getClienteById(id: number): Observable<Cliente> {
    const cliente = this.clientes.find(c => c.idCliente === id);
    return of(cliente!);
  }

  eliminarCliente(id: number): Observable<any> {
    this.clientes = this.clientes.filter(cliente => cliente.idCliente !== id);
    return of({ status: 'success' });
  }
}
