import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { LandingComponent } from './landing.component';

describe('LandingComponent', () => {
  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [LandingComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('muestra la escena con el texto enmascarado y las llamadas a la acción para invitados', () => {
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('svg text').length).toBe(4);
    expect(el.querySelector('mask#m')).toBeTruthy();
    expect(el.querySelector('a[href="/login"]')).toBeTruthy();
    expect(el.querySelector('a[href="/register"]')).toBeTruthy();
    expect(el.querySelectorAll('.contenido .card').length).toBe(3);
  });

  it('elimina sus ScrollTrigger al destruirse', () => {
    fixture.destroy();
    expect(ScrollTrigger.getAll().length).toBe(0);
  });
});
