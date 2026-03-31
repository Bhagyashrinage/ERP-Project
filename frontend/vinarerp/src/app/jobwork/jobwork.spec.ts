import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Jobwork } from './jobwork';

describe('Jobwork', () => {
  let component: Jobwork;
  let fixture: ComponentFixture<Jobwork>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Jobwork]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Jobwork);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
