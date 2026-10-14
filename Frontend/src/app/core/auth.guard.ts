import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';

/** Solo usuarios autenticados. Si no hay sesión, redirige al login. */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) {
    return true;
  }
  return redirigirALogin(auth, router, state.url);
};

/** Redirige al login conservando la URL solicitada y avisando si la sesión ha caducado. */
function redirigirALogin(auth: AuthService, router: Router, returnUrl: string) {
  const queryParams: Record<string, string> = { returnUrl };
  if (auth.tokenCaducado()) {
    auth.logout();
    queryParams['expirada'] = '1';
  }
  return router.createUrlTree(['/login'], { queryParams });
}

/** Solo administradores. */
export const adminGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.isLoggedIn()) {
    return redirigirALogin(auth, router, state.url);
  }
  return auth.isAdmin() ? true : router.createUrlTree(['/clientes']);
};

/** Páginas de login/registro: si ya hay sesión, va directo a la aplicación. */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isLoggedIn() ? router.createUrlTree([auth.rutaInicial()]) : true;
};
