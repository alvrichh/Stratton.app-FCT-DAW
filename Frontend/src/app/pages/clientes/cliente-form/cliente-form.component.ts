import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteRequest, PATRON_CUPS, PATRON_DNI, PATRON_IBAN, PATRON_TELEFONO } from '../cliente';
import { ESTADOS_SUMINISTRO } from '../suministro';
import { CatalogoService } from '../../../services/catalogo.service';
import { Comercializadora } from '../../../services/catalogo.model';
import { EnterNextDirective } from '../../../core/enter-next.directive';
import { AuthService } from '../../../auth.service';
import { EmpleadoService } from '../../empleados/empleado.service';
import { Empleado } from '../../empleados/empleado';

/**
 * Formulario de alta/edición de cliente, compartido por "Registrar cliente" y "Editar cliente".
 */
@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, FormsModule, EnterNextDirective],
  templateUrl: './cliente-form.component.html',
  styleUrl: './cliente-form.component.css'
})
export class ClienteFormComponent implements OnInit {

  @Input({ required: true }) cliente!: ClienteRequest;
  @Input() modoEdicion = false;
  @Input() guardando = false;
  @Output() guardar = new EventEmitter<ClienteRequest>();
  @Output() cancelar = new EventEmitter<void>();

  comercializadoras: Comercializadora[] = [];
  empleados: Empleado[] = [];
  estados = ESTADOS_SUMINISTRO;
  readonly patronDni = PATRON_DNI;
  readonly patronTelefono = PATRON_TELEFONO;
  readonly patronIban = PATRON_IBAN;
  readonly patronCups = PATRON_CUPS;
  readonly hoy = new Date().toISOString().substring(0, 10);
  readonly fechaMayorEdad = new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().substring(0, 10);

  constructor(private catalogo: CatalogoService, public authService: AuthService, private empleadoService: EmpleadoService) {}

  ngOnInit(): void {
    this.catalogo.obtenerComercializadoras().subscribe(c => this.comercializadoras = c);
    if (this.authService.isAdmin()) {
      this.empleadoService.obtenerListaDeEmpleados().subscribe({
        next: e => this.empleados = e,
        error: () => this.empleados = [],
      });
    }
  }

  get logoSeleccionado(): string | undefined {
    return this.comercializadoras.find(c => c.companiaContratada === this.cliente.companiaContratada)?.logo;
  }

  onSubmit(): void {
    if (this.guardando) {
      return;
    }
    const datos: ClienteRequest = { ...this.cliente };
    if (!datos.cups) { delete datos.cups; }
    if (!datos.companiaContratada) { delete datos.companiaContratada; }
    if (!datos.fechaNacimiento) { delete datos.fechaNacimiento; }
    if (!datos.fechaSubidaContrato) { delete datos.fechaSubidaContrato; }
    if (!datos.iban) { delete datos.iban; }
    if (datos.potencia === null || datos.potencia === undefined) { delete datos.potencia; }
    if (!datos.empleadoId) { delete datos.empleadoId; }
    this.guardar.emit(datos);
  }
}
