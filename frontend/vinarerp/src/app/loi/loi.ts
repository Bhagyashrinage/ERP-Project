import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-loi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loi.html',
  styleUrls: ['./loi.css']
})
export class LOIComponent implements OnInit {

  // expose Math to templates (used for Math.min in template)
  Math = Math;

  activeTab: 'create' | 'list' = 'create';

  loi = {
    loiNumber: '',
    customerName: '',
    location: '',
    loiDate: '',
    status: 'Pending',
    items: [
      { material: '', grade: '', section: '', length: 0, quantity: 0, unit: 'TON' }
    ]
  };

  lois: any[] = [];
  allLois: any[] = []; // master copy for filtering
  searchText: string = '';
  statusFilter: string = '';
  // Pagination
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 1;
  displayedLois: any[] = [];
  // default to oldest-first (ascending) so IDs show 1,2,3...
  sortDescending: boolean = false;
  loading = false;
  errorMessage = '';
  message = '';

  showLOIModal = false;
  selectedLOI: any = null;

  private loiApiUrl = 'http://localhost:8080/api/loi';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getAllLOIs();
    this.prefillNextLoiNumber();
  }

  switchTab(tab: 'create' | 'list') {
    this.activeTab = tab;
    if (tab === 'list') this.getAllLOIs();
  }

  addItem() {
    this.loi.items.push({ material: '', grade: '', section: '', length: 0, quantity: 0, unit: 'TON' });
  }

  removeItem(ix: number) {
    this.loi.items.splice(ix, 1);
  }

  resetForm() {
    this.loi = {
      loiNumber: '',
      customerName: '',
      location: 'Butibori',
      loiDate: new Date().toISOString().slice(0, 10),
      status: 'Pending',
      items: [
        { material: '', grade: '', section: '', length: 0, quantity: 0, unit: 'TON' }
      ]
    };
    this.message = '';
    this.prefillNextLoiNumber();
  }

  // ✅ New submit handler with popup validation
  onSubmit(form: any) {

  if (form.invalid) {
    alert('❌ Please correct all invalid fields before submitting the LOI.');
    return;
  }

  const alpha = /^[A-Za-z ]+$/;
  const alphaNum = /^[A-Za-z0-9 ]+$/;
  // allow numbers and symbols for section: * + - % and spaces
  const numbers = /^[-0-9*+%\s]+$/;

  if (!alpha.test(this.loi.customerName || '')) {
    alert('Customer name should contain only alphabets.');
    return;
  }

  if (!alphaNum.test(this.loi.location || '')) {
    alert('Location should contain only alphabets and numbers.');
    return;
  }

  for (let item of this.loi.items) {
    if (!alphaNum.test(item.material || '')) {
      alert('Material should contain only letters and numbers.');
      return;
    }
    if (!alphaNum.test(item.grade || '')) {
      alert('Grade should contain only letters and numbers.');
      return;
    }
    if (!numbers.test(String(item.section || ''))) {
      alert('Section should contain only numbers and symbols (e.g. 50*100+1%).');
      return;
    }
  }

  this.createLOI();
}

allowOnlyLetters(event: KeyboardEvent) {
  if (!/^[a-zA-Z ]$/.test(event.key)) {
    event.preventDefault();
  }
}

allowAlphaNumeric(event: KeyboardEvent) {
  if (!/^[a-zA-Z0-9 ]$/.test(event.key)) {
    event.preventDefault();
  }
}

allowOnlyNumbers(event: KeyboardEvent) {
  if (!/^[0-9]$/.test(event.key)) {
    event.preventDefault();
  }
}

