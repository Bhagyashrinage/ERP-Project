import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-purchase-order',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './purchase-order.html',
  styleUrls: ['./purchase-order.css']
})
export class PurchaseOrder implements OnInit {
  // expose Math to templates (used for Math.min in template)
  Math = Math;
  activeTab: 'create' | 'list' = 'create';

  po = {
    orderNo: '',
    party: '',
    supplier: '', // Added supplier field
    date: '',
    location: '',
    elements: [
      {
        material: '',
        grade: '',
        section: '',
        length: 0,
        steelWidth: 0,
        quantity: 0,
        type: '',
        receivedQuantity: 0,
        remarks: ''
      }
    ]
  };

  purchaseOrders: any[] = [];
  // keep a master copy so filtering doesn't lose original data
  allPurchaseOrders: any[] = [];
  // Pagination
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 1;
  displayedPurchaseOrders: any[] = [];
  // bindings used by template for filtering/sorting
  searchText: string = '';
  statusFilter: string = '';
  // default to ascending (1,2,3...)
  // default to ascending order (oldest first: 1,2,3...)
  sortDescending = false;
  message = '';
  loading = false;
  errorMessage = '';

  showPOModal = false;
  selectedPO: any = null;

  showGRNModal = false;
  selectedPOForGRN: any = null;
  grnNumber = '';
  vehicleNo = '';
  receiverName = '';
  grnDate: string | null = null;

  private poApiUrl = 'http://localhost:8080/api/po';
  private grnApiUrl = 'http://localhost:8080/api/grn';

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const url = this.router.url;
    if (url.includes('/addpo')) this.activeTab = 'create';
    if (url.includes('/po')) this.activeTab = 'list';

    this.getAllPOs();
    this.prefillNextPONumber();
  }

  switchTab(tab: 'create' | 'list') {
    this.activeTab = tab;
    if (tab === 'list') this.getAllPOs();
  }

  addElement() {
    this.po.elements.push({
      material: '',
      grade: '',
      section: '',
      length: 0,
      steelWidth: 0,
      quantity: 0,
      type: '',
      receivedQuantity: 0,
      remarks: ''
    });
  }

  removeElement(index: number) {
    this.po.elements.splice(index, 1);
  }

  createPO() {
    this.http.post(this.poApiUrl, this.po).subscribe({
      next: () => {
        this.message = 'Purchase Order created successfully!';
        this.resetForm();
        this.switchTab('list');
      },
      error: err => {
        console.error(err);
        this.message = 'Failed to create Purchase Order!';
      }
    });
  }

  resetForm() {
    this.po = {
      orderNo: '',
      party: '',
      supplier: '', // Reset supplier field
      date: new Date().toISOString().slice(0, 10),
      location: 'Butibori',
      elements: [
        {
          material: '',
          grade: '',
          section: '',
          length: 0,
          steelWidth: 0,
          quantity: 0,
          type: '',
          receivedQuantity: 0,
          remarks: ''
        }
      ]
    };
    this.prefillNextPONumber();
  }

  
