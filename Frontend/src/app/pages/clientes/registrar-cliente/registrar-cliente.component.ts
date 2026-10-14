import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../cliente.service';
import { ClienteRequest, clienteVacio } from '../cliente';
import { Alertas } from '../../../core/alertas';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';

@Component({
  selector: 'app-registrar-cliente',
  standalone: true,
  imports: [CommonModule, ClienteFormComponent],
  templateUrl: './registrar-cliente.component.html',
  styleUrls: ['./registrar-cliente.component.css']
})
export class RegistrarClienteComponent {

  cliente: ClienteRequest = clienteVacio();
  guardando = false;

  constructor(private clienteService: ClienteService, private router: Router) { }

  guardarCliente(datos: ClienteRequest): void {
    this.guardando = true;
    const peticion = datos.empleadoId
      ? this.clienteService.agregarClienteAEmpleado(datos.empleadoId, datos)
      : this.clienteService.registrarCliente(datos);
    peticion.subscribe({
      next: (creado) => {
        this.guardando = false;
        Alertas.exito('Cliente registrado', `${creado.nombre} ${creado.apellidos} se ha guardado correctamente`);
        this.router.navigate(['/detalles-cliente', creado.idCliente]);
      },
      error: (error) => {
        this.guardando = false;
        Alertas.error(error, 'No se pudo registrar el cliente');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/clientes']);
  }
}
