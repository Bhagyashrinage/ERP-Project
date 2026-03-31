// dashboard.component.ts
import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnDestroy
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

interface DashboardStats {
  totalPOs: number;
  pendingPOs: number;
  completedPOs: number;
  totalLOIs: number;
  pendingLOIs: number;
  totalStock: number;
  lowStockItems: number;
  totalDispatches: number;
  pendingDispatches: number;
  completedDispatches: number;
  dispatchesToday: number;
  monthlyPOValue: number;
  stockValue: number;
}

interface POData {
  id?: number;
  orderNo?: string;
  poNo?: string;
  supplier?: string;
  location?: string;
  status?: string;
  amount?: number;
  orderDate?: string;
  date?: string;
}

interface StockData {
  id?: number;
  itemName?: string;
  name?: string;
  quantity?: number;
  totalQuantity?: number;
  qty?: number;
  minQuantity?: number;
  value?: number;
  category?: string;
  section?: string;
  material?: string;
}

interface DispatchData {
  id?: number;
  dcNo?: string;
  dispatchNo?: string;
  customer?: string;
  customerName?: string;
  status?: string;
  date?: string;
  dispatchDate?: string;
}

interface LOIData {
  id?: number;
  loiNumber?: string;
  customerName?: string;
  customer?: string;
  status?: string;
  amount?: number;
  date?: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('procurementChart') procurementChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('dispatchChart') dispatchChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('stockChart') stockChartRef!: ElementRef<HTMLCanvasElement>;

  stats: DashboardStats = {
    totalPOs: 0,
    pendingPOs: 0,
    completedPOs: 0,
    totalLOIs: 0,
    pendingLOIs: 0,
    totalStock: 0,
    lowStockItems: 0,
    totalDispatches: 0,
    pendingDispatches: 0,
    completedDispatches: 0,
    dispatchesToday: 0,
    monthlyPOValue: 0,
    stockValue: 0
  };

  recentPOs: any[] = [];
  lowStockItems: any[] = [];
  upcomingDispatches: any[] = [];
  recentLOIs: any[] = [];

  monthlyPOData: any[] = [];
  dispatchStatusData: any[] = [];

  stockCategoryData: { name: string; qty: number; minQty: number }[] = [];

  loading = true;
  private baseUrl = 'http://localhost:8080/api';

