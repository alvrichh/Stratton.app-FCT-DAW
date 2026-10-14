import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { environment } from '../../environments/environment';
import { Asesoria, Comercializadora } from './catalogo.model';

/**
 * Catálogos de solo lectura (comercializadoras y asesorías) para los desplegables.
 */
@Injectable({
  providedIn: 'root'
})
export class CatalogoService {

  private baseURL = `${environment.apiUrl}/v2`;
  private comercializadoras$?: Observable<Comercializadora[]>;

  constructor(private http: HttpClient) { }

  obtenerComercializadoras(): Observable<Comercializadora[]> {
    if (!this.comercializadoras$) {
      this.comercializadoras$ = this.http.get<Comercializadora[]>(`${this.baseURL}/comercializadoras`)
        .pipe(shareReplay(1));
    }
    return this.comercializadoras$;
  }

  obtenerAsesorias(): Observable<Asesoria[]> {
    return this.http.get<Asesoria[]>(`${this.baseURL}/asesorias`);
  }
}
