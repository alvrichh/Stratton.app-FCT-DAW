import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EmpleadoService } from '../empleado.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Empleado } from '../empleado';

@Component({
  selector: 'app-actualizar-empleado',
  standalone: true,
  imports: [ CommonModule, FormsModule],
  templateUrl: './actualizar-empleado.component.html',
  styleUrl: './actualizar-empleado.component.css'
})
export class ActualizarEmpleadoComponent implements OnInit{

  id: number;
  empleado: Empleado = new Empleado();

  constructor(private empleadoServicio:EmpleadoService, private router:Router, private route: ActivatedRoute){}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.obtenerEmpleado(this.id);
  }

  obtenerEmpleado(id: number) {
    this.empleadoServicio.obtenerEmpleadoPorId(id).subscribe(
      data => {
        console.log(data);
        this.empleado = data;
      },
      error => console.log(error)
    );
  }

  onSubmit(){
    console.log(this.empleado)
    this.actualizarEmpleado();
  }

  actualizarEmpleado(){
    this.empleadoServicio.actualizarEmpleado(this.id, this.empleado).subscribe(
      data => {
        console.log(data);
        this.verListaEmpleados();
      },
      error => console.log(error)
    );
  }

  verListaEmpleados(){
    this.router.navigate(["/empleados"]);
  }
}
