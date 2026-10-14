import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { ScrollToPlugin } from 'gsap/ScrollToPlugin';
import { AuthService } from '../../auth.service';

gsap.registerPlugin(ScrollTrigger, ScrollToPlugin);

/**
 * Caja aproximada del bloque de texto en coordenadas del SVG (alineado a la derecha en x = 800).
 * Se usa hasta que el navegador puede medirlo de verdad con getBBox() (ver medirTexto()).
 */
const TEXTO_APROX = { x: 370, y: 95, width: 430, height: 175 };
/** Centro horizontal del encuadre estrecho que se usa en pantallas verticales. */
const CENTRO_X_VERTICAL = 585;

/**
 * Página de bienvenida con la escena de parallax de montañas y nubes y el texto
 * "stratton global / stratton app" enmascarado.
 *
 * Adaptada de https://codepen.io/alvrichh/pen/XWwVBmG (recreación de la escena de
 * Unfold, https://dribbble.com/unfold).
 *
 * - La escena va en un bloque "sticky" dentro de la sección .hero: se queda fija mientras
 *   se recorre el parallax y, al terminar, el contenido entra por encima como un telón.
 * - El texto se mantiene siempre en el centro de la pantalla: se calcula su posición según
 *   el tamaño de la ventana y las montañas y nubes se desplazan hacia abajo lo necesario
 *   para no taparlo (desplazamiento {@link desplazamiento}).
 */
