import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth.service';
import { Alertas } from '../../core/alertas';
import { EnterNextDirective } from '../../core/enter-next.directive';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, EnterNextDirective],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, AfterViewInit {
  @ViewChild('usernameInput') usernameInput?: ElementRef<HTMLInputElement>;
  username = '';
  password = '';
  cargando = false;
  mostrarPassword = false;
  sesionExpirada = false;
  private returnUrl: string | null = null;

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.sesionExpirada = this.route.snapshot.queryParamMap.has('expirada');
    this.returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.usernameInput?.nativeElement.focus());
  }

  login(): void {
    if (!this.username || !this.password || this.cargando) {
      return;
    }
    this.cargando = true;
    this.authService.login(this.username.trim(), this.password).subscribe({
      next: (res) => {
        this.cargando = false;
        Alertas.exito('Inicio de sesión correcto', `Bienvenido/a, ${res.nombre}`);
        this.router.navigateByUrl(this.returnUrl || this.authService.rutaInicial());
      },
      error: (error) => {
        this.cargando = false;
        Alertas.error(error, 'No se pudo iniciar sesión');
      }
    });
  }
}
