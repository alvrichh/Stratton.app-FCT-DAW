import { Component, OnInit } from '@angular/core';
import { EmpleadoService } from '../empleado.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-perfil-empleado',
  templateUrl: './perfil-empleado.component.html',
  styleUrls: ['./perfil-empleado.component.css'],
  imports: [CommonModule],
  standalone: true,
})
export class PerfilEmpleadoComponent implements OnInit {

  empleado: any;

  constructor(private empleadoService: EmpleadoService) { }

  ngOnInit(): void {
    this.obtenerPerfilEmpleado();
  }

  obtenerPerfilEmpleado() {
    this.empleadoService.obtenerPerfilEmpleado().subscribe(
      (data: any) => {
        this.empleado = data;
      },
      (error: any) => {
        console.log('Error al obtener el perfil del empleado:', error);
      }
    );
  }
}
