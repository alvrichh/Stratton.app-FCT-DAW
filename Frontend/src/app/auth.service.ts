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

  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  getToken(): string {
    return localStorage.getItem('token');
  }

  getUserRoles() {
    const token = this.getToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      const roles = decodedToken.roles || [];
      console.log('Roles del usuario:', roles);
      return roles;
    }
    return [];
  }

  getEmpleadoId(): number {
    const token = this.getToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      const empleadoId = decodedToken.empleadoId;
      console.log('ID del empleado:', empleadoId);
      return empleadoId;
    }
    console.log("error");
    return null;
  }

  isAdmin(): boolean {
    const roles = this.getUserRoles();
    console.log('Usuario es administrador:', roles.includes('ADMIN'));
    return roles.includes('ADMIN');
  }
}
