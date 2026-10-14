import Swal, { SweetAlertIcon } from 'sweetalert2';
import { HttpErrorResponse } from '@angular/common/http';

/** Pequeña fachada sobre SweetAlert2 para unificar los mensajes de la aplicación. */
export const Alertas = {
  exito(titulo: string, texto = ''): Promise<unknown> {
    return Swal.fire({ title: titulo, text: texto, icon: 'success', timer: 1800, showConfirmButton: false });
  },

  info(titulo: string, texto = ''): Promise<unknown> {
    return Swal.fire({ title: titulo, text: texto, icon: 'info' });
  },

  /** Muestra el mensaje de error devuelto por la API (o uno genérico). */
  error(error: unknown, titulo = 'Se ha producido un error'): Promise<unknown> {
    return Swal.fire({ title: titulo, html: Alertas.mensajeDe(error), icon: 'error' });
  },

  /** Diálogo de confirmación. Resuelve true si el usuario confirma. */
  async confirmar(titulo: string, texto: string, confirmar = 'Sí, continuar', icono: SweetAlertIcon = 'warning'): Promise<boolean> {
    const r = await Swal.fire({
      title: titulo,
      text: texto,
      icon: icono,
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: confirmar,
      cancelButtonText: 'No, cancelar',
    });
    return r.isConfirmed;
  },

  /** Escapa texto para insertarlo en HTML (SweetAlert2 no sanea la opción html). */
  escapar(texto: unknown): string {
    return String(texto ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c] as string));
  },

  /** Extrae un mensaje legible de un error HTTP (incluye los errores de validación por campo). */
  mensajeDe(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return 'No se puede conectar con el servidor. Comprueba que el backend está en marcha.';
      }
      const body = error.error;
      if (body && typeof body === 'object') {
        const campos = body.errors && typeof body.errors === 'object'
          ? Object.entries(body.errors as Record<string, string>).map(([k, v]) => `<li><b>${Alertas.escapar(k)}</b>: ${Alertas.escapar(v)}</li>`).join('')
          : '';
        const mensaje = body.message ? Alertas.escapar(body.message) : `Error ${error.status}`;
        return campos ? `${mensaje}<ul class="text-start">${campos}</ul>` : mensaje;
      }
      return `Error ${error.status}: ${error.statusText}`;
    }
    if (error instanceof Error) {
      return Alertas.escapar(error.message);
    }
    return 'Error desconocido';
  },
};
