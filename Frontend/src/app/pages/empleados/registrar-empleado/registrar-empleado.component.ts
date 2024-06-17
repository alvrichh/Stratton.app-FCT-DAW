import { Component, OnInit } from '@angular/core';
import { Empleado } from '../empleado';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { EmpleadoService } from '../empleado.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registrar-empleado',
  standalone: true,
  imports: [FormsModule, NgIf, CommonModule],
  templateUrl: './registrar-empleado.component.html',
  styleUrl: './registrar-empleado.component.css'
})
export class RegistrarEmpleadoComponent implements OnInit{


  empleado: Empleado = new Empleado();
  username: any;
  rolesDisponibles: string[] = ["ADMIN", "USER"];  // Correcta definición del array de roles
  empleadoForm: any;
  confirmPassword: string;
  constructor(private empleadoServicio:EmpleadoService, private router:Router){}

  ngOnInit(): void {
    console.clear();
    console.log(this.empleado)
    
  }
  onSubmit(){
    this.guardarEmpleado();
  }
  guardarEmpleado(){
    this.empleadoServicio.registrarEmpleado(this.empleado).subscribe(dato => {
      //console.log(dato);
      this.verListaEmpleados();
    }, error => console.log(error));
  }
  verListaEmpleados(){
    this.router.navigate(["/empleados"]);
  }
  validatePasswordConfirmation() {
    if (this.empleado.password !== this.confirmPassword) {
      this.empleadoForm.controls['confirmPassword'].setErrors({ 'passwordMismatch': true });
    } else {
      this.empleadoForm.controls['confirmPassword'].setErrors(null);
    }
  }
  
}
