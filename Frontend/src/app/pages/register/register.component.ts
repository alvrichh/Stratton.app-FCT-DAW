import { Component } from '@angular/core';
import { AuthService, RegistroRequest } from '../../auth.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Alertas } from '../../core/alertas';
import { EnterNextDirective } from '../../core/enter-next.directive';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, EnterNextDirective],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  datos: RegistroRequest = { nombre: '', apellidos: '', username: '', email: '', password: '' };
  confirmPassword = '';
  cargando = false;

  constructor(private authService: AuthService, private router: Router) {}

  get passwordsCoinciden(): boolean {
    return this.datos.password === this.confirmPassword;
  }

  register(): void {
    if (!this.passwordsCoinciden || this.cargando) {
      return;
    }
    this.cargando = true;
    this.authService.register(this.datos).subscribe({
      next: (res) => {
        this.cargando = false;
        Alertas.exito('Registro completado', `Bienvenido/a, ${res.nombre}`);
        this.router.navigate([this.authService.rutaInicial()]);
      },
      error: (error) => {
        this.cargando = false;
        Alertas.error(error, 'No se pudo completar el registro');
      }
    });
  }
}
