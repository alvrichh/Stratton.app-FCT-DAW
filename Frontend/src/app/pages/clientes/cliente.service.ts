import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from './cliente';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private baseURL = "http://localhost:8080/api/v1/clientes";

  constructor(private httpClient: HttpClient) { }

  obtenerListaDeClientes(): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}`);
  }

  registrarCliente(cliente: Cliente): Observable<Object> {
    return this.httpClient.post(`${this.baseURL}`, cliente);
  }

  obtenerClientePorId(id: number): Observable<Cliente> {
    return this.httpClient.get<Cliente>(`${this.baseURL}/${id}`);
  }

  actualizarCliente(id: number, cliente: Cliente): Observable<Object> {
    return this.httpClient.put(`${this.baseURL}/${id}`, cliente);
  }

  eliminarCliente(id: number): Observable<Object> {
    return this.httpClient.delete(`${this.baseURL}/${id}`);
  }

  obtenerClientesPorEmpleado(empleadoId: number): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}/empleado/${empleadoId}`);
  }
}
