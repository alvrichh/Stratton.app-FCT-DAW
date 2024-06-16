import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import swal from 'sweetalert2';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';
import { RegisterComponent } from '../register/register.component';
import { Empleado } from '../empleados/empleado';
import { EmpleadoService } from '../empleados/empleado.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule, RegisterComponent, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string;
  password: string;

  constructor(private authService: AuthService, private router: Router) {} // Inyecta el servicio AuthService

  login() {
    console.log("Intentando iniciar sesión con:", this.username, this.password);
    this.authService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        const token = response.token; // Extrae el token de la respuesta
        this.authService.saveToken(token); // Guarda el token en el localStorage usando el servicio AuthService
        swal('Inicio de sesión exitoso', 'Has iniciado sesión correctamente', 'success');
        this.verDashboard(response.role); // Redirige al usuario a la página adecuada basada en el rol
      },
      error: (error: any) => {
        console.error('Error al iniciar sesión:', error);
        swal('Error al iniciar sesión', 'Usuario no encontrado o credenciales incorrectas', 'error');
      }
    });
  }

  verDashboard(role: string) {
    if (role === 'ADMIN') {
      this.router.navigate(['/empleados']); // Redirige a la lista de empleados si el usuario es admin
    } else if (role === 'USER') {
      this.router.navigate([`/clientes`]); // Redirige a la lista de clientes si el usuario es USER
    }
  }
}
