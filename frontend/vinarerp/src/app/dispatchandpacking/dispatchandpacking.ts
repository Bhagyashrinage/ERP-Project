import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dispatchandpacking',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './dispatchandpacking.html',
  styleUrls: ['./dispatchandpacking.css']
})
export class DispatchandpackingComponent implements OnInit {

  packedItems: any[] = [];
  packedItemsAll: any[] = [];
  dispatches: any[] = [];
  dispatchesAll: any[] = [];
  // Pagination for packed items
  packedPageSize: number = 10;
  packedCurrentPage: number = 1;
  packedTotalPages: number = 1;
  displayedPackedItems: any[] = [];
  // Pagination for dispatch list
  dispatchPageSize: number = 10;
  dispatchCurrentPage: number = 1;
  dispatchTotalPages: number = 1;
  displayedDispatches: any[] = [];
  selectedPacking: any = null;

  showDispatchForm = false;
  isEditMode = false;
  loadingPackedItems = false;
  loadingDispatches = false;
  savingDispatch = false;
  message = '';

  // Filters for UI
  packedSearch = '';
  packedStatus = 'All Status';

  dispatchSearch = '';
  dispatchStatus = 'All Status';
  dispatchSortOldest = false;

  dispatch = this.createEmptyDispatch();

  private apiUrl = 'http://localhost:8080/api/dispatch';
  private packingApiUrl = 'http://localhost:8080/api/packing';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadPackedItems();
    this.loadDispatches();
    this.prefillNextChallan();
  }

  /** ------------------ API Methods ------------------ **/

  loadPackedItems() {
    this.loadingPackedItems = true;
    this.http.get<any[]>(`${this.packingApiUrl}/packed`).subscribe({
      next: data => {
        // keep a master copy and a filtered view
        this.packedItemsAll = (data || []).slice().sort((a, b) => this.parsePackingDate(b.packingDate).getTime() - this.parsePackingDate(a.packingDate).getTime());
        this.applyPackedFilters();
        this.loadingPackedItems = false;
      },
      error: err => {
        console.error(err);
        this.loadingPackedItems = false;
        this.message = 'Failed to load packed items.';
      }
    });
  }

  loadDispatches() {
    this.loadingDispatches = true;
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: data => {
        this.dispatchesAll = (data || []).slice().sort((a, b) => new Date(b.dispatchDate).getTime() - new Date(a.dispatchDate).getTime());
        this.applyDispatchFilters();
        this.loadingDispatches = false;
      },
      error: err => {
        console.error(err);
        this.loadingDispatches = false;
        this.message = 'Failed to load dispatches.';
      }
    });
  }

  prefillNextChallan() {
    this.http.get<any>(`${this.apiUrl}/next-challan`).subscribe({
      next: res => { if (res?.number) this.dispatch.challanNo = res.number; },
      error: err => console.error('Failed to fetch next challan number', err)
    });
  }

  /** ------------------ Dispatch Modal ------------------ **/

  openDispatchFormFromPacking(packing: any) {
    this.selectedPacking = packing;
    this.isEditMode = false;
    this.showDispatchForm = true;

    this.dispatch = {
      ...this.createEmptyDispatch(),
      poNo: packing.poNo,
      customer: packing.customer,
      section: packing.gradeSection,
      qtyIssued: packing.qtyInMt,
      challanNo: packing.challanNo || ''
    };

    if (!this.dispatch.challanNo) this.prefillNextChallan();
  }

  editDispatch(dispatch: any) {
    this.isEditMode = true;
    this.showDispatchForm = true;
    this.selectedPacking = null;

    if (dispatch.packing?.id) {
      this.http.get<any>(`${this.apiUrl}/from-packing/${dispatch.packing.id}`).subscribe({
        next: full => this.dispatch = { ...full },
        error: () => this.dispatch = { ...dispatch }
      });
    } else {
      this.dispatch = { ...dispatch };
    }
  }

  closeDispatchForm() {
    this.showDispatchForm = false;
    this.isEditMode = false;
    this.selectedPacking = null;
    this.resetForm();
  }

  resetForm() {
    this.dispatch = this.createEmptyDispatch();
    this.prefillNextChallan();
  }

  /** ------------------ Save / Update ------------------ **/

  saveDispatch(form: NgForm) {
    if (!form.valid) {
      this.message = 'Please fill all required fields (Vehicle, From Location, Destination).';
      return;
    }

    this.savingDispatch = true;

    if (this.isEditMode && this.dispatch.id) {
      this.http.put(`${this.apiUrl}/${this.dispatch.id}`, this.dispatch).subscribe({
        next: () => {
          this.message = 'Dispatch updated successfully!';
          this.loadDispatches();
          this.closeDispatchForm();
          this.savingDispatch = false;
        },
        error: err => { 
          console.error(err); 
          this.message = 'Failed to update dispatch.'; 
          this.savingDispatch = false;
        }
      });
    } else {
      if (!this.selectedPacking) {
        this.message = 'No packing selected!';
        this.savingDispatch = false;
        return;
      }

      const url = `${this.apiUrl}/from-packing/${this.selectedPacking.id}`;
      this.http.post<any>(url, this.dispatch).subscribe({
        next: res => {
          this.message = res.message || 'Dispatch created successfully!';
          alert(`✅ ${res.message}\nDispatch ID: ${res.dispatchId}`);

          // Remove dispatched item from packedItems
          this.packedItems = this.packedItems.filter(
            item => !(item.poNo === this.selectedPacking.poNo && item.heatNo === this.selectedPacking.heatNo)
          );

          this.loadDispatches();
          this.closeDispatchForm();
          this.savingDispatch = false;
        },
        error: err => { 
          console.error(err); 
          this.message = err?.error?.message || 'Failed to create dispatch.'; 
          this.savingDispatch = false;
        }
      });
    }
  }

  /** ------------------ Update Status ------------------ **/

  updateDispatchStatus(dispatch: any, newStatus: string) {
    if (!confirm(`Update dispatch ${dispatch.dispatchId} to ${newStatus}?`)) return;
    const updated = { ...dispatch, status: newStatus };
    this.http.put(`${this.apiUrl}/${dispatch.id}`, updated).subscribe({
      next: () => { 
        this.message = `Dispatch status updated to ${newStatus}!`; 
        this.loadDispatches(); 
      },
      error: err => { console.error(err); this.message = 'Failed to update status.'; }
    });
  }

  getBadgeClass(status: string): string {
    switch(status) {
      case 'Pending': return 'bg-warning text-dark';
      case 'In Transit': return 'bg-info text-dark';
      case 'Delivered': return 'bg-success';
      default: return 'bg-secondary';
    }
  }

  /** ------------------ Utilities ------------------ **/

  private createEmptyDispatch() {
    return {
      id: null as number | null,
      dispatchId: `DISP-${Date.now().toString().slice(-6)}`,
      poNo: '',
      customer: '',
      section: '',
      qtyIssued: null as number | null,
      challanNo: '',
      vehicleNo: '',
      fromLocation: '',
      destination: '',
      dispatchDate: new Date().toISOString().slice(0,10),
      status: 'Pending'
    };
  }

  private parsePackingDate(str: string): Date {
    if (!str) return new Date(0);
    if (/^\d{4}-\d{2}-\d{2}/.test(str)) return new Date(str);
    const [day, month, year] = str.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  trackByPoHeat(index: number, item: any) {
    return item.poNo + '-' + item.heatNo;
  }

  trackByDispatchId(index: number, item: any) {
    return item.dispatchId;
  }

  /** ------------------ Filtering Helpers ------------------ **/
  applyPackedFilters() {
    const q = (this.packedSearch || '').toString().trim().toLowerCase();
    this.packedItems = this.packedItemsAll.filter(item => {
      let ok = true;
      if (q) {
        ok = [item.poNo, item.customer, item.challanNo, item.heatNo]
          .some(f => (f || '').toString().toLowerCase().includes(q));
      }
      if (this.packedStatus && this.packedStatus !== 'All Status') {
        ok = ok && (item.status === this.packedStatus);
      }
      return ok;
    });
    this.packedCurrentPage = 1;
    this.updatePaginationPacked();
  }

  applyDispatchFilters() {
    const q = (this.dispatchSearch || '').toString().trim().toLowerCase();
    this.dispatches = this.dispatchesAll.filter(d => {
      let ok = true;
      if (q) {
        ok = [d.dispatchId, d.customer, d.poNo].some(f => (f || '').toString().toLowerCase().includes(q));
      }
      if (this.dispatchStatus && this.dispatchStatus !== 'All Status') {
        ok = ok && (d.status === this.dispatchStatus);
      }
      return ok;
    }).slice();

    // sort by date depending on sort flag
    this.dispatches.sort((a, b) => {
      const ta = new Date(a.dispatchDate).getTime();
      const tb = new Date(b.dispatchDate).getTime();
      return this.dispatchSortOldest ? ta - tb : tb - ta;
    });

    this.dispatchCurrentPage = 1;
    this.updatePaginationDispatch();
  }

  toggleDispatchSort() {
    this.dispatchSortOldest = !this.dispatchSortOldest;
    this.applyDispatchFilters();
  }

  // Packed items pagination
  updatePaginationPacked() {
    const total = this.packedItems.length;
    this.packedTotalPages = Math.max(1, Math.ceil(total / this.packedPageSize));
    if (this.packedCurrentPage > this.packedTotalPages) this.packedCurrentPage = this.packedTotalPages;
    const start = (this.packedCurrentPage - 1) * this.packedPageSize;
    this.displayedPackedItems = this.packedItems.slice(start, start + this.packedPageSize);
  }

  changePagePacked(page: number) {
    if (page < 1) page = 1;
    if (page > this.packedTotalPages) page = this.packedTotalPages;
    this.packedCurrentPage = page;
    this.updatePaginationPacked();
  }

  setPackedPageSize(size: number) {
    this.packedPageSize = size;
    this.packedCurrentPage = 1;
    this.updatePaginationPacked();
  }

  // Dispatch list pagination
  updatePaginationDispatch() {
    const total = this.dispatches.length;
    this.dispatchTotalPages = Math.max(1, Math.ceil(total / this.dispatchPageSize));
    if (this.dispatchCurrentPage > this.dispatchTotalPages) this.dispatchCurrentPage = this.dispatchTotalPages;
    const start = (this.dispatchCurrentPage - 1) * this.dispatchPageSize;
    this.displayedDispatches = this.dispatches.slice(start, start + this.dispatchPageSize);
  }

  changePageDispatch(page: number) {
    if (page < 1) page = 1;
    if (page > this.dispatchTotalPages) page = this.dispatchTotalPages;
    this.dispatchCurrentPage = page;
    this.updatePaginationDispatch();
  }

  setDispatchPageSize(size: number) {
    this.dispatchPageSize = size;
    this.dispatchCurrentPage = 1;
    this.updatePaginationDispatch();
  }
}
