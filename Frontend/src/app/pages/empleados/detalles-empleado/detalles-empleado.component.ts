import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EmpleadoService } from '../empleado.service';
import { Empleado } from '../empleado';
import { Suministro } from '../../clientes/suministro';
import { Alertas } from '../../../core/alertas';
import { iniciales, rolLegible } from '../../../core/formato';

@Component({
  selector: 'app-detalles-empleado',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './detalles-empleado.component.html',
  styleUrl: './detalles-empleado.component.css'
})
export class DetallesEmpleadoComponent implements OnInit {
  readonly iniciales = iniciales;
  readonly rolLegible = rolLegible;

  id!: number;
  empleado?: Empleado;
  suministros: Suministro[] = [];
  error: string | null = null;

  constructor(private route: ActivatedRoute, private empleadoServicio: EmpleadoService) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.params['id']);
    this.empleadoServicio.obtenerEmpleadoPorId(this.id).subscribe({
      next: (dato) => this.empleado = dato,
      error: (err) => this.error = Alertas.mensajeDe(err),
    });
    this.empleadoServicio.obtenerSuministrosDeClientes(this.id).subscribe({
      next: (s) => this.suministros = s,
      error: () => this.suministros = [],
    });
  }

  contar(estado: string): number {
    return this.suministros.filter(s => s.estado === estado).length;
  }
}
