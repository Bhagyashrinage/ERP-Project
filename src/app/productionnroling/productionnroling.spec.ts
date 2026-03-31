import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Productionnroling } from './productionnroling';

describe('Productionnroling', () => {
  let component: Productionnroling;
  let fixture: ComponentFixture<Productionnroling>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        Productionnroling,
        HttpClientTestingModule // provides HttpClient for component
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Productionnroling);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
