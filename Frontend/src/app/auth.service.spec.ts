import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../environments/environment';

/** Token de prueba firmado con HS256 (no válido en el backend, solo para decodificar en el cliente). */
function tokenDePrueba(claims: Record<string, unknown>): string {
  const b64 = (o: unknown) => btoa(JSON.stringify(o)).replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
  return `${b64({ alg: 'HS256', typ: 'JWT' })}.${b64(claims)}.firma`;
}

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('sin token no hay sesión', () => {
    expect(service.isLoggedIn()).toBeFalse();
    expect(service.isAdmin()).toBeFalse();
    expect(service.getUserRoles()).toEqual([]);
  });

  it('login guarda el token y expone los roles e id del empleado', () => {
    const exp = Math.floor(Date.now() / 1000) + 3600;
    const token = tokenDePrueba({ sub: 'admin', id: 1, role: 'ADMIN', roles: ['ADMIN'], nombre: 'Admin Stratton', exp });

    service.login('admin', 'admin').subscribe();
    const req = http.expectOne(`${environment.apiUrl}/v1/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token, role: 'ADMIN', roles: ['ADMIN'], id: 1, username: 'admin', nombre: 'Admin Stratton' });

    expect(service.getToken()).toBe(token);
    expect(service.isLoggedIn()).toBeTrue();
    expect(service.isAdmin()).toBeTrue();
    expect(service.isJefe()).toBeTrue();
    expect(service.getEmpleadoId()).toBe(1);
    expect(service.getNombre()).toBe('Admin Stratton');
    expect(service.rutaInicial()).toBe('/empleados');
  });

  it('un token caducado no se considera sesión válida', () => {
    const exp = Math.floor(Date.now() / 1000) - 10;
    service.saveToken(tokenDePrueba({ sub: 'usuario', id: 3, role: 'USER', roles: ['USER'], exp }));
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('logout elimina el token', () => {
    service.saveToken(tokenDePrueba({ sub: 'usuario', exp: Math.floor(Date.now() / 1000) + 60 }));
    service.logout();
    expect(service.getToken()).toBeNull();
  });
});
