import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, ClienteRequest } from './cliente';
import { Suministro, SuministroRequest } from './suministro';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private baseURL = `${environment.apiUrl}/v2/clientes`;
  private suministrosURL = `${environment.apiUrl}/v2/suministros`;

  constructor(private httpClient: HttpClient) { }

  /** Clientes visibles para el usuario autenticado (todos si es ADMIN/MANAGER, los suyos si es USER). */
  obtenerListaDeClientes(): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(this.baseURL);
  }

  obtenerClientesPorEmpleado(empleadoId: number): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}/${empleadoId}/clientes`);
  }

  obtenerClientePorId(id: number): Observable<Cliente> {
    return this.httpClient.get<Cliente>(`${this.baseURL}/${id}`);
  }

  /** Crea un cliente asignado al usuario autenticado. */
  registrarCliente(cliente: ClienteRequest): Observable<Cliente> {
    return this.httpClient.post<Cliente>(this.baseURL, cliente);
  }

  /** Crea un cliente asignado a un empleado concreto (ADMIN/MANAGER). */
  agregarClienteAEmpleado(empleadoId: number, cliente: ClienteRequest): Observable<Cliente> {
    return this.httpClient.post<Cliente>(`${this.baseURL}/${empleadoId}/clientes`, cliente);
  }

  actualizarCliente(id: number, cliente: ClienteRequest): Observable<Cliente> {
    return this.httpClient.put<Cliente>(`${this.baseURL}/${id}`, cliente);
  }

  eliminarCliente(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/${id}`);
  }

  // ----- Suministros -----

  obtenerSuministros(clienteId: number): Observable<Suministro[]> {
    return this.httpClient.get<Suministro[]>(`${this.baseURL}/${clienteId}/suministros`);
  }

  agregarSuministro(clienteId: number, suministro: SuministroRequest): Observable<Suministro> {
    return this.httpClient.post<Suministro>(`${this.baseURL}/${clienteId}/suministros`, suministro);
  }

  actualizarSuministro(cups: string, suministro: SuministroRequest): Observable<Suministro> {
    return this.httpClient.put<Suministro>(`${this.suministrosURL}/${encodeURIComponent(cups)}`, suministro);
  }

  eliminarSuministro(cups: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.suministrosURL}/${encodeURIComponent(cups)}`);
  }
}
