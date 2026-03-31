import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-reportandanalytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportandanalytics.html',
  styleUrls: ['./reportandanalytics.css']
})
export class Reportandanalytics implements OnInit {

  // Stock data
  allStock: any[] = [];
  filteredStock: any[] = [];
  // Pagination for stock table
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 1;
  displayedStock: any[] = [];
  loading = false;
  errorMessage = '';

  // Filters
  filters = {
    location: '',
    material: ''
  };

  locations: string[] = [];
  materials: string[] = [];

  // KPI Structure
  kpis = {
    totalLOIs: 0,
    pendingLOIs: 0,
    completedLOIs: 0,
    totalPOs: 0,
    totalGRNs: 0,
    totalStock: 0,
    totalProductions: 0,
    completedProductions: 0,
    totalPacking: 0,
    totalDispatches: 0,
    dispatchedItems: 0,
    totalJobworkChallans: 0,
    jobworkIssued: 0,
    jobworkPending: 0
  };

  locationKPIs: any[] = [];

  // API URLs
  private apiUrl = 'http://localhost:8080/api/stock';
  private loiApiUrl = 'http://localhost:8080/api/loi';
  private poApiUrl = 'http://localhost:8080/api/po';
  private grnApiUrl = 'http://localhost:8080/api/grn';
  private planApiUrl = 'http://localhost:8080/api/rolling-plan';
  private packingApiUrl = 'http://localhost:8080/api/packing';
  private dispatchApiUrl = 'http://localhost:8080/api/dispatch';
  private jobworkApiUrl = 'http://localhost:8080/api/jobwork/challans';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  // Normalize string (case-insensitive + trim)
  private normalize(value: string): string {
    return (value || '').trim().toLowerCase();
  }

  // Load all data
  loadAllData(): void {
    this.loading = true;
    this.errorMessage = '';

    Promise.all([
      this.loadStockData(),
      this.loadLOIData(),
      this.loadPOData(),
      this.loadGRNData(),
      this.loadProductionData(),
      this.loadPackingData(),
      this.loadDispatchData(),
      this.loadJobworkData()
    ])
      .then(() => {
        this.calculateKPIs();
        this.calculateLocationKPIs();
        this.loading = false;
      })
      .catch(() => {
        this.errorMessage = 'Some data failed to load.';
        this.loading = false;
      });
  }

