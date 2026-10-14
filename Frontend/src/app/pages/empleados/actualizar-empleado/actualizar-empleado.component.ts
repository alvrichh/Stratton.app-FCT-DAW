import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EmpleadoService } from '../empleado.service';
import { Empleado, EmpleadoRequest, Rol } from '../empleado';
import { Alertas } from '../../../core/alertas';
import { EmpleadoFormComponent } from '../empleado-form/empleado-form.component';

@Component({
  selector: 'app-actualizar-empleado',
  standalone: true,
  imports: [CommonModule, RouterLink, EmpleadoFormComponent],
  templateUrl: './actualizar-empleado.component.html',
  styleUrl: './actualizar-empleado.component.css'
})
export class ActualizarEmpleadoComponent implements OnInit {

  id!: number;
  empleado?: EmpleadoRequest;
  original?: Empleado;
  guardando = false;

  constructor(private empleadoServicio: EmpleadoService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.params['id']);
    this.empleadoServicio.obtenerEmpleadoPorId(this.id).subscribe({
      next: (e) => {
        this.original = e;
        this.empleado = {
          nombre: e.nombre,
          apellidos: e.apellidos,
          username: e.username,
          email: e.email,
          password: '',
          roles: (e.roles ?? []) as Rol[],
          asesoriaId: e.asesoriaId ?? null,
        };
      },
      error: (error) => {
        Alertas.error(error, 'No se pudo cargar el empleado');
        this.router.navigate(['/empleados']);
      }
    });
  }

  actualizarEmpleado(datos: EmpleadoRequest): void {
    this.guardando = true;
    this.empleadoServicio.actualizarEmpleado(this.id, datos).subscribe({
      next: () => {
        this.guardando = false;
        Alertas.exito('Empleado actualizado', 'Los cambios se han guardado correctamente');
        this.router.navigate(['/empleados']);
      },
      error: (error) => {
        this.guardando = false;
        Alertas.error(error, 'No se pudo actualizar el empleado');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/empleados']);
  }
}
