import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Empleado } from './empleado';

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {

  //Esta URL obtiene el listado de todos los empleados en el backend
  private baseURL = "http://localhost:8080/api/v1/empleados";

  constructor(private httpClient : HttpClient) { }

  //Este método nos sirve para obtener los empleados
  obtenerListaDeEmpleados():Observable<Empleado[]>{

    return this.httpClient.get<Empleado[]>(`${this.baseURL}`)
  }


}