onSubmitPO(form: any) {

  // If ANY field is blank or invalid
  if (form.invalid) {

    // Show all errors on UI
    Object.keys(form.controls).forEach(key => {
      form.controls[key].markAsTouched();
    });

    alert('❌ Please fill all required fields with valid values before submitting.');
    return;
  }

  // Extra safety checks (optional but good)
  if (!this.po.date) {
    alert('❌ Date is required.');
    return;
  }

  if (!this.po.party || !/^[A-Za-z ]+$/.test(this.po.party)) {
    alert('❌ Party should contain only alphabets.');
    return;
  }

  for (let e of this.po.elements) {
    if (!e.material || !/^[A-Za-z0-9 ]+$/.test(e.material)) {
      alert('❌ Material is required and must contain only letters and numbers.');
      return;
    }
    if (!e.grade || !/^[A-Za-z0-9]+$/.test(e.grade)) {
      alert('❌ Grade is required and must contain only letters and numbers.');
      return;
    }
    // allow numbers and common symbols in section (e.g. 50-100/1)
      if (!e.section || !/^[-0-9*+%\s]+$/.test(String(e.section))) {
        alert('❌ Section is required and must contain numbers or symbols like * + - %');
      return;
    }
    if (!e.type || !/^[A-Za-z ]+$/.test(e.type)) {
      alert('❌ Type is required and must contain only alphabets.');
      return;
    }
    if (!e.quantity || Number(e.quantity) <= 0) {
      alert('❌ Quantity must be greater than 0.');
      return;
    }
  }

  this.createPO();
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
  // allow digits and symbols * + - % and space
  if (!/^[-0-9*+%\s]$/.test((event as KeyboardEvent).key)) {
    event.preventDefault();
  }
}


  getAllPOs() {
    this.loading = true;
    this.http.get<any[]>(this.poApiUrl).subscribe({
      next: res => {
        const mapped = res
          .map(po => ({
            ...po,
            party:
              typeof po.party === 'string'
                ? po.party
                : po.party?.name || po.partyName || '-',
              // normalize supplier similar to party (backend may return object or string)
              supplier:
                typeof po.supplier === 'string'
                  ? po.supplier
                  : po.supplier?.name || po.supplierName || po.supplier || po.vendor?.name || po.vendorName || '-',
              // normalize receiver/lastReceiver from several possible backend shapes
              lastReceiverName:
                po.lastReceiverName || po.lastReceiver || po.receiverName || po.lastReceivedBy || po.receivedBy?.name || po.lastGrnReceiver || '-'
          }))
          // base sort by id asc so list shows 1,2,3... by default
          // base sort by id asc so list shows 1,2,3... by default
          .sort((a, b) => a.id - b.id);

        this.allPurchaseOrders = mapped;
        this.applyFilters();

        this.errorMessage = '';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Failed to fetch Purchase Orders!';
        this.loading = false;
      }
    });
  }

  // Apply current search/status filters and sorting to the master list
  filterPOs() {
    this.applyFilters();
  }

  private applyFilters() {
    let filtered = this.allPurchaseOrders.slice();

    const s = (this.searchText || '').toString().trim().toLowerCase();
    if (s) {
      filtered = filtered.filter(po => (po.orderNo || '').toString().toLowerCase().includes(s));
    }

    if (this.statusFilter) {
      filtered = filtered.filter(po => (po.status || '') === this.statusFilter);
    }

    if (this.sortDescending) filtered.sort((a, b) => b.id - a.id);
    else filtered.sort((a, b) => a.id - b.id);

    this.purchaseOrders = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  sortLatest() {
    this.sortDescending = !this.sortDescending;
    this.applyFilters();
  }

  // Pagination helpers
  updatePagination() {
    const total = this.purchaseOrders.length;
    this.totalPages = Math.max(1, Math.ceil(total / this.pageSize));
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.displayedPurchaseOrders = this.purchaseOrders.slice(start, end);
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

  openPOModal(po: any) {
    this.http.get<any>(`${this.poApiUrl}/${po.id}`).subscribe({
      next: res => {
        this.selectedPO = {
          ...res,
          party:
            typeof res.party === 'string'
              ? res.party
              : res.party?.name || res.partyName || '-',
          supplier:
            typeof res.supplier === 'string'
              ? res.supplier
              : res.supplier?.name || res.supplierName || res.supplier || '-'
        };
        // normalize lastReceiverName for modal
        this.selectedPO.lastReceiverName =
          res.lastReceiverName || res.lastReceiver || res.receiverName || res.lastReceivedBy || res.receivedBy?.name || res.lastGrnReceiver || '-';
        this.showPOModal = true;
      },
      error: err => console.error(err)
    });
  }

  closePOModal() {
    this.showPOModal = false;
    this.selectedPO = null;
  }

  openGRNModal(po: any) {
    this.http.get<any>(`${this.poApiUrl}/${po.id}`).subscribe({
      next: res => {
        this.selectedPOForGRN = {
          ...res,
          party:
            typeof res.party === 'string'
              ? res.party
              : res.party?.name || res.partyName || '-',
          supplier:
            typeof res.supplier === 'string'
              ? res.supplier
              : res.supplier?.name || res.supplierName || res.supplier || '-'
        };
        // normalize lastReceiverName for GRN modal
        this.selectedPOForGRN.lastReceiverName =
          res.lastReceiverName || res.lastReceiver || res.receiverName || res.lastReceivedBy || res.receivedBy?.name || res.lastGrnReceiver || '-';

        this.selectedPOForGRN.elements.forEach((e: any) => {
          e.receivedQuantityFromServer = e.receivedQuantity || 0;
          e.receivedQuantity = 0;
          e.remarks = '';
        });

        this.grnNumber = `GRN-${Date.now()}`;
        this.vehicleNo = '';
        this.receiverName = '';
        this.grnDate = new Date().toISOString().slice(0, 10);
        this.showGRNModal = true;
      },
      error: err => console.error(err)
    });
  }

  closeGRNModal() {
    this.showGRNModal = false;
    this.selectedPOForGRN = null;
    this.grnNumber = '';
    this.vehicleNo = '';
    this.receiverName = '';
    this.grnDate = null;
  }

  qtyLeftFor(e: any) {
    const total = Number(e.quantity || 0);
    const already = Number(e.receivedQuantityFromServer || 0);
    return Math.max(0, total - already);
  }

  // Format PO id into a consistent display form, e.g. PO-000123
  formatPOId(id: any): string {
    if (id === null || id === undefined) return '-';
    const n = Number(id);
    if (isNaN(n)) return String(id);
    return `PO-${n.toString().padStart(6, '0')}`;
  }

  submitGRN() {
    if (!this.selectedPOForGRN) return;

    if (!this.receiverName.trim()) {
      alert('Receiver Name is required!');
      return;
    }

    const poId = this.selectedPOForGRN.id;

    const items = this.selectedPOForGRN.elements
      .filter((e: any) => Number(e.receivedQuantity) > 0)
      .map((e: any) => ({
        poElementId: e.id,
        receivedQty: Number(e.receivedQuantity),
        remarks: e.remarks || ''
      }));

    if (items.length === 0) {
      alert('Please enter at least one received quantity.');
      return;
    }

    const grnBody = {
      grnNumber: this.grnNumber,
      vehicleNo: this.vehicleNo,
      grnDate: this.grnDate,
      receiverName: this.receiverName,
      items
    };

    this.http.post<any>(`${this.grnApiUrl}/po/${poId}`, grnBody).subscribe({
      next: () => {
        alert('GRN created successfully!');
        this.closeGRNModal();
        this.switchTab('list');
        this.getAllPOs();
      },
      error: err => {
        console.error(err);
        alert(err?.error?.message || 'Failed to create GRN.');
      }
    });
  }

  prefillNextPONumber() {
    this.http.get<any>(`${this.poApiUrl}/next-number`).subscribe({
      next: res => (this.po.orderNo = res?.number || this.po.orderNo),
      error: err => console.error('Failed to get next PO number', err)
    });
  }
}
