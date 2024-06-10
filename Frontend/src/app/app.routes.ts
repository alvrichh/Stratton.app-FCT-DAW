import { Routes } from '@angular/router';
import { ListaEmpleadosComponent } from './pages/empleados/lista-empleados/lista-empleados.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistrarEmpleadoComponent } from './pages/empleados/registrar-empleado/registrar-empleado.component';
import { ActualizarEmpleadoComponent } from './pages/empleados/actualizar-empleado/actualizar-empleado.component';
import { DetallesEmpleadoComponent } from './pages/empleados/detalles-empleado/detalles-empleado.component';
import { RegistrarClienteComponent } from './pages/clientes/registrar-cliente/registrar-cliente.component';
import { ListaClientesComponent } from './pages/clientes/lista-clientes/lista-clientes.component';
import { RegisterComponent } from './pages/register/register.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { HomeComponent } from './layout/home/home.component';

export const routes: Routes = [
    // Redirecciona al componente
    { path: 'empleados', component: ListaEmpleadosComponent, data: { role: 'ADMIN' } },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent},
    { path: 'registrar-empleado', component: RegistrarEmpleadoComponent },
    { path: 'actualizar-empleado/:id', component: ActualizarEmpleadoComponent },
    { path: 'detalles-empleado/:id', component: DetallesEmpleadoComponent },
    { path: 'registrar-cliente', component: RegistrarClienteComponent },
    { path: 'clientes', component: ListaClientesComponent }, // Agrega la ruta para ListaClientesComponent
    { path: 'feedback', component: FeedbackComponent },
    { path: 'dashboard', component: HomeComponent},
    { path: '', redirectTo: 'login', pathMatch: 'full' },

];
