import { Injectable, NgZone, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export type EstadoServidor = 'comprobando' | 'despertando' | 'listo';

/** Tiempo sin respuesta a partir del cual se muestra la pantalla de espera. */
const UMBRAL_ESPERA_MS = 1500;
/** Pausa entre comprobaciones mientras el servidor despierta. */
const REINTENTO_MS = 3000;
/** Tiempo máximo de cada comprobación (Render retiene la petición mientras arranca). */
const TIMEOUT_PETICION_MS = 20000;

/**
 * Comprueba si el backend está despierto. En el plan gratuito de Render el backend se duerme
 * tras 15 minutos sin uso y tarda un par de minutos en arrancar: mientras tanto el estado
 * es "despertando" y `segundos` lleva la cuenta del tiempo de espera.
 */
@Injectable({ providedIn: 'root' })
export class ServidorService {
  readonly estado = signal<EstadoServidor>(environment.servidorGratuito ? 'comprobando' : 'listo');
  readonly segundos = signal(0);

  private inicio = 0;
  private reloj?: ReturnType<typeof setInterval>;

  constructor(private zone: NgZone) {
    if (environment.servidorGratuito) {
      this.vigilar();
    }
  }

  private vigilar(): void {
    this.inicio = Date.now();
    const aviso = setTimeout(() => {
      if (this.estado() === 'comprobando') {
        this.estado.set('despertando');
        this.reloj = setInterval(() => this.segundos.set(Math.floor((Date.now() - this.inicio) / 1000)), 1000);
      }
    }, UMBRAL_ESPERA_MS);

    const intentar = async (): Promise<void> => {
      if (await this.responde()) {
        clearTimeout(aviso);
        clearInterval(this.reloj);
        this.zone.run(() => this.estado.set('listo'));
        return;
      }
      setTimeout(intentar, REINTENTO_MS);
    };
    intentar();
  }

  /** true si el backend contesta a /v1/salud con JSON válido (no con la página de espera de Render). */
  private async responde(): Promise<boolean> {
    const control = new AbortController();
    const corte = setTimeout(() => control.abort(), TIMEOUT_PETICION_MS);
    try {
      const res = await fetch(`${environment.apiUrl}/v1/salud`, { signal: control.signal, cache: 'no-store' });
      return res.ok && (await res.json())?.estado === 'ok';
    } catch {
      return false;
    } finally {
      clearTimeout(corte);
    }
  }
}
