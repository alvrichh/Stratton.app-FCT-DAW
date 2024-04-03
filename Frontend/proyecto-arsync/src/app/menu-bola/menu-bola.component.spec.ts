import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuBolaComponent } from './menu-bola.component';

describe('MenuBolaComponent', () => {
  let component: MenuBolaComponent;
  let fixture: ComponentFixture<MenuBolaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuBolaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MenuBolaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
