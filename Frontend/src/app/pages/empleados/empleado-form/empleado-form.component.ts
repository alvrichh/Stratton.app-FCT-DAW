import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmpleadoRequest, ROLES_DISPONIBLES, Rol } from '../empleado';
import { CatalogoService } from '../../../services/catalogo.service';
import { Asesoria } from '../../../services/catalogo.model';
import { EnterNextDirective } from '../../../core/enter-next.directive';

/**
 * Formulario de alta/edición de empleado (solo ADMIN).
 */
@Component({
  selector: 'app-empleado-form',
  standalone: true,
  imports: [CommonModule, FormsModule, EnterNextDirective],
  templateUrl: './empleado-form.component.html',
  styleUrl: './empleado-form.component.css'
})
export class EmpleadoFormComponent implements OnInit {

  @Input({ required: true }) empleado!: EmpleadoRequest;
  @Input() modoEdicion = false;
  @Input() guardando = false;
  @Output() guardar = new EventEmitter<EmpleadoRequest>();
  @Output() cancelar = new EventEmitter<void>();

  asesorias: Asesoria[] = [];
  rolesDisponibles = ROLES_DISPONIBLES;
  confirmPassword = '';

  constructor(private catalogo: CatalogoService) {}

  ngOnInit(): void {
    this.catalogo.obtenerAsesorias().subscribe({ next: a => this.asesorias = a, error: () => this.asesorias = [] });
  }

  tieneRol(rol: Rol): boolean {
    return this.empleado.roles.includes(rol);
  }

  toggleRol(rol: Rol, activo: boolean): void {
    const roles = this.empleado.roles.filter(r => r !== rol);
    if (activo) {
      roles.push(rol);
    }
    this.empleado.roles = roles;
  }

  get passwordsCoinciden(): boolean {
    return (this.empleado.password ?? '') === this.confirmPassword;
  }

  get passwordValida(): boolean {
    const p = this.empleado.password ?? '';
    if (this.modoEdicion && p === '') {
      return true; // en edición, vacía = no cambiar
    }
    return p.length >= 6 && this.passwordsCoinciden;
  }

  onSubmit(): void {
    if (this.guardando || !this.passwordValida) {
      return;
    }
    const datos: EmpleadoRequest = { ...this.empleado };
    if (!datos.password) { delete datos.password; }
    if (!datos.asesoriaId) { delete datos.asesoriaId; }
    if (!datos.roles.length) { datos.roles = ['USER']; }
    this.guardar.emit(datos);
  }
}
