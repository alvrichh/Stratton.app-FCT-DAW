import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../cliente.service';
import { Cliente, PATRON_CUPS } from '../cliente';
import { ESTADOS_SUMINISTRO, Suministro, SuministroRequest } from '../suministro';
import { Alertas } from '../../../core/alertas';
import { CatalogoService } from '../../../services/catalogo.service';
import { Comercializadora } from '../../../services/catalogo.model';
import { EnterNextDirective } from '../../../core/enter-next.directive';

@Component({
  selector: 'app-detalles-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, EnterNextDirective],
  templateUrl: './detalles-cliente.component.html',
  styleUrl: './detalles-cliente.component.css'
})
export class DetallesClienteComponent implements OnInit {
  id!: number;
  cliente?: Cliente;
  error: string | null = null;
  comercializadoras: Comercializadora[] = [];
  estados = ESTADOS_SUMINISTRO;
  readonly patronCups = PATRON_CUPS;

  mostrarFormulario = false;
  editandoCups: string | null = null;
  nuevo: SuministroRequest = this.suministroVacio();
  guardando = false;

  constructor(private route: ActivatedRoute, private router: Router, private clienteService: ClienteService,
              private catalogo: CatalogoService) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.params['id']);
    this.catalogo.obtenerComercializadoras().subscribe({ next: c => this.comercializadoras = c, error: () => this.comercializadoras = [] });
    this.cargar();
  }

  cargar(): void {
    this.clienteService.obtenerClientePorId(this.id).subscribe({
      next: (data) => this.cliente = data,
      error: (err) => this.error = Alertas.mensajeDe(err),
    });
  }

  suministroVacio(): SuministroRequest {
    return { cups: '', direccion: this.cliente?.direccion ?? '', estado: 'PENDIENTE', mensaje: '', potencia: null, companiaContratada: this.cliente?.companiaContratada ?? '' };
  }

  get totalPorEstado(): Record<string, number> {
    const totales: Record<string, number> = { ACTIVO: 0, PENDIENTE: 0, INACTIVO: 0 };
    this.cliente?.suministros?.forEach(s => totales[s.estado] = (totales[s.estado] ?? 0) + 1);
    return totales;
  }

  nuevoSuministro(): void {
    this.editandoCups = null;
    this.nuevo = this.suministroVacio();
    this.mostrarFormulario = true;
  }

  editarSuministro(s: Suministro): void {
    this.editandoCups = s.cups;
    this.nuevo = { cups: s.cups, direccion: s.direccion, estado: s.estado, mensaje: s.mensaje ?? '', potencia: s.potencia ?? null, companiaContratada: s.companiaContratada ?? '' };
    this.mostrarFormulario = true;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
    this.editandoCups = null;
  }

  guardarSuministro(): void {
    if (this.guardando) {
      return;
    }
    const datos: SuministroRequest = { ...this.nuevo, cups: this.nuevo.cups.trim().toUpperCase() };
    if (!datos.companiaContratada) { delete datos.companiaContratada; }
    if (datos.potencia === null || datos.potencia === undefined) { delete datos.potencia; }
    this.guardando = true;
    const peticion = this.editandoCups
      ? this.clienteService.actualizarSuministro(this.editandoCups, datos)
      : this.clienteService.agregarSuministro(this.id, datos);
    peticion.subscribe({
      next: () => {
        this.guardando = false;
        Alertas.exito(this.editandoCups ? 'Suministro actualizado' : 'Suministro añadido');
        this.cancelarFormulario();
        this.cargar();
      },
      error: (err) => {
        this.guardando = false;
        Alertas.error(err, 'No se pudo guardar el suministro');
      }
    });
  }

  async eliminarSuministro(s: Suministro): Promise<void> {
    const confirmado = await Alertas.confirmar('¿Eliminar suministro?', `Se eliminará el CUPS ${s.cups}.`, 'Sí, eliminar');
    if (!confirmado) {
      return;
    }
    this.clienteService.eliminarSuministro(s.cups).subscribe({
      next: () => { Alertas.exito('Suministro eliminado'); this.cargar(); },
      error: (err) => Alertas.error(err, 'No se pudo eliminar el suministro'),
    });
  }

  async eliminarCliente(): Promise<void> {
    if (!this.cliente) {
      return;
    }
    const confirmado = await Alertas.confirmar('¿Eliminar cliente?',
      `Se eliminará a ${this.cliente.nombre} ${this.cliente.apellidos} y todos sus suministros.`, 'Sí, eliminar');
    if (!confirmado) {
      return;
    }
    this.clienteService.eliminarCliente(this.id).subscribe({
      next: () => { Alertas.exito('Cliente eliminado'); this.router.navigate(['/clientes']); },
      error: (err) => Alertas.error(err, 'No se pudo eliminar el cliente'),
    });
  }

  regresar(): void {
    this.router.navigate(['/clientes']);
  }
}
