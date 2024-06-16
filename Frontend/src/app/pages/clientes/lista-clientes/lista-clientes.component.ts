import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../cliente.service';
import { Router } from '@angular/router';
import { Cliente } from '../cliente'; // Importa la clase Cliente
import { CommonModule } from '@angular/common';
import swal from 'sweetalert2';
import { AuthService } from '../../../auth.service';

@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule], // No necesitas importar Empleado aquí
  templateUrl: './lista-clientes.component.html',
  styleUrl: './lista-clientes.component.css'
})
export class ListaClientesComponent implements OnInit {

  clientes: Cliente[];
  empleadoId: number;
  isAdmin: boolean = false;
  empleados: any;

  constructor(private clienteService: ClienteService, private router: Router, public authService: AuthService) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
    this.empleadoId = this.authService.getEmpleadoId(); // Obtén el ID del empleado logueado
    this.isAdmin = this.authService.isAdmin(); // Verifica si el usuario es administrador
    this.obtenerClientes();
    }
  }

  private obtenerClientes() {
    if (this.isAdmin) {
      this.clienteService.obtenerListaDeClientes().subscribe(data => {
        this.clientes = data;
      });
    } else {
      this.clienteService.obtenerClientesPorEmpleado(this.empleadoId).subscribe(data => {
        this.clientes = data;
      });
    }
  }

  verCliente(id: number) {
    this.router.navigate(['detalles-cliente', id]);
  }

  actualizarCliente(id: number) {
    this.router.navigate(['actualizar-cliente', id]);
  }
  
  eliminarCliente(id:number){
    swal({
      title: '¿Estas seguro?',
      text: "Confirma si deseas eliminar al cliente",
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si , elimínalo',
      cancelButtonText: 'No, cancelar',
      confirmButtonClass: 'btn btn-success',
      cancelButtonClass: 'btn btn-danger',
      buttonsStyling: true
    }).then((result) => {
      if(result.value){
        this.clienteService.eliminarCliente(id).subscribe(dato => {
          console.log(dato);
          this.obtenerClientes();
          swal(
            'Cliente eliminado',
            'El Cliente ha sido eliminado con exito',
            'success'
          )
        })
      }
    })
  }
}