@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('hero', { static: true }) hero!: ElementRef<HTMLElement>;
  @ViewChild('escena', { static: true }) escena!: ElementRef<HTMLElement>;
  @ViewChild('contenido', { static: true }) contenido!: ElementRef<HTMLElement>;
  @ViewChild('textoBase', { static: true }) textoBase!: ElementRef<SVGGElement>;

  private ctx?: gsap.Context;
  private mm?: gsap.MatchMedia;
  private readonly alRedimensionar = () => this.zone.run(() => this.calcularEncuadre(true));

  /** Pantalla en vertical (móvil): se encuadra la zona del texto en lugar de toda la escena. */
  vertical = false;
  /** Transformación que centra (y en móvil reduce) el bloque de texto. */
  transformTexto = '';
  /** Cuánto se bajan montañas y nubes (en unidades del SVG) para dejar sitio al texto centrado. */
  desplazamiento = 0;

  readonly pasos = [
    { icono: 'bi-person-vcard', titulo: 'Registra al cliente', texto: 'DNI, teléfono, IBAN y fecha de nacimiento validados al momento, sin duplicados.' },
    { icono: 'bi-lightning-charge', titulo: 'Añade sus suministros', texto: 'Uno o varios CUPS por cliente, con su comercializadora y potencia contratada.' },
    { icono: 'bi-graph-up-arrow', titulo: 'Sigue cada contrato', texto: 'Ve de un vistazo qué está activo, qué está pendiente y qué ha salido K.O.' },
  ];

  readonly ventajas = [
    { icono: 'bi-people', titulo: 'Cartera de clientes', texto: 'Cada asesor gestiona sus clientes; búsqueda sin tildes y filtro por comercializadora.' },
    { icono: 'bi-shield-check', titulo: 'Acceso por roles', texto: 'Asesor, jefe de equipo y administrador: cada uno ve exactamente lo que le corresponde.' },
    { icono: 'bi-headset', titulo: 'Equipo comercial', texto: 'Alta de empleados, asignación de asesoría y reparto de clientes entre el equipo.' },
  ];

  readonly comercializadoras = [
    { nombre: 'Iberdrola', logo: 'assets/images/iberdrola.png' },
    { nombre: 'Endesa', logo: 'assets/images/endesa.png' },
    { nombre: 'Naturgy', logo: 'assets/images/naturgy.png' },
  ];

  constructor(public authService: AuthService, private zone: NgZone) {}

  ngOnInit(): void {
    // Antes del primer render, para no cambiar los bindings después de comprobarlos
    this.calcularEncuadre(false);
  }

  ngAfterViewInit(): void {
    window.addEventListener('resize', this.alRedimensionar, { passive: true });
    // Con la fuente Montserrat ya cargada se mide el texto real y se vuelve a centrar
    document.fonts?.ready.then(() => this.zone.run(() => this.calcularEncuadre(true)));

    // Las animaciones de scroll no necesitan detección de cambios de Angular
    this.zone.runOutsideAngular(() => {
      this.ctx = gsap.context(() => {
        this.mm = gsap.matchMedia();
        // Solo se anima si el usuario no ha pedido reducir el movimiento
        this.mm.add('(prefers-reduced-motion: no-preference)', () => {
          const s = () => this.desplazamiento;
          // El parallax dura lo que mide la sección menos la pantalla del telón (ver CSS)
          gsap.timeline({
            scrollTrigger: {
              trigger: this.hero.nativeElement,
              start: 'top top',
              end: () => `+=${this.hero.nativeElement.offsetHeight - 2 * window.innerHeight}`,
              scrub: 1,
              invalidateOnRefresh: true,
            }
          })
            .fromTo('.sky', { y: 0 }, { y: -200 }, 0)
            .fromTo('.cloud1', { y: () => 100 + s() }, { y: -800 }, 0)
            .fromTo('.cloud2', { y: () => -150 + s() }, { y: -500 }, 0)
            .fromTo('.cloud3', { y: () => -50 + s() }, { y: -650 }, 0)
            .fromTo('.mountBg', { y: () => -10 + s() }, { y: -100 }, 0)
            .fromTo('.mountMg', { y: () => -30 + s() }, { y: -250 }, 0)
            .fromTo('.mountFg', { y: () => -50 + s() }, { y: -600 }, 0);

          // Aparición suave del contenido al llegar a cada bloque
          gsap.utils.toArray<HTMLElement>('.revelar').forEach(el => {
            gsap.from(el.children, {
              opacity: 0,
              y: 24,
              duration: 0.6,
              ease: 'power2.out',
              stagger: 0.08,
              scrollTrigger: { trigger: el, start: 'top 85%', once: true },
            });
          });
        });

        // Con movimiento reducido la escena se muestra en su estado inicial, sin parallax
        this.mm.add('(prefers-reduced-motion: reduce)', () => {
          gsap.set('.cloud1', { y: () => 100 + this.desplazamiento });
          gsap.set(['.mountBg', '.mountMg', '.mountFg', '.cloud2', '.cloud3'], { y: () => this.desplazamiento });
        });
      }, this.hero.nativeElement.parentElement ?? this.hero.nativeElement);
    });
  }

  /**
   * Calcula el encuadre del SVG y la posición del texto para que quede centrado en la pantalla.
   * Horizontal: viewBox completo (1200x800) recortado ("slice") y alineado arriba.
   * Vertical: viewBox estrecho centrado en el texto, y el texto se reduce si no cabe.
   */
  private calcularEncuadre(refrescar: boolean): void {
    const ancho = this.escena.nativeElement.clientWidth || window.innerWidth;
    const alto = this.escena.nativeElement.clientHeight || window.innerHeight;
    this.vertical = ancho / alto < 0.75;

    const vbAncho = this.vertical ? 460 : 1200;
    const escala = Math.max(ancho / vbAncho, alto / 800);
    const anchoVisible = ancho / escala;
    const texto = this.medirTexto();
    const cx = texto.x + texto.width / 2;
    const cy = texto.y + texto.height / 2;

    // Punto del SVG que cae en el centro de la pantalla (encuadre alineado arriba y centrado en horizontal)
    const destinoX = this.vertical ? CENTRO_X_VERTICAL : 600;
    const destinoY = Math.min(alto / 2 / escala, 560);
    const k = Math.min(1, (anchoVisible - 30) / texto.width);

    this.transformTexto = `translate(${destinoX} ${destinoY.toFixed(1)}) scale(${k.toFixed(3)}) translate(${(-cx).toFixed(1)} ${(-cy).toFixed(1)})`;
    this.desplazamiento = Math.max(0, Math.round(destinoY - cy));

    if (refrescar) {
      ScrollTrigger.refresh();
    }
  }

  /** Caja real del texto "stratton / global" (sin la transformación aplicada), o la aproximada. */
  private medirTexto(): { x: number; y: number; width: number; height: number } {
    try {
      const caja = this.textoBase?.nativeElement.getBBox();
      if (caja && caja.width > 0 && caja.height > 0) {
        return caja;
      }
    } catch {
      // getBBox falla si el SVG aún no se ha pintado
    }
    return TEXTO_APROX;
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
    const cabecera = document.querySelector<HTMLElement>('app-header header')?.offsetHeight ?? 0;
    gsap.to(window, {
      scrollTo: { y: this.contenido.nativeElement, offsetY: cabecera },
      duration: reducir ? 0 : 2,
      ease: 'power1.inOut',
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this.alRedimensionar);
    // Elimina los ScrollTrigger al salir de la página (si no, seguirían escuchando el scroll)
    this.mm?.revert();
    this.ctx?.revert();
  }
}
