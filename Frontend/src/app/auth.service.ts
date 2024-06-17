import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    const body = { username, password };
    return this.http.post(`${this.apiUrl}/login`, body);
  }

  register(username: string, password: string) {
    const body = { username, password };
    return this.http.post(`${this.apiUrl}/register`, body);
  }

  saveToken(token: string) {
    localStorage.setItem('token', token);
    this.saveEmpleadoId();
  }

  getToken(): string {
    return localStorage.getItem('token');
  }

  getUserRoles() {
    const token = this.getToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      const role = decodedToken.role || [];
      return role;
    }
    return [];
  }

  saveEmpleadoId() {
    const token = this.getToken();
    const decodedToken = this.jwtHelper.decodeToken(token);
    const id = decodedToken?.id;
    if (id) {
      localStorage.setItem('empleadoId', id);
    }
  }

  getEmpleadoId(): number {
    return Number(localStorage.getItem('empleadoId'));
  }

  isAdmin(): boolean {
    const roles = this.getUserRoles();
    return roles.includes('ADMIN');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return token != null && !this.jwtHelper.isTokenExpired(token);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('empleadoId');
    console.log('Sesión cerrada.');
  }
}