allowNumbersAndSymbols(event: KeyboardEvent) {
  // Allow digits and characters * + - % and space
  if (!/^[-0-9*+%\s]$/.test((event as KeyboardEvent).key)) {
    event.preventDefault();
  }
}


  createLOI() {
    const payload = JSON.parse(JSON.stringify(this.loi));
    payload.items = payload.items.filter((it: any) => it.material && Number(it.quantity) > 0);

    if (!payload.items.length) {
      this.message = 'Please add at least one valid item.';
      return;
    }

    this.http.post<any>(this.loiApiUrl, payload).subscribe({
      next: () => {
        this.message = 'LOI created successfully!';
        this.resetForm();
        this.switchTab('list');
        this.getAllLOIs();
      },
      error: err => {
        console.error(err);
        this.message = err?.error?.message || 'Failed to create LOI';
      }
    });
  }

  prefillNextLoiNumber() {
    this.http.get<any>(`${this.loiApiUrl}/next-number`).subscribe({
      next: res => this.loi.loiNumber = res?.number || this.loi.loiNumber,
      error: err => console.error('Failed to get next LOI number', err)
    });
  }

  getAllLOIs() {
    this.loading = true;
    this.http.get<any[]>(this.loiApiUrl).subscribe({
      next: res => {
        const list = (res || []).map(l => ({
          ...l,
          customerName: typeof l.customerName === 'string' ? l.customerName : l.customerName?.name || l.customer || '-'
        }));
  // base sort by id asc so list shows 1,2,3... by default
  list.sort((a, b) => a.id - b.id);
        this.allLois = list;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Failed to fetch LOIs';
        this.loading = false;
      }
    });
  }

  // Apply filters and sorting from UI state
  filterLOIs() {
    this.applyFilters();
  }

  private applyFilters() {
    let filtered = this.allLois.slice();

    const s = (this.searchText || '').toString().trim().toLowerCase();
    if (s) {
      filtered = filtered.filter(l => (l.loiNumber || '').toString().toLowerCase().includes(s) || (l.customerName || '').toString().toLowerCase().includes(s));
    }

    if (this.statusFilter) {
      filtered = filtered.filter(l => (l.status || '') === this.statusFilter);
    }

    if (this.sortDescending) filtered.sort((a, b) => b.id - a.id);
    else filtered.sort((a, b) => a.id - b.id);

    // update pagination state and slice for display
    this.lois = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  sortLatest() {
    this.sortDescending = !this.sortDescending;
    this.applyFilters();
  }

  // Pagination helpers
  updatePagination() {
    const total = this.lois.length;
    this.totalPages = Math.max(1, Math.ceil(total / this.pageSize));
    // clamp currentPage
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.displayedLois = this.lois.slice(start, end);
  }

  changePage(page: number) {
    if (page < 1) page = 1;
    if (page > this.totalPages) page = this.totalPages;
    this.currentPage = page;
    this.updatePagination();
  }

  setPageSize(size: number) {
    this.pageSize = size;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Format LOI id into a consistent display form, e.g. LOI-000123
  formatLoiId(id: any): string {
    if (id === null || id === undefined) return '-';
    const n = Number(id);
    if (isNaN(n)) return String(id);
    return `LOI-${n.toString().padStart(6, '0')}`;
  }

  openLOIModal(row: any) {
    this.http.get<any>(`${this.loiApiUrl}/${row.id}`).subscribe({
      next: res => {
        this.selectedLOI = res;
        this.showLOIModal = true;
      },
      error: err => console.error(err)
    });
  }

  closeLOIModal() {
    this.showLOIModal = false;
    this.selectedLOI = null;
  }

  updateStatus(row: any, status: 'Pending' | 'Planned' | 'Completed') {
    if (row.status === 'Completed') {
      alert('This LOI is already completed. Status cannot be changed.');
      return;
    }

    this.http.put<any>(
      `${this.loiApiUrl}/${row.id}/status?status=${encodeURIComponent(status)}`, {}
    ).subscribe({
      next: () => this.getAllLOIs(),
      error: err => console.error(err)
    });
  }

  badgeClass(status: string) {
    return {
      'badge': true,
      'badge-pending': status === 'Pending',
      'badge-planned': status === 'Planned',
      'badge-completed': status === 'Completed'
    };
  }
}
