import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from './cliente';
import { Suministro } from './Suministro';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private baseURL = "http://localhost:8080/api/v2/clientes";
  private empleadoURL = "http://localhost:8080/api/v2/empleados";

  constructor(private httpClient: HttpClient) { }

  obtenerListaDeClientes(): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}`);
  }

  registrarCliente(cliente: Cliente): Observable<Object> {
    return this.httpClient.post(`${this.empleadoURL}`, cliente);
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

  obtenerClientesPorEmpleado(id: number): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}/${id}/clientes`);
  }
  agregarClienteAEmpleado(id: number, cliente: Cliente): Observable<Object> {
    return this.httpClient.post(`${this.baseURL}/${id}/clientes`, cliente);
  }

  // Nuevo método para obtener suministros de los clientes de un empleado
  obtenerSuministrosDeClientesPorEmpleado(id: number): Observable<Suministro[]> {
    return this.httpClient.get<Suministro[]>(`${this.empleadoURL}/${id}/suministros-clientes`);
  }
}
