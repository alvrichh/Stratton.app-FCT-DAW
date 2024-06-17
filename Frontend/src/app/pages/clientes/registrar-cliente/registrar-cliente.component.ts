import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { ClienteService } from '../cliente.service';
import { Router } from '@angular/router';
import { Cliente } from '../cliente';

@Component({
  selector: 'app-registrar-cliente',
  standalone: true,
  imports: [FormsModule, NgIf, CommonModule],
  templateUrl: './registrar-cliente.component.html',
  styleUrls: ['./registrar-cliente.component.css']
})
export class RegistrarClienteComponent implements OnInit {

  cliente: Cliente = new Cliente();
  empleadoId: number = 1; // Asumiendo que ya tienes el ID del empleado
  clienteForm: any;

  constructor(private clienteService: ClienteService, private router: Router) { }

  ngOnInit(): void {
    console.clear();
    console.log(this.cliente);

  }

  onSubmit(): void {
    console.log(this.cliente);
    this.guardarCliente();
  }

  guardarCliente(): void {
    // Método para enviar el cliente al servicio y guardar en la base de datos
    this.clienteService.agregarClienteAEmpleado(this.empleadoId, this.cliente).subscribe(
      data => {
        console.log('Cliente guardado correctamente:', data);
        this.verListaClientes(); // Redirigir a la lista de clientes después de guardar
      },
      error => {
        console.error('Error al guardar el cliente:', error);
      }
    );
  }
  verListaClientes(): void {
    this.router.navigate(['/clientes']);
  }
  selectComercializadora(): void {
    console.log('Comercializadora seleccionada:', this.cliente.companiaContratada);
    // Por ejemplo, podrías realizar acciones específicas según la comercializadora seleccionada
  }
}
