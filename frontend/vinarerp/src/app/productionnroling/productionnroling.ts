import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-productionnroling',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productionnroling.html',
  styleUrls: ['./productionnroling.css']
})
export class Productionnroling implements OnInit {

  activeTab: 'create' | 'list' = 'create';

  plan = {
    loiId: null as number | null,
    mill: '',
    shift: '',
    items: [] as any[]
  };

  lois: any[] = [];
  plans: any[] = [];
  filteredPlans: any[] = [];
  // Pagination for plans
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 1;
  displayedPlans: any[] = [];
  // expose Math to templates
  Math = Math;
  planFilter: string = '';

  loading = false;
  message = '';
  errorMessage = '';

  /* ================= MODAL & STOCK ================= */
  showStockModal = false;
  selectedItem: any = null;
  eligibleStock: any[] = [];

  selectedStocks: { [loiItemId: number]: any[] } = {};
  modalSelections = new Map<number, Set<number>>();
  modalQty: { [stockId: number]: number } = {};

  private readonly loiApiUrl = 'http://localhost:8080/api/loi';
  private readonly planApiUrl = 'http://localhost:8080/api/rolling-plan';
  private readonly stockApiUrl = 'http://localhost:8080/api/stock';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchLOIs();
    this.fetchPlans();
  }

  /* ================= TAB SWITCH ================= */
  switchTab(tab: 'create' | 'list'): void {
    this.activeTab = tab;
    this.message = '';
    this.errorMessage = '';
    if (tab === 'list') this.fetchPlans();
  }

  /* ================= LOI ================= */
  fetchLOIs(): void {
    this.http.get<any[]>(this.loiApiUrl).subscribe({
      next: res => {
        this.lois = res.filter(
          loi => loi.status === 'Completed'
        );
      },
      error: err => console.error('Failed to load LOIs', err)
    });
  }

  onSelectLOI(event: Event): void {
    const id = (event.target as HTMLSelectElement).value;
    this.resetSelections();
    if (!id) {
      this.plan.loiId = null;
      this.plan.items = [];
      return;
    }

    this.http.get<any>(`${this.loiApiUrl}/${id}`).subscribe({
      next: loi => {
        this.plan.loiId = loi.id;
        // Ensure plan.items is a reference array
        this.plan.items = loi.items.map((it: any) => ({
          loiItemId: it.id,
          material: it.material,
          grade: it.grade,
          section: it.section,
          length: it.length,
          quantity: it.quantity,
          unit: it.unit,
          selected: false,
          selectedStock: [] // initialize as empty array
        }));
      },
      error: err => console.error('Failed to load LOI', err)
    });
  }

  /* ================= STOCK MODAL ================= */
  openStockModal(item: any): void {
    // Ensure reference is from plan.items
    this.selectedItem = this.plan.items.find(i => i.loiItemId === item.loiItemId);
    if (!this.selectedItem) return;

    this.showStockModal = true;
    this.message = '';
    this.eligibleStock = [];
    this.modalQty = {};

    const itemId = this.selectedItem.loiItemId;
    const alreadySelected = (this.selectedStocks[itemId] || []).map(s => s.stockId);
    this.modalSelections.set(itemId, new Set(alreadySelected));

    this.http.get<any[]>(`${this.planApiUrl}/eligible-stock/${itemId}`).subscribe({
      next: res => {
        // Filter eligible stock if backend returns extra items
        // Relaxed filter: match by section OR material
        this.eligibleStock = res.filter(s =>
          s.section === this.selectedItem.section || s.material === this.selectedItem.material
        );
        this.eligibleStock.forEach(s => {
          this.modalQty[s.id] = Math.min(this.selectedItem.quantity, s.totalQuantity);
        });
        if (!this.eligibleStock.length) this.message = `⚠️ No eligible stock found for ${this.selectedItem.section} or ${this.selectedItem.material}`;
      },
      error: err => {
        console.error(err);
        this.message = 'Failed to load eligible stock';
      }
    });
  }

  toggleModalSelection(stock: any, checked: boolean): void {
    if (!this.selectedItem) return;
    const itemId = this.selectedItem.loiItemId;
    if (!this.modalSelections.has(itemId)) this.modalSelections.set(itemId, new Set());
    const set = this.modalSelections.get(itemId)!;
    checked ? set.add(stock.id) : set.delete(stock.id);
  }

  isModalSelected(stock: any): boolean {
    if (!this.selectedItem) return false;
    return this.modalSelections.get(this.selectedItem.loiItemId)?.has(stock.id) ?? false;
  }

  confirmStockSelection(): void {
    if (!this.selectedItem) return;
    const itemId = this.selectedItem.loiItemId;
    const selectedIds = Array.from(this.modalSelections.get(itemId) || []);

    if (!this.selectedStocks[itemId]) this.selectedStocks[itemId] = [];

    selectedIds.forEach(id => {
      const stock = this.eligibleStock.find(s => s.id === id);
      if (!stock) return;
      let qty = this.modalQty[id] ?? this.selectedItem.quantity;
      qty = Math.min(qty, stock.totalQuantity);

      // Add or update stock for this LOI item
      const existing = this.selectedStocks[itemId].find(s => s.stockId === id);
      if (existing) {
        existing.plannedQty = qty;
      } else {
        this.selectedStocks[itemId].push({
          loiItemId: itemId,
          stockId: stock.id,
          plannedQty: qty,
          meta: { ...stock }
        });
      }
    });

    // Bind all selected stocks to the plan item for table display
    this.selectedItem.selectedStock = this.selectedStocks[itemId];

    this.closeStockModal();
  }

  closeStockModal(): void {
    this.showStockModal = false;
    if (this.selectedItem) {
      this.modalSelections.delete(this.selectedItem.loiItemId);
      this.modalQty = {};
    }
  }

  removeSelectedStock(loiItemId: number, index: number): void {
    const arr = this.selectedStocks[loiItemId];
    if (!arr) return;
    arr.splice(index, 1);
    const item = this.plan.items.find(i => i.loiItemId === loiItemId);
    if (item) item.selectedStock = arr.length ? arr : [];
  }

  /* ================= CREATE PLAN ================= */
  createPlanFromSelections(): void {
    this.message = '';
    if (!this.plan.loiId) { this.message = 'Please select an LOI.'; return; }
    if (!this.plan.mill || !this.plan.shift) { this.message = 'Select Mill & Shift.'; return; }
    const selections = Object.values(this.selectedStocks).flat();
    if (!selections.length) { this.message = 'Select stock for at least one item.'; return; }

    const payload = {
      loiId: this.plan.loiId,
      mill: this.plan.mill,
      shift: this.plan.shift,
      selectedStocks: selections.map(s => ({
        loiItemId: s.loiItemId,
        stockId: s.stockId,
        plannedQty: s.plannedQty
      }))
    };

    this.loading = true;
    this.http.post<any>(`${this.planApiUrl}/from-selection`, payload).subscribe({
      next: res => {
        this.loading = false;
        this.message = res?.message || 'Rolling Plan created successfully!';
        this.resetForm();
        this.fetchPlans();
        this.switchTab('list');
      },
      error: err => {
        this.loading = false;
        console.error(err);
        this.message = err?.error?.message || 'Rolling Plan creation failed';
      }
    });
  }

  resetForm(): void {
    this.plan = { loiId: null, mill: '', shift: '', items: [] };
    this.resetSelections();
  }

  resetSelections(): void {
    this.selectedStocks = {};
    this.modalSelections.clear();
    this.selectedItem = null;
    this.eligibleStock = [];
    this.modalQty = {};
  }

  /* ================= PLAN LIST ================= */
  fetchPlans(): void {
    this.loading = true;
    this.http.get<any[]>(this.planApiUrl).subscribe({
      next: res => {
        // Sort newest first
        this.plans = res.sort((a, b) => new Date(b.planDate).getTime() - new Date(a.planDate).getTime());
        this.applyFilter(); // initialize filteredPlans and pagination
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to fetch plans';
        this.loading = false;
      }
    });
  }

  // ================= FILTER FUNCTION =================
  applyFilter(): void {
    const filter = this.planFilter.trim().toLowerCase();
    if (!filter) {
      this.filteredPlans = [...this.plans];
      this.currentPage = 1;
      this.updatePagination();
      return;
    }

    this.filteredPlans = this.plans.filter(p =>
      (p.planNumber?.toString().toLowerCase().includes(filter)) ||
      (p.loiId?.toString().toLowerCase().includes(filter)) ||
      (p.mill?.toLowerCase().includes(filter)) ||
      (p.shift?.toLowerCase().includes(filter)) ||
      (p.status?.toLowerCase().includes(filter))
    );
    this.currentPage = 1;
    this.updatePagination();
  }

  // Pagination helpers
  updatePagination() {
    const total = this.filteredPlans.length;
    this.totalPages = Math.max(1, Math.ceil(total / this.pageSize));
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.displayedPlans = this.filteredPlans.slice(start, end);
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

  badgeClass(status: string) {
    return {
      badge: true,
      'badge-pending': status === 'Pending',
      'badge-planned': status === 'Planned',
      'badge-completed': status === 'Completed'
    };
  }

  markAsCompleted(plan: any): void {
    if (!confirm(`Mark production ${plan.planNumber} as completed?`)) return;
    this.http.put(`${this.planApiUrl}/${plan.id}`, { ...plan, status: 'Completed' })
      .subscribe({
        next: () => {
          this.message = 'Production marked as completed! Go to Packing module.';
          this.fetchPlans();
        },
        error: err => {
          console.error(err);
          this.message = err?.error?.message || 'Failed to update status';
        }
      });
  }
}
