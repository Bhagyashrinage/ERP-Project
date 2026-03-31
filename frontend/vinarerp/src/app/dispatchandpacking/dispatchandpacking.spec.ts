import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { DispatchandpackingComponent } from './dispatchandpacking';

describe('DispatchandpackingComponent', () => {
  let component: DispatchandpackingComponent;
  let fixture: ComponentFixture<DispatchandpackingComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DispatchandpackingComponent, HttpClientTestingModule, RouterTestingModule, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(DispatchandpackingComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load packed items and dispatches on init', fakeAsync(() => {
    const mockPacked = [{ id: 1, poNo: 'PO1', heatNo: 'H1', qtyInMt: 10, customer: 'ABC', gradeSection: 'A1', challanNo: '' }];
    const mockDispatches = [{ id: 1, dispatchId: 'DISP-001', poNo: 'PO2', status: 'Pending' }];

    // Packed items request
    const reqPacked = httpMock.expectOne(`${component['packingApiUrl']}/packed`);
    expect(reqPacked.request.method).toBe('GET');
    reqPacked.flush(mockPacked);

    // Dispatches request
    const reqDispatch = httpMock.expectOne(component['apiUrl']);
    expect(reqDispatch.request.method).toBe('GET');
    reqDispatch.flush(mockDispatches);

    tick();
    fixture.detectChanges();

    expect(component.packedItems.length).toBe(1);
    expect(component.dispatches.length).toBe(1);
  }));

  it('should open dispatch modal from packing', () => {
    const packingItem = { id: 1, poNo: 'PO123', customer: 'ABC', gradeSection: 'A1', qtyInMt: 10 };
    component.openDispatchFormFromPacking(packingItem);

    expect(component.showDispatchForm).toBeTrue();
    expect(component.isEditMode).toBeFalse();
    expect(component.dispatch.poNo).toBe('PO123');
    expect(component.selectedPacking).toEqual(packingItem);
  });

  it('should remove packed item after creating dispatch', () => {
    const packingItem = { id: 1, poNo: 'PO123', heatNo: 'H001', qtyInMt: 10, customer: 'ABC', gradeSection: 'A1', challanNo: 'CH001' };
    component.packedItems = [packingItem];
    component.selectedPacking = packingItem;
    component.dispatch = { ...((component as any).createEmptyDispatch()), poNo: 'PO123', customer: 'ABC', section: 'A1', qtyIssued: 10 };

    spyOn(window, 'alert'); // prevent alert popup

    component['http'].post = jasmine.createSpy().and.returnValue({
      subscribe: (obj: any) => obj.next({ message: 'Success', dispatchId: 'DISP-001' })
    } as any);

    component.saveDispatch();

    expect(component.packedItems.length).toBe(0);
    expect(component.showDispatchForm).toBeFalse();
    expect(component.selectedPacking).toBeNull();
  });

  it('should update dispatch status', () => {
    const dispatchItem = { id: 1, dispatchId: 'DISP-001', status: 'Pending' };
    component.dispatches = [dispatchItem];

    component['http'].put = jasmine.createSpy().and.returnValue({
      subscribe: (obj: any) => obj.next()
    } as any);

    spyOn(window, 'confirm').and.returnValue(true);

    component.updateDispatchStatus(dispatchItem, 'In Transit');

    expect(component['http'].put).toHaveBeenCalledWith(`${component['apiUrl']}/1`, jasmine.objectContaining({ status: 'In Transit' }));
  });

  it('should open and close modal', () => {
    component.showDispatchForm = false;
    component.closeDispatchForm();
    expect(component.showDispatchForm).toBeFalse();
    expect(component.isEditMode).toBeFalse();
    expect(component.selectedPacking).toBeNull();
  });

  it('should prefill next challan number', fakeAsync(() => {
    component.prefillNextChallan();
    const req = httpMock.expectOne(`${component['apiUrl']}/next-challan`);
    expect(req.request.method).toBe('GET');
    req.flush({ number: 'CH-1001' });
    tick();
    expect(component.dispatch.challanNo).toBe('CH-1001');
  }));
});
