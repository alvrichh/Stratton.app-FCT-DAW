import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { ScrollToPlugin } from 'gsap/ScrollToPlugin';
import { AuthService } from '../../auth.service';

gsap.registerPlugin(ScrollTrigger, ScrollToPlugin);

/**
 * Página de bienvenida con la escena de parallax de montañas y nubes y el texto
 * "stratton global / stratton app" enmascarado.
 *
 * Adaptada de https://codepen.io/alvrichh/pen/XWwVBmG (recreación de la escena de
 * Unfold, https://dribbble.com/unfold). En lugar de un contenedor fijo a pantalla completa,
 * la escena va en un bloque "sticky" dentro de una sección de 200vh, para convivir con la
 * cabecera, el resto de la página y el pie de la aplicación.
 */
@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements AfterViewInit, OnDestroy {

  @ViewChild('hero', { static: true }) hero!: ElementRef<HTMLElement>;
  @ViewChild('contenido', { static: true }) contenido!: ElementRef<HTMLElement>;

  private ctx?: gsap.Context;
  private mm?: gsap.MatchMedia;

  /** Pantalla en vertical (móvil): se encuadra la zona del texto en lugar de toda la escena. */
  vertical = false;
  private readonly consultaVertical = window.matchMedia('(max-aspect-ratio: 3/4)');
  private readonly alCambiarOrientacion = (e: MediaQueryListEvent) => this.zone.run(() => {
    this.vertical = e.matches;
    ScrollTrigger.refresh();
  });

  readonly ventajas = [
    { icono: 'bi-people', titulo: 'Cartera de clientes', texto: 'Cada asesor gestiona sus clientes con DNI, teléfono, IBAN y fecha de contrato validados.' },
    { icono: 'bi-lightning-charge', titulo: 'Puntos de suministro', texto: 'Varios CUPS por cliente con su comercializadora, potencia y estado: activo, pendiente o K.O.' },
    { icono: 'bi-headset', titulo: 'Equipo comercial', texto: 'Roles de empleado, jefe de equipo y administrador para organizar la asesoría.' },
  ];

  constructor(public authService: AuthService, private zone: NgZone) {
    this.vertical = this.consultaVertical.matches;
    this.consultaVertical.addEventListener('change', this.alCambiarOrientacion);
  }

  ngAfterViewInit(): void {
    // Las animaciones de scroll no necesitan detección de cambios de Angular
    this.zone.runOutsideAngular(() => {
      this.ctx = gsap.context(() => {
        this.mm = gsap.matchMedia();
        // Solo se anima el parallax si el usuario no ha pedido reducir el movimiento
        this.mm.add('(prefers-reduced-motion: no-preference)', () => {
          gsap.timeline({
            scrollTrigger: { trigger: this.hero.nativeElement, start: 'top top', end: 'bottom bottom', scrub: 1 }
          })
            .fromTo('.sky', { y: 0 }, { y: -200 }, 0)
            .fromTo('.cloud1', { y: 100 }, { y: -800 }, 0)
            .fromTo('.cloud2', { y: -150 }, { y: -500 }, 0)
            .fromTo('.cloud3', { y: -50 }, { y: -650 }, 0)
            .fromTo('.mountBg', { y: -10 }, { y: -100 }, 0)
            .fromTo('.mountMg', { y: -30 }, { y: -250 }, 0)
            .fromTo('.mountFg', { y: -50 }, { y: -600 }, 0);
        });
      }, this.hero.nativeElement);
    });
  }

  flechaEncima(): void {
    gsap.to(this.hero.nativeElement.querySelector('.arrow'), { y: 10, duration: 0.8, ease: 'back.inOut(3)', overwrite: 'auto' });
  }

  flechaFuera(): void {
    gsap.to(this.hero.nativeElement.querySelector('.arrow'), { y: 0, duration: 0.5, ease: 'power3.out', overwrite: 'auto' });
  }

  /** Recorre la escena hasta el final y baja al contenido. */
  bajar(): void {
    const reducir = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    gsap.to(window, {
      scrollTo: { y: this.contenido.nativeElement, offsetY: 70 },
      duration: reducir ? 0 : 1.5,
      ease: 'power1.inOut',
    });
  }

  ngOnDestroy(): void {
    this.consultaVertical.removeEventListener('change', this.alCambiarOrientacion);
    // Elimina los ScrollTrigger al salir de la página (si no, seguirían escuchando el scroll)
    this.mm?.revert();
    this.ctx?.revert();
  }
}
