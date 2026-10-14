import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth.service';
import { environment } from '../../environments/environment';

/**
 * Añade la cabecera "Authorization: Bearer <token>" a todas las peticiones a la API
 * y cierra la sesión si el backend responde 401 (token caducado o inválido).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  const esApi = req.url.startsWith(environment.apiUrl) || req.url.startsWith('/api');
  const esLogin = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  const peticion = token && esApi && !esLogin
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && esApi && !esLogin) {
        authService.logout();
        router.navigate(['/login'], { queryParams: { expirada: 1 } });
      }
      return throwError(() => error);
    })
  );
};
