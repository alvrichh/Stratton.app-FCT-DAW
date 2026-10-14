import { Component, OnInit } from '@angular/core';
import { Empleado } from '../empleado';
import { CommonModule } from '@angular/common';
import { EmpleadoService } from '../empleado.service';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../auth.service';
import { FormsModule } from '@angular/forms';
import { Alertas } from '../../../core/alertas';

@Component({
  selector: 'app-lista-empleados',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lista-empleados.component.html',
  styleUrl: './lista-empleados.component.css'
})
export class ListaEmpleadosComponent implements OnInit {

  empleados: Empleado[] = [];
  filteredEmpleados: Empleado[] = [];
  searchText = '';
  cargando = true;
  error: string | null = null;

  constructor(private empleadoServicio: EmpleadoService, private router: Router, public authService: AuthService) {}

  ngOnInit(): void {
    this.obtenerEmpleados();
  }

  obtenerEmpleados(): void {
    this.cargando = true;
    this.error = null;
    this.empleadoServicio.obtenerListaDeEmpleados().subscribe({
      next: (dato) => {
        this.empleados = dato;
        this.cargando = false;
        this.applyFilter();
      },
      error: (err) => {
        this.cargando = false;
        this.error = Alertas.mensajeDe(err);
      }
    });
  }

  private normalizar(texto: unknown): string {
    return String(texto ?? '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
  }

  applyFilter(): void {
    const texto = this.normalizar(this.searchText.trim());
    this.filteredEmpleados = !texto ? [...this.empleados] : this.empleados.filter(e =>
      this.normalizar(`${e.nombre} ${e.apellidos}`).includes(texto) ||
      this.normalizar(e.username).includes(texto) ||
      this.normalizar(e.email).includes(texto) ||
      this.normalizar(e.roles?.join(' ')).includes(texto) ||
      this.normalizar(e.asesoria).includes(texto)
    );
  }

  esYoMismo(empleado: Empleado): boolean {
    return empleado.id === this.authService.getEmpleadoId();
  }

  verEmpleado(id: number): void {
    this.router.navigate(['/detalles-empleado', id]);
  }

  actualizarEmpleado(id: number): void {
    this.router.navigate(['/actualizar-empleado', id]);
  }

  async eliminarEmpleado(empleado: Empleado): Promise<void> {
    const confirmado = await Alertas.confirmar('¿Eliminar empleado?',
      `Se eliminará a ${empleado.nombre} ${empleado.apellidos} junto con sus ${empleado.totalClientes} cliente(s).`, 'Sí, eliminar');
    if (!confirmado) {
      return;
    }
    this.empleadoServicio.eliminarEmpleado(empleado.id).subscribe({
      next: () => {
        Alertas.exito('Empleado eliminado', 'El empleado se ha eliminado correctamente');
        this.obtenerEmpleados();
      },
      error: (err) => Alertas.error(err, 'No se pudo eliminar el empleado'),
    });
  }
}
