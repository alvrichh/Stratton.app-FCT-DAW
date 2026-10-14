import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../auth.service';
import { environment } from '../../environments/environment';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controller: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
      ]
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
    localStorage.clear();
  });

  afterEach(() => {
    controller.verify();
    localStorage.clear();
  });

  it('añade la cabecera Authorization a las peticiones a la API cuando hay token', () => {
    auth.saveToken('abc.def.ghi');
    http.get(`${environment.apiUrl}/v2/clientes`).subscribe();
    const req = controller.expectOne(`${environment.apiUrl}/v2/clientes`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer abc.def.ghi');
    req.flush([]);
  });

  it('no añade la cabecera al login', () => {
    auth.saveToken('abc.def.ghi');
    http.post(`${environment.apiUrl}/v1/auth/login`, {}).subscribe();
    const req = controller.expectOne(`${environment.apiUrl}/v1/auth/login`);
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('cierra la sesión si la API responde 401', () => {
    auth.saveToken('abc.def.ghi');
    http.get(`${environment.apiUrl}/v2/clientes`).subscribe({ error: () => {} });
    controller.expectOne(`${environment.apiUrl}/v2/clientes`).flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(auth.getToken()).toBeNull();
  });
});
