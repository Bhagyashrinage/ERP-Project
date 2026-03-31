import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-packing',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './packing.html',
  styleUrls: ['./packing.css']
})
export class PackingComponent implements OnInit {
  showPackingForm = false;
  isEditMode = false;
  packingItems: any[] = [];
  packingItemsAll: any[] = [];
  completedProductions: any[] = [];
  // Pagination for packing items
  packingPageSize: number = 10;
  packingCurrentPage: number = 1;
  packingTotalPages: number = 1;
  displayedPackingItems: any[] = [];
  // Pagination for completed productions
  compPageSize: number = 10;
  compCurrentPage: number = 1;
  compTotalPages: number = 1;
  displayedCompletedProductions: any[] = [];
  selectedProduction: any = null;
  loading = false;
  message = '';

  // Filters
  packingSearch = '';
  packingStatus = 'All Status';
  packingSortOldest = false;

  packing = {
    id: null as number | null,
    srNo: null as number | null,
    poNo: '',
    grade: '',
    colourCode: '',
    gradeSection: '',
    sectionWt: '',
    length: null as number | null,
    noOfPcs: null as number | null,
    qtyInMt: null as number | null,
    heatNo: '',
    challanQty: null as number | null,
    challanNo: '',
    customer: '',
    docNo: '',
    packingDate: new Date().toISOString().slice(0, 10),
    lorryNo: '',
    status: 'PACKED'
  };

  private readonly apiUrl = 'http://localhost:8080/api/packing';
  private readonly productionApiUrl = 'http://localhost:8080/api/rolling-plan';

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.loadPackingItems();
    this.loadCompletedProductions();
  }

  // ✅ Load all existing packing records
  loadPackingItems() {
    this.loading = true;
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (data) => {
        // keep master copy and filtered view
        this.packingItemsAll = (data || []).slice().sort((a, b) => this.parseMaybeDate(b.packingDate) - this.parseMaybeDate(a.packingDate));
        this.applyPackingFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading packing items:', err);
        this.loading = false;
      }
    });
  }

  // parse date string in ISO or dd-MM-yyyy formats; return timestamp
  private parseMaybeDate(str?: string): number {
    if (!str) return 0;
    const t = Date.parse(str);
    if (!isNaN(t)) return t;
    const parts = str.split('-').map(p => Number(p));
    if (parts.length === 3) {
      // assume dd-mm-yyyy
      const [d, m, y] = parts;
      return new Date(y, (m || 1) - 1, d || 1).getTime();
    }
    return 0;
  }

  // ✅ Load all completed productions
  loadCompletedProductions() {
    this.http.get<any[]>(`${this.productionApiUrl}/completed`).subscribe({
      next: (data) => {
        this.completedProductions = data || [];
        // reset pagination for completed productions
        this.compCurrentPage = 1;
        this.updatePaginationCompleted();
      },
      error: (err) => {
        console.error('Error loading completed productions:', err);
      }
    });
  }

  getCustomerName(production: any): string {
    return production?.customer || production?.customerName || 'Customer';
  }

  /** ------------------ Filtering Helpers ------------------ **/
  applyPackingFilters() {
    const q = (this.packingSearch || '').toString().trim().toLowerCase();
    this.packingItems = this.packingItemsAll.filter(item => {
      let ok = true;
      if (q) {
        ok = [item.poNo, item.customer, item.challanNo, item.heatNo]
          .some(f => (f || '').toString().toLowerCase().includes(q));
      }
      if (this.packingStatus && this.packingStatus !== 'All Status') {
        ok = ok && (item.status === this.packingStatus);
      }
      return ok;
    }).slice();

    // sort
    this.packingItems.sort((a, b) => this.packingSortOldest ? this.parseMaybeDate(a.packingDate) - this.parseMaybeDate(b.packingDate) : this.parseMaybeDate(b.packingDate) - this.parseMaybeDate(a.packingDate));

    // pagination
    this.packingCurrentPage = 1;
    this.updatePaginationPacking();
  }

  togglePackingSort() {
    this.packingSortOldest = !this.packingSortOldest;
    this.applyPackingFilters();
  }

  // Packing items pagination
  updatePaginationPacking() {
    const total = this.packingItems.length;
    this.packingTotalPages = Math.max(1, Math.ceil(total / this.packingPageSize));
    if (this.packingCurrentPage > this.packingTotalPages) this.packingCurrentPage = this.packingTotalPages;
    const start = (this.packingCurrentPage - 1) * this.packingPageSize;
    this.displayedPackingItems = this.packingItems.slice(start, start + this.packingPageSize);
  }

  changePagePacking(page: number) {
    if (page < 1) page = 1;
    if (page > this.packingTotalPages) page = this.packingTotalPages;
    this.packingCurrentPage = page;
    this.updatePaginationPacking();
  }

  setPackingPageSize(size: number) {
    this.packingPageSize = size;
    this.packingCurrentPage = 1;
    this.updatePaginationPacking();
  }

  // Completed productions pagination
  updatePaginationCompleted() {
    const total = this.completedProductions.length;
    this.compTotalPages = Math.max(1, Math.ceil(total / this.compPageSize));
    if (this.compCurrentPage > this.compTotalPages) this.compCurrentPage = this.compTotalPages;
    const start = (this.compCurrentPage - 1) * this.compPageSize;
    this.displayedCompletedProductions = this.completedProductions.slice(start, start + this.compPageSize);
  }

  changePageCompleted(page: number) {
    if (page < 1) page = 1;
    if (page > this.compTotalPages) page = this.compTotalPages;
    this.compCurrentPage = page;
    this.updatePaginationCompleted();
  }

  setCompletedPageSize(size: number) {
    this.compPageSize = size;
    this.compCurrentPage = 1;
    this.updatePaginationCompleted();
  }

  // ✅ Open packing form pre-filled from production
  openPackingFormFromProduction(production: any) {
    this.selectedProduction = production;
    this.showPackingForm = true;
    this.isEditMode = false;

    if (production.items && production.items.length > 0) {
      const firstItem = production.items[0];
      this.packing = {
        id: null,
        srNo: null,
        poNo: production?.loiNumber || '',
        grade: firstItem.grade || '',
        colourCode: '',
        gradeSection: firstItem.section || '',
        sectionWt: '',
        length: firstItem.length || null,
        noOfPcs: firstItem.plannedQty || null,
        qtyInMt: firstItem.plannedQty || null,
        heatNo: 'HEAT-' + Date.now(),
        challanQty: null,
        challanNo: '',
        customer: production?.customer || production?.customerName || '',
        docNo: 'DOC-' + Date.now(),
        packingDate: new Date().toISOString().slice(0, 10),
        lorryNo: '',
        status: 'PACKED'
      };
    }
  }

  // ✅ Close form
  closePackingForm() {
    this.showPackingForm = false;
    this.resetForm();
  }

