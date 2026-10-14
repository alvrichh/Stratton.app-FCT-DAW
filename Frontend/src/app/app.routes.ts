import { Routes } from '@angular/router';
import { ListaEmpleadosComponent } from './pages/empleados/lista-empleados/lista-empleados.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistrarEmpleadoComponent } from './pages/empleados/registrar-empleado/registrar-empleado.component';
import { ActualizarEmpleadoComponent } from './pages/empleados/actualizar-empleado/actualizar-empleado.component';
import { DetallesEmpleadoComponent } from './pages/empleados/detalles-empleado/detalles-empleado.component';
import { PerfilEmpleadoComponent } from './pages/empleados/perfil-empleado/perfil-empleado.component';
import { RegistrarClienteComponent } from './pages/clientes/registrar-cliente/registrar-cliente.component';
import { ListaClientesComponent } from './pages/clientes/lista-clientes/lista-clientes.component';
import { DetallesClienteComponent } from './pages/clientes/detalles-cliente/detalles-cliente.component';
import { ActualizarClienteComponent } from './pages/clientes/actualizar-cliente/actualizar-cliente.component';
import { RegisterComponent } from './pages/register/register.component';
import { InicioComponent } from './pages/inicio/inicio.component';
import { LandingComponent } from './pages/landing/landing.component';
import { ComparadorComponent } from './pages/comparador/comparador.component';
import { adminGuard, authGuard, guestGuard } from './core/auth.guard';

export const routes: Routes = [
  // Landing pública con la escena de parallax; /inicio sigue siendo la portada con las comercializadoras
  { path: '', component: LandingComponent, pathMatch: 'full' },
  { path: 'inicio', component: InicioComponent },
  // Comparador público de facturas (enlazado desde la landing)
  { path: 'comparador', component: ComparadorComponent, title: 'Comparador de facturas · StrattonApp' },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  // Clientes: cualquier empleado autenticado (un USER solo ve los suyos)
  { path: 'clientes', component: ListaClientesComponent, canActivate: [authGuard] },
  { path: 'registrar-cliente', component: RegistrarClienteComponent, canActivate: [authGuard] },
  { path: 'detalles-cliente/:id', component: DetallesClienteComponent, canActivate: [authGuard] },
  { path: 'actualizar-cliente/:id', component: ActualizarClienteComponent, canActivate: [authGuard] },
  { path: 'perfil', component: PerfilEmpleadoComponent, canActivate: [authGuard] },

  // Empleados: solo ADMIN
  { path: 'empleados', component: ListaEmpleadosComponent, canActivate: [adminGuard] },
  { path: 'registrar-empleado', component: RegistrarEmpleadoComponent, canActivate: [adminGuard] },
  { path: 'actualizar-empleado/:id', component: ActualizarEmpleadoComponent, canActivate: [adminGuard] },
  { path: 'detalles-empleado/:id', component: DetallesEmpleadoComponent, canActivate: [adminGuard] },

  { path: '**', redirectTo: '' },
];
