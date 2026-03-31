import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { catchError, of } from 'rxjs';

/* ===================== INTERFACES ===================== */

interface GrnItem {
  grade?: string;
  section?: string;
  material?: string;
  quantity?: number;
  alreadyReceived?: number;
  receivedQuantity?: number;
  remarks?: string;
}

interface Grn {
  grnNumber?: string;
  orderNo?: string;
  location?: string;
  vehicleNo?: string;

  grnDate?: string;        // formatted for UI
  rawGrnDate?: number;     // timestamp for sorting

  items?: GrnItem[];
}

/* ===================== COMPONENT ===================== */

@Component({
  selector: 'app-rawmaterials',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rawmaterials.html',
  styleUrls: ['./rawmaterials.css']
})
export class Rawmaterials implements OnInit {

  grns: Grn[] = [];
  filteredGrns: Grn[] = [];
  // Pagination
  grnPageSize: number = 10;
  grnCurrentPage: number = 1;
  grnTotalPages: number = 1;
  displayedGrns: Grn[] = [];

  loading = false;
  errorMessage = '';
  searchTerm = '';

  private grnApiUrl = 'http://localhost:8080/api/grn';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getAllGRNs();
  }

  /* ===================== FETCH GRNs ===================== */

  getAllGRNs() {
    this.loading = true;
    this.errorMessage = '';

    this.http.get<any[]>(this.grnApiUrl)
      .pipe(
        catchError(err => {
          console.error('Error fetching GRNs:', err);
          this.errorMessage =
            err.status === 0 ? 'Server not reachable!' : 'Failed to fetch GRNs!';
          this.loading = false;
          return of([]);
        })
      )
      .subscribe(res => {

        this.grns = res.map(grn => {
          const dateObj = grn.grnDate ? new Date(grn.grnDate) : null;

          return {
            ...grn,
            rawGrnDate: dateObj ? dateObj.getTime() : 0,   // ✅ for sorting
            grnDate: dateObj ? dateObj.toLocaleDateString() : ''
          };
        });

        // default: sort newest-first by date
        this.grns.sort((a, b) => (b.rawGrnDate || 0) - (a.rawGrnDate || 0));
          this.filteredGrns = [...this.grns];
          this.grnCurrentPage = 1;
          this.updatePaginationGrns();
        this.loading = false;
      });
  }

  /* ===================== SEARCH ===================== */

  filterGrns() {
    const term = this.searchTerm.toLowerCase().trim();

    if (!term) {
      this.filteredGrns = [...this.grns];
      this.grnCurrentPage = 1;
      this.updatePaginationGrns();
      return;
    }

    this.filteredGrns = this.grns.filter(grn =>
      grn.grnNumber?.toLowerCase().includes(term) ||
      grn.grnDate?.toLowerCase().includes(term) ||
      grn.vehicleNo?.toLowerCase().includes(term)
    );
    this.grnCurrentPage = 1;
    this.updatePaginationGrns();
  }

  /* ===================== SORT ===================== */

  sortGrns(key: keyof Grn) {

    this.filteredGrns = [...this.filteredGrns].sort((a, b) => {

      if (key === 'grnDate') {
        // show latest date first when sorting by date
        return (b.rawGrnDate || 0) - (a.rawGrnDate || 0);
      }

      return (a[key] || '').toString()
        .localeCompare((b[key] || '').toString());
    });
    this.grnCurrentPage = 1;
    this.updatePaginationGrns();
  }

  updatePaginationGrns() {
    const total = this.filteredGrns.length;
    this.grnTotalPages = Math.max(1, Math.ceil(total / this.grnPageSize));
    if (this.grnCurrentPage > this.grnTotalPages) this.grnCurrentPage = this.grnTotalPages;
    const start = (this.grnCurrentPage - 1) * this.grnPageSize;
    const end = start + this.grnPageSize;
    this.displayedGrns = this.filteredGrns.slice(start, end);
  }

  changePageGrns(page: number) {
    if (page < 1) page = 1;
    if (page > this.grnTotalPages) page = this.grnTotalPages;
    this.grnCurrentPage = page;
    this.updatePaginationGrns();
  }

  setGrnPageSize(size: number) {
    this.grnPageSize = size;
    this.grnCurrentPage = 1;
    this.updatePaginationGrns();
  }

  // Get unique material list from all GRNs (case-insensitive, trimmed)
  getUniqueMaterials(): string[] {
    const materialMap = new Map<string, string>();
    this.grns.forEach(grn => {
      grn.items?.forEach(item => {
        if (!item.material) return;
        const key = item.material.trim().toLowerCase();
        if (!materialMap.has(key)) {
          // Title case for display
          const toTitleCase = (str: string) => str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
          materialMap.set(key, toTitleCase(item.material.trim()));
        }
      });
    });
    return Array.from(materialMap.values()).sort();
  }
}