// ✅ Save Packi;k`  ng
  savePacking() {
    if (!this.selectedProduction) {
      this.message = 'No production selected!';
      alert('❌ ' + this.message);
      return;
    }

    const url = `${this.apiUrl}/from-production/${this.selectedProduction.id}`;
    console.log('Sending packing data:', this.packing);

    // 🚀 No custom headers needed — Angular sets JSON automatically
    this.http.post<any>(url, this.packing).subscribe({
      next: (res) => {
        this.message = res?.message || 'Packing created successfully!';
        alert(`✅ ${this.message}\n\nPacking ID: ${res?.packingId || ''}`);
        this.loadPackingItems();
        this.loadCompletedProductions();
        this.closePackingForm();
      },
      error: (err) => {
        console.error('Packing error:', err);
        this.message = err?.error?.message || 'Failed to create packing.';
        alert('❌ ' + this.message);
      }
    });
  }

  // ✅ Reset form
  resetForm() {
    this.packing = {
      id: null,
      srNo: null,
      poNo: '',
      grade: '',
      colourCode: '',
      gradeSection: '',
      sectionWt: '',
      length: null,
      noOfPcs: null,
      qtyInMt: null,
      heatNo: '',
      challanQty: null,
      challanNo: '',
      customer: '',
      docNo: '',
      packingDate: new Date().toISOString().slice(0, 10),
      lorryNo: '',
      status: 'PACKED'
    };
  }

  getBadgeClass(status: string): string {
    switch (status) {
      case 'PACKED':
        return 'bg-success';
      case 'DISPATCHED':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }

  goToDispatch(packing: any) {
    globalThis.location.href = '/dispatch';
  }
}
