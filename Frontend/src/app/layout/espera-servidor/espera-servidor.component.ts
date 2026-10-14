import { Component, ElementRef, OnDestroy, ViewChild, computed, effect, signal } from '@angular/core';
import { ServidorService } from '../../core/servidor.service';

interface Captura {
  src: string;
  texto: string;
}

/** Duración aproximada del arranque del backend en el plan gratuito de Render. */
const ARRANQUE_ESTIMADO_S = 120;

/**
 * Pantalla de espera mientras el backend gratuito de Render despierta: enseña capturas de la
 * aplicación, el tiempo transcurrido y una barra de progreso estimada. Se puede minimizar a una
 * pastilla flotante para seguir viendo la presentación mientras tanto.
 */
@Component({
  selector: 'app-espera-servidor',
  standalone: true,
  templateUrl: './espera-servidor.component.html',
  styleUrl: './espera-servidor.component.css'
})
export class EsperaServidorComponent implements OnDestroy {
  @ViewChild('botonPrincipal') set botonPrincipal(boton: ElementRef<HTMLButtonElement> | undefined) {
    boton?.nativeElement.focus();
  }

  readonly capturas: Captura[] = [
    { src: 'assets/images/demo/clientes.webp', texto: 'Cartera de clientes con buscador y filtro por comercializadora' },
    { src: 'assets/images/demo/detalle.webp', texto: 'Ficha del cliente con todos sus puntos de suministro' },
    { src: 'assets/images/demo/nuevo.webp', texto: 'Alta de clientes junto a su primer punto de suministro' },
    { src: 'assets/images/demo/perfil.webp', texto: 'Panel personal con el estado de tus suministros' },
    { src: 'assets/images/demo/empleados.webp', texto: 'Gestión del equipo comercial por roles' },
    { src: 'assets/images/demo/inicio.webp', texto: 'Comparativa de las ofertas de cada comercializadora' },
  ];

  readonly actual = signal(0);
  readonly minimizado = signal(false);
  /** Mensaje de "listo" que se muestra un momento antes de cerrar la pantalla. */
  readonly recienListo = signal(false);

  readonly visible = computed(() => this.servidor.estado() === 'despertando' || this.recienListo());
  readonly tiempo = computed(() => {
    const s = this.servidor.segundos();
    return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
  });
  readonly progreso = computed(() => Math.min(95, Math.round((this.servidor.segundos() / ARRANQUE_ESTIMADO_S) * 100)));
  readonly mensaje = computed(() => {
    const s = this.servidor.segundos();
    if (s < 30) return 'Arrancando el contenedor…';
    if (s < 90) return 'Iniciando Spring Boot y conectando con la base de datos…';
    if (s < 150) return 'Ya casi está…';
    return 'Está tardando más de lo normal, pero sigue arrancando.';
  });

  private carrusel = setInterval(() => this.siguiente(), 4500);

  constructor(readonly servidor: ServidorService) {
    let estabaDespertando = false;
    effect(() => {
      const estado = this.servidor.estado();
      if (estado === 'despertando') {
        estabaDespertando = true;
      } else if (estado === 'listo' && estabaDespertando) {
        estabaDespertando = false;
        this.recienListo.set(true);
        clearInterval(this.carrusel);
        setTimeout(() => this.recienListo.set(false), this.minimizado() ? 3000 : 1500);
      }
    }, { allowSignalWrites: true });
  }

  siguiente(): void {
    this.actual.update(i => (i + 1) % this.capturas.length);
  }

  irA(i: number): void {
    this.actual.set(i);
    clearInterval(this.carrusel);
    this.carrusel = setInterval(() => this.siguiente(), 4500);
  }

  ngOnDestroy(): void {
    clearInterval(this.carrusel);
  }
}
