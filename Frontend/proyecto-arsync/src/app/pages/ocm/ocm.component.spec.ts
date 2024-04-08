import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OCMComponent } from './ocm.component';

describe('OCMComponent', () => {
  let component: OCMComponent;
  let fixture: ComponentFixture<OCMComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OCMComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OCMComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
