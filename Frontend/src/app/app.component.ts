import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ListaEmpleadosComponent } from './pages/empleados/lista-empleados/lista-empleados.component';
import { PerfilEmpleadoComponent } from './pages/empleados/perfil-empleado/perfil-empleado.component';
import { ListaClientesComponent } from './pages/clientes/lista-clientes/lista-clientes.component';
import { RegisterComponent } from './pages/register/register.component';
import { FooterComponent } from './layout/footer/footer.component';
import { HeaderComponent } from './layout/header/header.component';
//servicio
import { CargarScriptsService } from './cargar-scripts.service';
import { AuthService } from './auth.service';
import { MaterialModule } from './modules/material.module';
import { HomeComponent } from './layout/home/home.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ListaEmpleadosComponent, CommonModule, RouterOutlet, RouterLink, FormsModule, ListaClientesComponent, PerfilEmpleadoComponent, RegisterComponent, HeaderComponent, FooterComponent, MaterialModule, HomeComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Sistema de gestión de empleados';
  token: any;

}
