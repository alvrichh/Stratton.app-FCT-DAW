import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { ClienteService } from '../cliente.service';
import { Router } from '@angular/router';
import { Cliente } from '../cliente';
import { AuthService } from '../../../auth.service'; // Asegúrate de importar AuthService

@Component({
  selector: 'app-registrar-cliente',
  standalone: true,
  imports: [FormsModule, NgIf, CommonModule],
  templateUrl: './registrar-cliente.component.html',
  styleUrls: ['./registrar-cliente.component.css']
})
export class RegistrarClienteComponent implements OnInit {

  cliente: Cliente = new Cliente();
  empleadoId: number; 
  clienteForm: any;

  constructor(
    private clienteService: ClienteService,
    private router: Router,
    private authService: AuthService // Inyecta AuthService
  ) { }

  ngOnInit(): void {
    console.clear();
    console.log(this.cliente);
    
    this.empleadoId = this.authService.getEmpleadoId(); // Obtén el ID del empleado
    // this.empleadoId = this.authService.getEmpleadoId();
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

    // Ejemplo: Mostrar imagen asociada a la comercializadora seleccionada
    switch (this.cliente.companiaContratada) {
      case 'Endesa':
        this.cliente.logo = 'assets/images/endesa.png'; // Ruta a la imagen de Endesa
        break;
      case 'Iberdrola':
        this.cliente.logo = 'assets/images/iberdrola.png'; // Ruta a la imagen de Iberdrola
        break;
      case 'Naturgy':
        this.cliente.logo = 'assets/images/naturgy.png'; // Ruta a la imagen de Naturgy
        break;
      default:
        this.cliente.logo = ''; // Si no hay selección o no se reconoce la comercializadora, limpiar la imagen
        break;
    }
  }  
}
