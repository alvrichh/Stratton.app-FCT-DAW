import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import swal from 'sweetalert2';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';
import { RegisterComponent } from '../register/register.component';

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

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        const token = response.token;
        this.authService.saveToken(token);

        const employeeId = this.authService.getEmpleadoId();
        console.log('Empleado ID después de iniciar sesión:', employeeId);

        swal('Inicio de sesión exitoso', 'Has iniciado sesión correctamente', 'success');
        this.verDashboard(response.role);
      },
      error: (error: any) => {
        console.error('Error al iniciar sesión:', error);
        swal('Error al iniciar sesión', 'Usuario no encontrado o credenciales incorrectas', 'error');
      }
    });
  }

  verDashboard(role: string) {
    if (role === 'ADMIN') {
      this.router.navigate(['/empleados']);
    } else if (role === 'USER') {
      this.router.navigate([`/clientes`]);
    }
  }
}
