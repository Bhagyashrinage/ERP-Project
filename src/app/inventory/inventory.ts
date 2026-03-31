import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';

interface Stock {
  id?: number;
  location: string;
  material: string;
  grade: string;
  section: string;
  steelWidth?: number;
  length: number;
  type: string;
  totalQuantity: number;
  lastUpdated: string;
}

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory.html',
  styleUrls: ['./inventory.css']
})
export class Inventory implements OnInit {

  stockData: Stock[] = [];
  filteredStock: Stock[] = [];

  locations: string[] = [];
  materials: string[] = [];

  selectedLocation: string = '';
  selectedMaterial: string = '';
  loading = false;
  errorMessage = '';

  // Pagination variables
  pageSize: number = 10;
  currentPage: number = 1;
  totalPages: number = 1;
  displayedStock: Stock[] = [];

  private apiUrl = 'http://localhost:8080/api/stock';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchAllStock();
  }

  // Fetch all stock
  fetchAllStock() {
    this.loading = true;
    this.errorMessage = '';
    this.http.get<Stock[]>(this.apiUrl).pipe(
      finalize(() => this.loading = false)
    ).subscribe({
      next: (res) => {
        // Sort by lastUpdated descending (newest first)
        this.stockData = res.sort(
          (a, b) => new Date(b.lastUpdated).getTime() - new Date(a.lastUpdated).getTime()
        );
        this.populateFilters();
        this.applyFilters();
        this.currentPage = 1;
        this.updatePagination();
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Failed to fetch stock data.';
      }
    });
  }

  // Populate filter dropdowns (case-insensitive uniqueness, trimmed)
  populateFilters() {
    const toTitleCase = (str: string) =>
      str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();

    // ----- Locations -----
    const locationMap = new Map<string, string>();
    this.stockData.forEach(item => {
      const key = item.location.trim().toLowerCase();
      if (!locationMap.has(key)) {
        locationMap.set(key, toTitleCase(item.location.trim()));
      }
    });
    this.locations = Array.from(locationMap.values()).sort();

    // ----- Materials -----
    const materialMap = new Map<string, string>();
    this.stockData.forEach(item => {
      if (!item.material) return;
      const key = item.material.trim().toLowerCase();
      if (!materialMap.has(key)) {
        materialMap.set(key, toTitleCase(item.material.trim()));
      }
    });
    this.materials = Array.from(materialMap.values()).sort();
  }

  // Apply filters (case-insensitive) and maintain descending order
  applyFilters() {
    this.filteredStock = this.stockData
      .filter(item =>
        (this.selectedLocation
          ? item.location.toLowerCase() === this.selectedLocation.toLowerCase()
          : true) &&
        (this.selectedMaterial
          ? item.material.toLowerCase() === this.selectedMaterial.toLowerCase()
          : true)
      );
    this.currentPage = 1;
    this.updatePagination();
  }

  updatePagination() {
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
    this.updatePagination();
  }

  setPageSize(size: number) {
    this.pageSize = size;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Reset filters
  resetFilters() {
    this.selectedLocation = '';
    this.selectedMaterial = '';
    this.applyFilters();
  }

  // TrackBy for ngFor performance
  trackByStock(index: number, stock: Stock) {
    return stock.id || index;
  }
}
