import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { RegistrarClienteComponent } from './registrar-cliente.component';

describe('RegistrarClienteComponent', () => {
  let component: RegistrarClienteComponent;
  let fixture: ComponentFixture<RegistrarClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrarClienteComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(RegistrarClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
