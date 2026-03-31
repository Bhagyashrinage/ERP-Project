import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LOIComponent } from './loi';

describe('LOIComponent (standalone)', () => {
  let component: LOIComponent;
  let fixture: ComponentFixture<LOIComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // LOIComponent is standalone so it should go in imports
      imports: [LOIComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(LOIComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
