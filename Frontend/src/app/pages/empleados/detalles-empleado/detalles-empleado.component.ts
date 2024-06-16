import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, RouterLink } from '@angular/router';
import { EmpleadoService } from '../empleado.service';
import swal from 'sweetalert2';
import { Empleado } from '../empleado';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-detalles-empleado',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './detalles-empleado.component.html',
  styleUrl: './detalles-empleado.component.css'
})
export class DetallesEmpleadoComponent implements OnInit {

  id:number;
  empleado:Empleado;
  totalClientes: number;


  constructor(private route:ActivatedRoute, private empleadoServicio:EmpleadoService){}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.empleado = new Empleado();
    this.obtenerTotalClientes();

    this.empleadoServicio.obtenerEmpleadoPorId(this.id).subscribe(dato =>{
      this.empleado = dato;
      swal(`Detalles del empleado ${this.empleado.nombre}`);
    })

  }
  obtenerTotalClientes(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.empleadoServicio.getTotalClientesPorEmpleado(id).subscribe(total => this.totalClientes = total);
  }

}
