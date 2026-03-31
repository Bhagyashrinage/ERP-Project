import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Rawmaterials } from './rawmaterials';

describe('Rawmaterials', () => {
  let component: Rawmaterials;
  let fixture: ComponentFixture<Rawmaterials>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Rawmaterials]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Rawmaterials);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
