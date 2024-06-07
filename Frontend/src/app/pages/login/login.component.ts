import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import swal from 'sweetalert2';
import { Router } from '@angular/router';
import { Empleado } from '../empleado';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private authService: AuthService,  private router: Router) {} // Inyecta el servicio AuthService
  username: string;
  password: string;
  token: string;
  roles: string[] = [];

  login() {
    console.log("Intentando iniciar sesión con:", this.username, this.password);
    this.authService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        const token = response.token; // Extrae el token de la respuesta
        this.authService.saveToken(token); // Guarda el token en el localStorage usando el servicio AuthService
        swal('Inicio de sesión exitoso', 'Has iniciado sesión correctamente', 'success');
        this.verDashboard(); // Redirige al usuario a la página adecuada
      },
      error: (error: any) => {
        console.error('Error al iniciar sesión:', error);
        swal('Error al iniciar sesión', 'Usuario no encontrado o credenciales incorrectas', 'error');
      }
    });
  }

  verDashboard() {
    if (this.authService.isAdmin()) {
      this.router.navigate(['/empleados']);
    } else {
      this.router.navigate(['/clientes']); 
    }
  }
}
