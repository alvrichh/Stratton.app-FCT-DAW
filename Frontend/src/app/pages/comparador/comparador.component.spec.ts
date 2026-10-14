import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ComparadorComponent } from './comparador.component';
import { ResultadoComparativa } from './comparador.service';

describe('ComparadorComponent', () => {
  let component: ComparadorComponent;
  let fixture: ComponentFixture<ComparadorComponent>;
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComparadorComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(ComparadorComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    http.expectOne('/api/v1/comparador/oferta').flush({
      nombre: 'Tarifa Stratton', precioEnergia: 0.1, precioPotenciaP1: 0.09, precioPotenciaP2: 0.09,
    });
  });

  afterEach(() => http.verify());

  it('empieza en el paso de subir la factura y explica que se lee en el navegador', () => {
    fixture.detectChanges();
    expect(component.paso).toBe('subir');
    expect(fixture.nativeElement.textContent).toContain('no se sube a ningún servidor');
  });

  it('si faltan datos tras leer la factura avisa y marca los campos', async () => {
    component['mostrarLeidos']({ dias: 30, potenciaP1Kw: 3.45, potenciaP2Kw: 3.45 });
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    expect(component.paso).toBe('revisar');
    expect(component.aviso).toContain('faltan 8 datos');
    expect(fixture.nativeElement.querySelector('#c1').classList).toContain('is-invalid');
    expect(fixture.nativeElement.querySelector('#dias').classList).not.toContain('is-invalid');
  });

  it('con precio único copia el precio de punta a llano y valle y calcula', () => {
    component.manual();
    Object.assign(component.datos, {
      dias: 56, potenciaP1Kw: 5.75, potenciaP2Kw: 5.75, precioPotenciaP1: 0.1, precioPotenciaP2: 0.01,
      consumoP1Kwh: 335, consumoP2Kwh: 363, consumoP3Kwh: 431, precioEnergiaP1: 0.19,
    });
    component.calcular();

    const peticion = http.expectOne('/api/v1/comparador/calcular');
    expect(peticion.request.body.precioEnergiaP2).toBe(0.19);
    expect(peticion.request.body.precioEnergiaP3).toBe(0.19);

    const desglose = { potencia: 1, energia: 1, bonoSocial: 0, impuestoElectrico: 0, alquilerContador: 0, iva: 0, total: 2 };
    const resultado: ResultadoComparativa = {
      actual: { ...desglose, total: 300 }, oferta: { ...desglose, total: 200 }, ahorro: 100, ahorroPorcentaje: 33.33,
      ahorroAnual: 650, consumoTotalKwh: 1129, dias: 56, totalFacturaOriginal: null, nombreOferta: 'Tarifa Stratton',
      precioEnergiaOferta: 0.1,
    };
    peticion.flush(resultado);
    fixture.detectChanges();

    expect(component.paso).toBe('resultado');
    expect(fixture.nativeElement.textContent).toContain('te ahorrarías');
    expect(component.enlaceWhatsapp).toContain('wa.me/34679556404');
  });
});
