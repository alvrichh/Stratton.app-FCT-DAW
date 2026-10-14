import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, HostBinding, NgZone, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription, filter } from 'rxjs';
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
export class HeaderComponent implements AfterViewInit, OnDestroy {

  /** Cabecera transparente sobre la escena de la landing, hasta que entra el contenido. */
  @HostBinding('class.transparente') transparente = false;

  private enLanding = false;
  private suscripcion?: Subscription;
  private observador?: ResizeObserver;
  private readonly alHacerScroll = () => this.actualizarTransparencia();

  constructor(public authService: AuthService, private router: Router, private zone: NgZone,
              private el: ElementRef<HTMLElement>) {
    this.suscripcion = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => {
        this.enLanding = e.urlAfterRedirects.split(/[?#]/)[0] === '/';
        // Tras renderizar la nueva página
        setTimeout(() => this.actualizarTransparencia());
      });
  }

  ngAfterViewInit(): void {
    this.zone.runOutsideAngular(() => {
      window.addEventListener('scroll', this.alHacerScroll, { passive: true });
      // La altura de la cabecera se publica en --st-header-h (la landing la usa para quedar debajo)
      const cabecera = this.el.nativeElement.querySelector('header');
      if (cabecera && 'ResizeObserver' in window) {
        this.observador = new ResizeObserver(() =>
          document.documentElement.style.setProperty('--st-header-h', `${cabecera.offsetHeight}px`));
        this.observador.observe(cabecera);
      }
    });
  }

  /**
   * Transparente solo al principio de la landing, mientras detrás está el cielo azul:
   * en cuanto se hace scroll la nube cubre la escena de blanco y los enlaces blancos no se leerían.
   */
  private actualizarTransparencia(): void {
    const valor = this.enLanding && window.scrollY < window.innerHeight * 0.15;
    if (valor !== this.transparente) {
      this.zone.run(() => this.transparente = valor);
    }
  }

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

  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.alHacerScroll);
    this.observador?.disconnect();
    this.suscripcion?.unsubscribe();
  }
}
