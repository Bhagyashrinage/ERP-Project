import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PurchaseOrder } from './purchase-order';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('PurchaseOrder', () => {
  let component: PurchaseOrder;
  let fixture: ComponentFixture<PurchaseOrder>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchaseOrder, HttpClientTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PurchaseOrder);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default tab as create', () => {
    expect(component.activeTab).toBe('create');
  });

  it('should switch to list tab', () => {
    component.switchTab('list');
    expect(component.activeTab).toBe('list');
  });

  it('should add and remove elements', () => {
    const initialLength = component.po.elements.length;
    component.addElement();
    expect(component.po.elements.length).toBe(initialLength + 1);
    component.removeElement(0);
    expect(component.po.elements.length).toBe(initialLength);
  });
});
