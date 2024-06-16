import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../auth.service';
import { Router, RouterLink } from '@angular/router';
import { EmpleadoService } from '../../pages/empleados/empleado.service';
import swal from 'sweetalert2';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
empleado: any;
  constructor(public authService: AuthService, private router: Router, private empleadoServicio: EmpleadoService) {}
  
  logout() {
    swal({
      title: '¿Estás seguro?',
      text: 'Confirma si deseas cerrar sesión',
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, cerrar sesión',
      cancelButtonText: 'No, cancelar',
      buttonsStyling: true
    }).then((result) => {
      if (result.value) {
        this.authService.logout();
        this.router.navigate(['/login']); // Redirige al usuario a la página de login
      }
    });
  }
  
}