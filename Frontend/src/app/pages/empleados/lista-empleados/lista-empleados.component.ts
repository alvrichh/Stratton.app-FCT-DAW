import { Component, OnInit } from '@angular/core';
import { Empleado } from '../empleado';
import { CommonModule } from '@angular/common';
import { EmpleadoService } from '../empleado.service';
import { Router, RouterLink } from '@angular/router';
import swal from 'sweetalert2';
import { AuthService } from '../../../auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lista-empleados',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lista-empleados.component.html',
  styleUrl: './lista-empleados.component.css'
})
export class ListaEmpleadosComponent implements OnInit{

  empleados: Empleado[];
  empleado: any;
searchText: String = '';
filteredEmpleados: Empleado[] = [];


  constructor(
    private empleadoServicio: EmpleadoService,
    private router: Router,
    public authService: AuthService // Inyecta el servicio de autenticación
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.obtenerEmpleados();
      this.filteredEmpleados = [...this.empleados];

    }
  }

  actualizarEmpleado(id: number) {
    this.router.navigate(['actualizar-empleado', id]);
  }

  eliminarEmpleado(id: number) {
    swal({
      title: '¿Estas seguro?',
      text: "Confirma si deseas eliminar al empleado",
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
      if (result.value) {
        this.empleadoServicio.eliminarEmpleado(id).subscribe(dato => {
          console.log(dato);
          this.obtenerEmpleados();
          swal(
            'Empleado eliminado',
            'El empleado ha sido eliminado con exito',
            'success'
          );
        });
      }
    });
  }

  verEmpleado(id: number) {
    this.router.navigate(['detalles-empleado', id]);
  }

  private obtenerEmpleados() {
    this.empleadoServicio.obtenerListaDeEmpleados().subscribe(dato => {
      this.empleados = dato;
    });
  }
  

  applyFilter(): void {
    const searchText = this.searchText.toLowerCase().trim();
    // Aplica la ordenación actual después del filtrado
    if (searchText) {
      this.empleados = this.empleados.filter(empleado =>
        empleado.nombre.toLowerCase().includes(searchText) ||
        empleado.apellidos.toLowerCase().includes(searchText) ||
        empleado.username.toLowerCase().includes(searchText)
      );
    } else {
      this.obtenerEmpleados(); // Vuelve a obtener todos los empleados si no hay texto de búsqueda
      this.filteredEmpleados = [...this.empleados];

    }
  }
  
}
