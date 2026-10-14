import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable, tap } from 'rxjs';
import { environment } from '../environments/environment';

/** Respuesta del backend al iniciar sesión o registrarse. */
export interface AuthResponse {
  token: string;
  role: string;
  roles: string[];
  id: number;
  username: string;
  nombre: string;
}

/** Datos del formulario de registro. */
export interface RegistroRequest {
  nombre: string;
  apellidos: string;
  username: string;
  email: string;
  password: string;
}

const TOKEN_KEY = 'token';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/v1/auth`;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { username, password })
      .pipe(tap(res => this.saveToken(res.token)));
  }

  register(datos: RegistroRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, datos)
      .pipe(tap(res => this.saveToken(res.token)));
  }

  saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private claims(): any {
    const token = this.getToken();
    if (!token) {
      return null;
    }
    try {
      return this.jwtHelper.decodeToken(token);
    } catch {
      return null;
    }
  }

  /** Roles del usuario (claim "roles" del token). */
  getUserRoles(): string[] {
    const claims = this.claims();
    if (!claims) {
      return [];
    }
    if (Array.isArray(claims.roles)) {
      return claims.roles;
    }
    return claims.role ? [claims.role] : [];
  }

  /** Rol principal (ADMIN, MANAGER o USER). */
  getMainRole(): string {
    return this.claims()?.role ?? 'USER';
  }

  getEmpleadoId(): number {
    return Number(this.claims()?.id ?? 0);
  }

  getUsername(): string {
    return this.claims()?.sub ?? '';
  }

  getNombre(): string {
    return this.claims()?.nombre ?? this.getUsername();
  }

  isAdmin(): boolean {
    return this.getUserRoles().includes('ADMIN');
  }

  /** ADMIN o MANAGER: puede ver los clientes de todos los empleados. */
  isJefe(): boolean {
    const roles = this.getUserRoles();
    return roles.includes('ADMIN') || roles.includes('MANAGER');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
      return !this.jwtHelper.isTokenExpired(token);
    } catch {
      return false;
    }
  }

  /** Página a la que ir tras iniciar sesión según el rol. */
  rutaInicial(): string {
    return this.isAdmin() ? '/empleados' : '/clientes';
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem('empleadoId');
  }
}
