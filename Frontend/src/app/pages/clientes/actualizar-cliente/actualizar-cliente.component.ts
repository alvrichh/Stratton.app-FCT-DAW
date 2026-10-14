import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Cliente, ClienteRequest, clienteVacio } from '../cliente';
import { ClienteService } from '../cliente.service';
import { Alertas } from '../../../core/alertas';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';

@Component({
  selector: 'app-actualizar-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, ClienteFormComponent],
  templateUrl: './actualizar-cliente.component.html',
  styleUrl: './actualizar-cliente.component.css'
})
export class ActualizarClienteComponent implements OnInit {
  id!: number;
  cliente?: ClienteRequest;
  original?: Cliente;
  guardando = false;

  constructor(private route: ActivatedRoute, private router: Router, private clienteService: ClienteService) { }

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.params['id']);
    this.clienteService.obtenerClientePorId(this.id).subscribe({
      next: (c) => {
        this.original = c;
        const principal = c.suministros?.[0];
        this.cliente = {
          ...clienteVacio(),
          nombre: c.nombre,
          apellidos: c.apellidos,
          dni: c.dni,
          email: c.email,
          telefono: c.telefono,
          direccion: c.direccion ?? '',
          iban: c.iban ?? '',
          fechaSubidaContrato: c.fechaSubidaContrato ?? '',
          fechaNacimiento: c.fechaNacimiento ?? '',
          cups: c.cups ?? '',
          companiaContratada: c.companiaContratada ?? '',
          estado: principal?.estado ?? 'PENDIENTE',
          potencia: principal?.potencia ?? null,
          empleadoId: c.empleadoId,
        };
      },
      error: (error) => {
        Alertas.error(error, 'No se pudo cargar el cliente');
        this.router.navigate(['/clientes']);
      }
    });
  }

  onGuardar(datos: ClienteRequest): void {
    this.guardando = true;
    this.clienteService.actualizarCliente(this.id, datos).subscribe({
      next: () => {
        this.guardando = false;
        Alertas.exito('Cliente actualizado', 'Los cambios se han guardado correctamente');
        this.router.navigate(['/detalles-cliente', this.id]);
      },
      error: (error) => {
        this.guardando = false;
        Alertas.error(error, 'No se pudo actualizar el cliente');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/clientes']);
  }
}
