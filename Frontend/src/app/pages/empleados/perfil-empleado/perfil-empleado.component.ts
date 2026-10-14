import { Component, OnInit } from '@angular/core';
import { EmpleadoService } from '../empleado.service';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Empleado } from '../empleado';
import { Alertas } from '../../../core/alertas';
import { iniciales, rolLegible } from '../../../core/formato';

/** Perfil del empleado autenticado, con sus clientes. */
@Component({
  selector: 'app-perfil-empleado',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './perfil-empleado.component.html',
  styleUrls: ['./perfil-empleado.component.css'],
})
export class PerfilEmpleadoComponent implements OnInit {
  readonly iniciales = iniciales;
  readonly rolLegible = rolLegible;

  empleado?: Empleado;
  error: string | null = null;

  constructor(private empleadoService: EmpleadoService) { }

  ngOnInit(): void {
    this.empleadoService.obtenerPerfilEmpleado().subscribe({
      next: (data) => this.empleado = data,
      error: (err) => this.error = Alertas.mensajeDe(err),
    });
  }

  get totalPorEstado(): Record<string, number> {
    const totales: Record<string, number> = { ACTIVO: 0, PENDIENTE: 0, INACTIVO: 0 };
    this.empleado?.clientes?.forEach(c => c.suministros?.forEach(s => totales[s.estado] = (totales[s.estado] ?? 0) + 1));
    return totales;
  }
}
