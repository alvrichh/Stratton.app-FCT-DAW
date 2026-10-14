import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { EmpleadoRequest, empleadoVacio } from '../empleado';
import { EmpleadoService } from '../empleado.service';
import { Alertas } from '../../../core/alertas';
import { EmpleadoFormComponent } from '../empleado-form/empleado-form.component';

@Component({
  selector: 'app-registrar-empleado',
  standalone: true,
  imports: [CommonModule, RouterLink, EmpleadoFormComponent],
  templateUrl: './registrar-empleado.component.html',
  styleUrl: './registrar-empleado.component.css'
})
export class RegistrarEmpleadoComponent {

  empleado: EmpleadoRequest = empleadoVacio();
  guardando = false;

  constructor(private empleadoServicio: EmpleadoService, private router: Router) {}

  guardarEmpleado(datos: EmpleadoRequest): void {
    this.guardando = true;
    this.empleadoServicio.registrarEmpleado(datos).subscribe({
      next: (creado) => {
        this.guardando = false;
        Alertas.exito('Empleado creado', `${creado.nombre} ${creado.apellidos} ya puede iniciar sesión`);
        this.router.navigate(['/empleados']);
      },
      error: (error) => {
        this.guardando = false;
        Alertas.error(error, 'No se pudo crear el empleado');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/empleados']);
  }
}
