import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EsperaServidorComponent } from './espera-servidor.component';

describe('EsperaServidorComponent', () => {
  let component: EsperaServidorComponent;
  let fixture: ComponentFixture<EsperaServidorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EsperaServidorComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(EsperaServidorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('no se muestra si el servidor no es gratuito', () => {
    expect(fixture.nativeElement.querySelector('.velo')).toBeNull();
  });
});
