import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../auth.service';
import { Alertas } from '../../core/alertas';
import { iniciales, rolLegible } from '../../core/formato';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  constructor(public authService: AuthService, private router: Router) {}

  get iniciales(): string {
    return iniciales(this.authService.getNombre());
  }

  get rolLegible(): string {
    return rolLegible(this.authService.getMainRole());
  }

  async logout(): Promise<void> {
    const confirmado = await Alertas.confirmar('¿Cerrar sesión?', 'Confirma si deseas cerrar sesión', 'Sí, cerrar sesión', 'question');
    if (confirmado) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }
}
