import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../cliente.service';
import { Router, RouterLink } from '@angular/router';
import { Cliente } from '../cliente'; // Importa la clase Cliente
import { CommonModule } from '@angular/common';
import swal from 'sweetalert2';
import { AuthService } from '../../../auth.service';
import { FormsModule, NgModel } from '@angular/forms';

@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], // No necesitas importar Empleado aquí
  templateUrl: './lista-clientes.component.html',
  styleUrl: './lista-clientes.component.css',
})
export class ListaClientesComponent implements OnInit {
  clientes: Cliente[];
  id: number;
  filteredClientes: Cliente[] = [];
  isAdmin: boolean = false;
  searchText: string = '';
  empleados: any;

  constructor(
    private clienteService: ClienteService,
    private router: Router,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.id = this.authService.getEmpleadoId(); // Obtén el ID del empleado logueado
      this.isAdmin = this.authService.isAdmin(); // Verifica si el usuario es administrador
      this.obtenerClientes();
     // console.log(this.authService.getEmpleadoId());
      this.filteredClientes = [...this.clientes];
    }
  }

  private obtenerClientes() {
    if (this.isAdmin) {
      this.clienteService.obtenerListaDeClientes().subscribe((data) => {
        this.clientes = data;
        this.filteredClientes = [...this.clientes]; // Inicialmente muestra todos los clientes

      });
    } else {
      this.clienteService.obtenerClientesPorEmpleado(this.id).subscribe((data) => {
          this.clientes = data;
          this.filteredClientes = [...this.clientes]; // Inicialmente muestra todos los clientes

        });
    }
  }

  verCliente(id: number) {
    this.router.navigate(['detalles-cliente', id]);
  }

  actualizarCliente(id: number) {
    this.router.navigate(['actualizar-cliente', id]);
  }

  eliminarCliente(id: number) {
    swal({
      title: '¿Estas seguro?',
      text: 'Confirma si deseas eliminar al cliente',
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si , elimínalo',
      cancelButtonText: 'No, cancelar',
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: true,
    }).then((result) => {
      if (result.value) {
        this.clienteService.eliminarCliente(id).subscribe((dato) => {
          console.log(dato);
          this.obtenerClientes();
          swal(
            'Cliente eliminado',
            'El Cliente ha sido eliminado con exito',
            'success'
          );
        });
      }
    });
  }
  /*
  FILTRO DE BÚSQUEDA (NO FUNCIONA)
  */
  applyFilter(): void {
    const searchText = this.searchText.toLowerCase().trim();
    if (searchText) {
      this.filteredClientes = this.clientes.filter(cliente =>
        cliente.nombre.toLowerCase().includes(searchText) ||
        cliente.apellidos.toLowerCase().includes(searchText) ||
        cliente.dni.toLowerCase().includes(searchText) ||
        cliente.companiaContratada.toLowerCase().includes(searchText) ||
        cliente.fechaSubidaContrato.toLowerCase().includes(searchText)
      );
    } else {
      this.filteredClientes = [...this.clientes]; // Mostrar todos los clientes si no hay texto de búsqueda
    }
  }
}
