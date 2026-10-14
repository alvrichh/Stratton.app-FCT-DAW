import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Empleado, EmpleadoRequest } from './empleado';
import { Cliente } from '../clientes/cliente';
import { Suministro } from '../clientes/suministro';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {

  private baseURL = `${environment.apiUrl}/v2/empleados`;

  constructor(private httpClient: HttpClient) { }

  obtenerListaDeEmpleados(): Observable<Empleado[]> {
    return this.httpClient.get<Empleado[]>(this.baseURL);
  }

  registrarEmpleado(empleado: EmpleadoRequest): Observable<Empleado> {
    return this.httpClient.post<Empleado>(this.baseURL, empleado);
  }

  actualizarEmpleado(id: number, empleado: EmpleadoRequest): Observable<Empleado> {
    return this.httpClient.put<Empleado>(`${this.baseURL}/${id}`, empleado);
  }

  obtenerEmpleadoPorId(id: number): Observable<Empleado> {
    return this.httpClient.get<Empleado>(`${this.baseURL}/${id}`);
  }

  eliminarEmpleado(id: number): Observable<{ eliminar: boolean }> {
    return this.httpClient.delete<{ eliminar: boolean }>(`${this.baseURL}/${id}`);
  }

  /** Perfil del empleado autenticado, con sus clientes. */
  obtenerPerfilEmpleado(): Observable<Empleado> {
    return this.httpClient.get<Empleado>(`${this.baseURL}/perfil`);
  }

  getTotalClientesPorEmpleado(id: number): Observable<number> {
    return this.httpClient.get<number>(`${this.baseURL}/${id}/total-clientes`);
  }

  obtenerClientesDeEmpleado(id: number): Observable<Cliente[]> {
    return this.httpClient.get<Cliente[]>(`${this.baseURL}/${id}/clientes`);
  }

  /** Suministros de todos los clientes del empleado (para los contadores por estado). */
  obtenerSuministrosDeClientes(id: number): Observable<Suministro[]> {
    return this.httpClient.get<Suministro[]>(`${this.baseURL}/${id}/suministros-clientes`);
  }
}