  // Stock
  loadStockData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.apiUrl).subscribe({
        next: data => {

          // 🔥 Normalize location names
          this.allStock = data.map(s => ({
            ...s,
            location: s.location ? s.location.trim().toUpperCase() : 'UNKNOWN'
          }));

          this.filteredStock = [...this.allStock];
          this.extractFilters();
          // initialize pagination for stock table
          this.currentPage = 1;
          this.updatePaginationStock();
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadLOIData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.loiApiUrl).subscribe({
        next: data => {
          this.kpis.totalLOIs = data.length;
          this.kpis.pendingLOIs = data.filter(x => x.status === 'Pending').length;
          this.kpis.completedLOIs = data.filter(x => x.status === 'Completed').length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadPOData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.poApiUrl).subscribe({
        next: data => {
          this.kpis.totalPOs = data.length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadGRNData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.grnApiUrl).subscribe({
        next: data => {
          this.kpis.totalGRNs = data.length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadProductionData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.planApiUrl).subscribe({
        next: data => {
          this.kpis.totalProductions = data.length;
          this.kpis.completedProductions = data.filter(p => p.status === 'Completed').length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadPackingData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.packingApiUrl).subscribe({
        next: data => {
          this.kpis.totalPacking = data.length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadDispatchData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.dispatchApiUrl).subscribe({
        next: data => {
          this.kpis.totalDispatches = data.length;
          this.kpis.dispatchedItems = data.filter(d => d.status === 'Delivered').length;
          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  loadJobworkData(): Promise<any> {
    return new Promise(resolve => {
      this.http.get<any[]>(this.jobworkApiUrl).subscribe({
        next: data => {
          this.kpis.totalJobworkChallans = data.length;

          this.kpis.jobworkIssued = data.filter(c =>
            this.normalize(c.status).includes('issued')
          ).length;

          this.kpis.jobworkPending = data.filter(c =>
            this.normalize(c.status).includes('pending')
          ).length;

          resolve(data);
        },
        error: () => resolve([])
      });
    });
  }

  // Helper method to convert MT to KG
  convertToKg(quantity: number): number {
    return quantity * 1000;
  }

  calculateKPIs(): void {
    this.kpis.totalStock = this.allStock.reduce(
      (sum, s) => sum + (s.totalQuantity || s.quantity || 0),
      0
    );
  }

  calculateLocationKPIs(): void {
    const map = new Map<string, any>();

    this.allStock.forEach(s => {
      const loc = s.location;

      if (!map.has(loc)) {
        map.set(loc, {
          location: loc,
          totalItems: 0,
          totalQuantity: 0,
          materials: new Set<string>()
        });
      }

      const obj = map.get(loc);
      obj.totalItems++;
      obj.totalQuantity += s.totalQuantity || s.quantity || 0;
      obj.materials.add(s.material);
    });

    this.locationKPIs = Array.from(map.values()).map(x => ({
      location: x.location,
      totalItems: x.totalItems,
      totalQuantity: x.totalQuantity,
      uniqueMaterials: x.materials.size
    }));
  }

  extractFilters() {
    this.locations = [...new Set(this.allStock.map(s => s.location))].sort();
    this.materials = [...new Set(this.allStock.map(s => s.material).filter(Boolean))].sort();
  }

  applyFilters() {
    this.filteredStock = this.allStock.filter(s => {
      const locMatch = !this.filters.location || s.location === this.filters.location;
      const matMatch = !this.filters.material || s.material === this.filters.material;
      return locMatch && matMatch;
    });
    // reset to first page and update displayed rows
    this.currentPage = 1;
    this.updatePaginationStock();
  }

  resetFilters() {
    this.filters = { location: '', material: '' };
    this.filteredStock = [...this.allStock];
    this.currentPage = 1;
    this.updatePaginationStock();
  }

  refreshData() {
    this.loadAllData();
  }

  // ===== KPI Percentages (FIXED) =====
  getLOICompletion(): string {
    if (!this.kpis.totalLOIs) return '0%';
    return ((this.kpis.completedLOIs / this.kpis.totalLOIs) * 100).toFixed(1) + '%';
  }

  getProductionEfficiency(): string {
    if (!this.kpis.totalProductions) return '0%';
    return ((this.kpis.completedProductions / this.kpis.totalProductions) * 100).toFixed(1) + '%';
  }

  getDispatchCompletion(): string {
    if (!this.kpis.totalDispatches) return '0%';
    return ((this.kpis.dispatchedItems / this.kpis.totalDispatches) * 100).toFixed(1) + '%';
  }

  getTotalFilteredQuantity() {
    const totalInMT = this.filteredStock.reduce(
      (sum, s) => sum + (s.totalQuantity || s.quantity || 0),
      0
    );
    return this.convertToKg(totalInMT);
  }

  // Helper method to get individual stock quantity in KG
  getStockQuantityInKg(stock: any): number {
    return this.convertToKg(stock.totalQuantity || stock.quantity || 0);
  }

  // Helper method to get location KPI quantity in KG
  getLocationQuantityInKg(quantity: number): number {
    return this.convertToKg(quantity);
  }

  /* ================= PAGINATION HELPERS FOR STOCK TABLE ================= */
  updatePaginationStock() {
    const total = this.filteredStock.length;
    this.totalPages = Math.max(1, Math.ceil(total / this.pageSize));
    if (this.currentPage > this.totalPages) this.currentPage = this.totalPages;
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.displayedStock = this.filteredStock.slice(start, end);
  }

  changePage(page: number) {
    if (page < 1) page = 1;
    if (page > this.totalPages) page = this.totalPages;
    this.currentPage = page;
    this.updatePaginationStock();
  }

  setPageSize(size: number) {
    this.pageSize = size;
    this.currentPage = 1;
    this.updatePaginationStock();
  }
}