  private procurementChart: Chart | null = null;
  private dispatchChart: Chart | null = null;
  private stockChart: Chart | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadAllDashboardData();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.createCharts(), 300);
  }

  loadAllDashboardData(): void {
    this.loading = true;
    Promise.all([
      this.loadPOData(),
      this.loadStockData(),
      this.loadDispatchData(),
      this.loadLOIData()
    ])
      .then(() => {
        this.loading = false;
        setTimeout(() => this.createCharts(), 150);
      })
      .catch(err => {
        console.error('Dashboard load error', err);
        this.loading = false;
      });
  }

  loadPOData(): Promise<void> {
    return new Promise(resolve => {
      this.http.get<POData[]>(`${this.baseUrl}/po`).subscribe({
        next: pos => {
          const arr = Array.isArray(pos) ? pos : [];
          this.stats.totalPOs = arr.length;
          this.stats.pendingPOs = arr.filter(p =>
            ['pending', 'open'].includes((p.status || '').toLowerCase())
          ).length;
          this.stats.completedPOs = arr.filter(
            p => (p.status || '').toLowerCase() === 'completed'
          ).length;

          const currentMonth = new Date().getMonth();
          this.stats.monthlyPOValue = arr
            .filter(p => {
              const poDate = new Date(p.orderDate || p.date || '');
              return (
                !isNaN(poDate.getTime()) &&
                poDate.getMonth() === currentMonth
              );
            })
            .reduce((sum, p) => sum + (p.amount || 0), 0);

          this.recentPOs = arr
            .sort((a, b) => {
              const da = new Date(a.orderDate || a.date || '').getTime();
              const db = new Date(b.orderDate || b.date || '').getTime();
              return db - da;
            })
            .slice(0, 5)
            .map(p => ({
              poNo: p.orderNo || p.poNo || 'N/A',
              supplier: p.supplier || p.location || 'N/A',
              status: p.status || 'Open',
              amount: p.amount || 0
            }));

          this.calculateMonthlyPOTrend(arr);
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  loadStockData(): Promise<void> {
    return new Promise(resolve => {
      this.http.get<StockData[]>(`${this.baseUrl}/stock`).subscribe({
        next: stock => {
          const arr = Array.isArray(stock) ? stock : [];
          this.stats.totalStock = arr.length;

          this.stats.stockValue = arr.reduce((sum, s) => {
            const qty =
              Number(
                (s as any).totalQuantity ??
                  (s as any).quantity ??
                  (s as any).qty ??
                  0
              ) || 0;
            const val = Number((s as any).value ?? 0) || 0;
            return sum + qty * val;
          }, 0);

          this.lowStockItems = arr
            .filter(s => {
              const qty =
                Number(
                  (s as any).totalQuantity ??
                    (s as any).quantity ??
                    (s as any).qty ??
                    0
                ) || 0;
              const minQty = (s as any).minQuantity ?? 50;
              return qty < minQty;
            })
            .sort(
              (a, b) =>
                ((a as any).quantity || 0) -
                ((b as any).quantity || 0)
            )
            .slice(0, 5)
            .map(s => ({
              item:
                (s as any).itemName ||
                (s as any).name ||
                'N/A',
              stock:
                Number(
                  (s as any).totalQuantity ??
                    (s as any).quantity ??
                    (s as any).qty ??
                    0
                ) || 0,
              minStock: (s as any).minQuantity ?? 50,
              category:
                (s as any).category ||
                (s as any).section ||
                'Uncategorized'
            }));

          this.stats.lowStockItems = this.lowStockItems.length;
          this.calculateStockByCategory(arr);
          resolve();
        },
        error: () => resolve()
      });
    });
  }

  loadDispatchData(): Promise<void> {
    return new Promise(resolve => {
      this.http.get<DispatchData[]>(`${this.baseUrl}/dispatch`).subscribe({
        next: dispatches => {
          const arr = Array.isArray(dispatches) ? dispatches : [];
          const normalize = (s: string | undefined | null) =>
            (s || '').trim().toLowerCase();

          const isPending = (s: string | undefined | null) =>
            ['pending', 'open', 'created'].includes(normalize(s));

          const isDelivered = (s: string | undefined | null) =>
            ['delivered', 'completed'].includes(normalize(s));

          const isInTransit = (s: string | undefined | null) =>
            ['in transit', 'in-transit', 'dispatched', 'in progress'].includes(normalize(s));

          this.stats.totalDispatches = arr.length;
          this.stats.pendingDispatches = arr.filter(d => isPending(d.status)).length;
          this.stats.completedDispatches = arr.filter(d => isDelivered(d.status)).length;
          const inTransitCount = arr.filter(d => isInTransit(d.status)).length;

          this.upcomingDispatches = arr
            .filter(d => !isDelivered(d.status))
            .sort((a, b) => {
              const da = new Date(a.date || a.dispatchDate || '').getTime();
              const db = new Date(b.date || b.dispatchDate || '').getTime();
              return da - db;
            })
            .slice(0, 5)
            .map(d => ({
              dcNo: d.dcNo || d.dispatchNo || 'N/A',
              customer: d.customer || d.customerName || 'N/A',
              status: d.status || 'Pending'
            }));

          const today = new Date().toISOString().split('T')[0];
          const todayDispatches = arr.filter(d => {
            const dispatchDate = (d.date || d.dispatchDate || '').split('T')[0];
            return dispatchDate === today;
          });
          this.stats.dispatchesToday = todayDispatches.length;

          this.dispatchStatusData = [
            { name: 'Pending', value: this.stats.pendingDispatches, color: '#f59e0b' },
            { name: 'In Transit', value: inTransitCount, color: '#3b82f6' },
            { name: 'Delivered', value: this.stats.completedDispatches, color: '#10b981' }
          ];

          resolve();
        },
        error: () => resolve()
      });
    });
  }

  loadLOIData(): Promise<void> {
    return new Promise(resolve => {
      this.http.get<LOIData[]>(`${this.baseUrl}/loi`).subscribe({
        next: lois => {
          const arr = Array.isArray(lois) ? lois : [];
          this.stats.totalLOIs = arr.length;
          this.stats.pendingLOIs = arr.filter(
            l => (l.status || '').toLowerCase() === 'pending'
          ).length;

          this.recentLOIs = arr
            .sort((a, b) => {
              const da = new Date(a.date || '').getTime();
              const db = new Date(b.date || '').getTime();
              return db - da;
            })
            .slice(0, 5)
            .map(l => ({
              loiNumber: l.loiNumber || 'N/A',
              customer: l.customerName || l.customer || 'N/A',
              status: l.status || 'Pending',
              amount: l.amount || 0
            }));

          resolve();
        },
        error: () => resolve()
      });
    });
  }

  calculateMonthlyPOTrend(pos: POData[]): void {
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    const currentYear = new Date().getFullYear();
    this.monthlyPOData = months.map((month, index) => {
      const monthPOs = (pos || []).filter(p => {
        const poDate = new Date(p.orderDate || p.date || '');
        return !isNaN(poDate.getTime()) &&
               poDate.getMonth() === index &&
               poDate.getFullYear() === currentYear;
      });
      return {
        month,
        placed: monthPOs.length,
        value: monthPOs.reduce((sum, p) => sum + (p.amount || 0), 0)
      };
    });
  }

  calculateStockByCategory(stock: StockData[]): void {
    const categoryMap = new Map<string, { qty: number; minQty: number }>();

    (stock || []).forEach(s => {
      const category =
        (s as any).category ||
        (s as any).section ||
        (s as any).material ||
        'Uncategorized';

      const qty =
        Number(
          (s as any).totalQuantity ??
            (s as any).quantity ??
            (s as any).qty ??
            0
        ) || 0;

      const minQty = Number((s as any).minQuantity ?? 50) || 0;

      if (!categoryMap.has(category)) {
        categoryMap.set(category, { qty: 0, minQty: 0 });
      }

      const current = categoryMap.get(category)!;
      current.qty += qty;
      current.minQty += minQty;
    });

    this.stockCategoryData = Array.from(categoryMap.entries()).map(
      ([name, v]) => ({
        name,
        qty: v.qty,
        minQty: v.minQty
      })
    );
  }

  createCharts(): void {
    this.createProcurementChart();
    this.createDispatchChart();
    this.createStockChart();
  }

  private createProcurementChart(): void {
    try {
      if (!this.procurementChartRef) return;
      const ctx = this.procurementChartRef.nativeElement.getContext('2d');
      if (!ctx) return;

      if (this.procurementChart) {
        this.procurementChart.destroy();
        this.procurementChart = null;
      }

  // show full year from Jan to Dec
  const labels = this.monthlyPOData.map(d => d.month);
  const values = this.monthlyPOData.map(d => d.placed);

      this.procurementChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'POs Placed',
            data: values,
            borderColor: '#10b981',
            backgroundColor: 'transparent',
            borderWidth: 3,
            pointRadius: 4,
            pointBackgroundColor: '#10b981',
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: true, position: 'bottom' } },
          scales: {
            y: { beginAtZero: true },
            x: {}
          }
        }
      });

    } catch (e) {}
  }

  private createDispatchChart(): void {
    try {
      if (!this.dispatchChartRef) return;
      const ctx = this.dispatchChartRef.nativeElement.getContext('2d');
      if (!ctx) return;

      if (this.dispatchChart) {
        this.dispatchChart.destroy();
        this.dispatchChart = null;
      }

      const labels = this.dispatchStatusData.map(d => d.name);
      const values = this.dispatchStatusData.map(d => d.value || 0);
      const colors = this.dispatchStatusData.map(d => d.color);

      this.dispatchChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels,
          datasets: [{
            data: values,
            backgroundColor: colors,
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: '60%',
          plugins: {
            legend: { display: true, position: 'bottom' }
          }
        }
      });

    } catch (e) {}
  }

  private createStockChart(): void {
    try {
      if (!this.stockChartRef) return;
      const ctx = this.stockChartRef.nativeElement.getContext('2d');
      if (!ctx) return;

      if (this.stockChart) {
        this.stockChart.destroy();
        this.stockChart = null;
      }

      const labels = this.stockCategoryData.map(d => d.name);
      const qtyValues = this.stockCategoryData.map(d => d.qty);
      const minValues = this.stockCategoryData.map(d => d.minQty);

      this.stockChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels,
          datasets: [
            {
              label: 'Available Qty',
              data: qtyValues,
              backgroundColor: '#3b82f6'
            },
            {
              label: 'Minimum Required',
              data: minValues,
              backgroundColor: '#f97316'
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: true, position: 'bottom' }
          },
          scales: {
            y: { beginAtZero: true },
            x: {}
          }
        }
      });

    } catch (e) {}
  }

  navigateToPO(): void {
    this.router.navigate(['/po']); 
  }

  navigateToStock(): void {
    this.router.navigate(['/inventory']); 
  }

  navigateToDispatch(): void {
    this.router.navigate(['/dispatch']); 
  }

  navigateToLOI(): void {
    this.router.navigate(['/loi']); 
  }

  refreshDashboard(): void {
    this.loadAllDashboardData();
  }

  ngOnDestroy(): void {
    if (this.procurementChart) this.procurementChart.destroy();
    if (this.dispatchChart) this.dispatchChart.destroy();
    if (this.stockChart) this.stockChart.destroy();
  }
}