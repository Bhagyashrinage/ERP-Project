import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reportandanalytics } from './reportandanalytics';

describe('Reportandanalytics', () => {
  let component: Reportandanalytics;
  let fixture: ComponentFixture<Reportandanalytics>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reportandanalytics]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Reportandanalytics);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
