import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

/*
  Restored Jobwork component with implementations matching template bindings.
*/

interface JobWorkLine { itemCode: string; description?: string; qtyIssued?: number; weight?: number; uom?: string; remarks?: string; }
interface JobWorkChallan { id?: any; challanNo: string; challanDate: string; supplier: string; vehicleNo: string; lines: JobWorkLine[]; status: string; }
interface JobWorkReceipt { id?: any; challanId: number; challanNo: string; receiptDate: string; receiverName: string; qtyReceived: number; actualWeight: number; kataWeight: number; difference: number; jobCharge: number; status: string; }

@Component({
  selector: 'app-jobwork',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './jobwork.html',
  styleUrls: ['./jobwork.css']
})
export class Jobwork implements OnInit {
  activeTab: 'issue' | 'receive' | 'list' | 'invoices' = 'issue';

  issueChallan: JobWorkChallan = { challanNo: '', challanDate: new Date().toISOString().split('T')[0], supplier: '', vehicleNo: '', lines: [{ itemCode: '' }], status: 'ISSUED' } as any;
  receiptForm: JobWorkReceipt = { challanId: 0, challanNo: '', receiptDate: new Date().toISOString().split('T')[0], receiverName: '', qtyReceived: 0, actualWeight: 0, kataWeight: 0, difference: 0, jobCharge: 0, status: 'PENDING' } as any;

  challans: JobWorkChallan[] = [];
  receipts: JobWorkReceipt[] = [];
  filteredChallans: JobWorkChallan[] = [];
  filteredReceipts: JobWorkReceipt[] = [];

  challanPageSize = 10; challanCurrentPage = 1; challanTotalPages = 1; displayedChallans: JobWorkChallan[] = [];
  receiptPageSize = 10; receiptCurrentPage = 1; receiptTotalPages = 1; displayedReceipts: JobWorkReceipt[] = [];

  Math = Math; loading = false; message = ''; errorMessage = '';

  showDetailModal = false;
  selectedChallan: JobWorkChallan | null = null;
  differenceMessage = '';

  searchChallanNo = ''; filterSupplier = ''; filterStatus = ''; filterReceiptStatus = '';

  private challanApiUrl = 'http://localhost:8080/api/jobwork/challans';
  private receiptApiUrl = 'http://localhost:8080/api/jobwork/receipts';

  constructor(private http: HttpClient) {}

  ngOnInit(): void { this.getAllChallans(); this.getAllReceipts(); }

  switchTab(tab: 'issue' | 'receive' | 'list' | 'invoices') { this.activeTab = tab; this.message = ''; if (tab === 'list') this.getAllChallans(); if (tab === 'invoices') this.getAllReceipts(); }

  addIssueLine() { this.issueChallan.lines = this.issueChallan.lines || []; this.issueChallan.lines.push({ itemCode: '' }); }
  removeIssueLine(i: number) { this.issueChallan.lines?.splice(i, 1); }

  issueChallanSubmit() { this.http.post(this.challanApiUrl, this.issueChallan).subscribe({ next: () => { this.message = 'Issued'; this.resetIssueForm(); this.getAllChallans(); }, error: () => this.message = 'Failed' }); }
  resetIssueForm() { this.issueChallan = { challanNo: '', challanDate: new Date().toISOString().split('T')[0], supplier: '', vehicleNo: '', lines: [{ itemCode: '' }], status: 'ISSUED' } as any; }

  // paste/input sanitizers used by template
  onPasteNumbers(e: any) { const text = e?.clipboardData?.getData?.('text') || (window as any).clipboardData?.getData?.('Text') || ''; const cleaned = (text || '').toString().replace(/\D+/g, ''); const t = e.target || e.srcElement; if (t) t.value = cleaned; e.preventDefault?.(); }
  onPasteLetters(e: any) { const text = e?.clipboardData?.getData?.('text') || (window as any).clipboardData?.getData?.('Text') || ''; const cleaned = (text || '').toString().replace(/[^A-Za-z ]+/g, ''); const t = e.target || e.srcElement; if (t) t.value = cleaned; e.preventDefault?.(); }
  onPasteAlphaNumeric(e: any) { const text = e?.clipboardData?.getData?.('text') || (window as any).clipboardData?.getData?.('Text') || ''; const cleaned = (text || '').toString().replace(/[^A-Za-z0-9 \-\/]+/g, ''); const t = e.target || e.srcElement; if (t) t.value = cleaned; e.preventDefault?.(); }

  allowOnlyLetters(e: KeyboardEvent) { if (!/^[a-zA-Z ]$/.test((e as KeyboardEvent).key)) (e as KeyboardEvent).preventDefault(); }
  allowAlphaNumeric(e: KeyboardEvent) { if (!/^[a-zA-Z0-9 \-\/]$/.test((e as KeyboardEvent).key)) (e as KeyboardEvent).preventDefault(); }
  allowOnlyNumbers(e: KeyboardEvent) { if (!/^[0-9]$/.test((e as KeyboardEvent).key)) (e as KeyboardEvent).preventDefault(); }

  onChallanNoInput(e: any) { const cleaned = (e.target?.value || '').toString().replace(/\D+/g, ''); if (e.target) e.target.value = cleaned; this.issueChallan.challanNo = cleaned; }
  onReceiverInput(e: any) { const cleaned = (e.target?.value || '').toString().replace(/[^A-Za-z ]+/g, ''); if (e.target) e.target.value = cleaned; this.issueChallan.supplier = cleaned; }
  onVehicleInput(e: any) { const cleaned = (e.target?.value || '').toString().replace(/[^A-Za-z0-9\- ]+/g, ''); if (e.target) e.target.value = cleaned; this.issueChallan.vehicleNo = cleaned; }
  onReceiptReceiverInput(e: any) { const cleaned = (e.target?.value || '').toString().replace(/[^A-Za-z ]+/g, ''); if (e.target) e.target.value = cleaned; this.receiptForm.receiverName = cleaned; }

  updateDifference() { const d = (this.receiptForm.actualWeight || 0) - (this.receiptForm.kataWeight || 0); this.receiptForm.difference = d; this.differenceMessage = d === 0 ? 'No difference' : (d > 0 ? `Penalty ₹${(d * 100).toFixed(2)}` : `Bonus ₹${(Math.abs(d) * 50).toFixed(2)}`); }
  onChallanSelect(challanNo?: string) { const c = this.challans.find(x => x.challanNo === (challanNo || '')); this.receiptForm.challanId = c && c.id ? Number(c.id) : 0; }
  recordReceipt() { if (!this.receiptForm.challanId) { this.message = 'Invalid challan'; return; } this.updateDifference(); this.http.post(this.receiptApiUrl, this.receiptForm).subscribe({ next: () => { this.message = 'Recorded'; this.resetReceiptForm(); this.getAllReceipts(); }, error: () => this.message = 'Failed' }); }
  resetReceiptForm() { this.receiptForm = { challanId: 0, challanNo: '', receiptDate: new Date().toISOString().split('T')[0], receiverName: '', qtyReceived: 0, actualWeight: 0, kataWeight: 0, difference: 0, jobCharge: 0, status: 'PENDING' } as any; this.differenceMessage = ''; }

  getAllChallans() { this.loading = true; this.http.get<JobWorkChallan[]>(this.challanApiUrl).subscribe({ next: (res: any) => { const list = (res || []).map((l: any) => ({ ...l, lines: l.lines || [], status: l.status || 'ISSUED', supplier: l.supplier || '', vehicleNo: l.vehicleNo || '', challanDate: l.challanDate || '' })); this.challans = list; this.applyFilters(); this.loading = false; }, error: () => { this.errorMessage = 'Failed'; this.loading = false; } }); }
  applyFilters() { let filtered = this.challans.slice(); const s = (this.searchChallanNo || '').toString().trim().toLowerCase(); if (s) filtered = filtered.filter(l => (l.challanNo || '').toLowerCase().includes(s) || (l.supplier || '').toLowerCase().includes(s)); if (this.filterStatus) filtered = filtered.filter(l => (l.status || '') === this.filterStatus); this.filteredChallans = filtered; this.challanCurrentPage = 1; this.updateChallanPagination(); }

  getAllReceipts() { this.loading = true; this.http.get<JobWorkReceipt[]>(this.receiptApiUrl).subscribe({ next: (res: any) => { this.receipts = (res || []).map((r: any) => ({ ...r, difference: (r.actualWeight || 0) - (r.kataWeight || 0), status: r.status || 'PENDING' })); this.applyReceiptFilters(); this.loading = false; }, error: () => this.loading = false }); }
  applyReceiptFilters() {
    const s = (this.searchChallanNo || '').toString().trim().toLowerCase();
    let filtered = this.receipts.slice();
    if (s) {
      filtered = filtered.filter(r => (r.challanNo || '').toLowerCase().includes(s) || (r.receiverName || '').toLowerCase().includes(s));
    }
    if (this.filterReceiptStatus) filtered = filtered.filter(r => r.status === this.filterReceiptStatus);
    this.filteredReceipts = filtered;
    this.receiptCurrentPage = 1;
    this.updateReceiptPagination();
  }

  clearReceiptFilters() { this.filterReceiptStatus = ''; this.searchChallanNo = ''; this.applyReceiptFilters(); }

  approveReceipt(id?: any) { if (!id) return; this.http.post(`${this.receiptApiUrl}/${id}/approve?approvedBy=admin`, {}).subscribe({ next: () => { this.message = 'Approved'; this.getAllReceipts(); }, error: () => this.message = 'Failed' }); }
  rejectReceipt(id?: any) { if (!id) return; this.http.post(`${this.receiptApiUrl}/${id}/reject?rejectedBy=admin`, {}).subscribe({ next: () => { this.message = 'Rejected'; this.getAllReceipts(); }, error: () => this.message = 'Failed' }); }

  deleteChallan(id: any) { if (!id) return; this.http.delete(`${this.challanApiUrl}/${id}`).subscribe({ next: () => { this.message = 'Deleted'; this.getAllChallans(); }, error: () => this.message = 'Failed' }); }

  openDetailModal(challan: JobWorkChallan) { this.selectedChallan = challan; this.showDetailModal = true; }
  closeDetailModal() { this.selectedChallan = null; this.showDetailModal = false; }

  getWeightDifference(r: JobWorkReceipt) { return (r?.actualWeight || 0) - (r?.kataWeight || 0); }
  getPenaltyOrBonus(r: JobWorkReceipt) { const d = this.getWeightDifference(r); if (d > 0) return `Penalty ₹${(d * 100).toFixed(2)}`; if (d < 0) return `Bonus ₹${(Math.abs(d) * 50).toFixed(2)}`; return '-'; }

  getTotalIssuedQty() { return (this.issueChallan.lines || []).reduce((s, l) => s + (Number((l as any).qtyIssued) || 0), 0); }
  getTotalIssuedWeight() { return (this.issueChallan.lines || []).reduce((s, l) => s + (Number((l as any).weight) || 0), 0); }

  updateChallanPagination() { const total = this.filteredChallans.length; this.challanTotalPages = Math.max(1, Math.ceil(total / this.challanPageSize)); if (this.challanCurrentPage > this.challanTotalPages) this.challanCurrentPage = this.challanTotalPages; const start = (this.challanCurrentPage - 1) * this.challanPageSize; this.displayedChallans = this.filteredChallans.slice(start, start + this.challanPageSize); }
  changeChallanPage(p: number) { this.challanCurrentPage = Math.max(1, Math.min(this.challanTotalPages, p)); this.updateChallanPagination(); }
  setChallanPageSize(sz: number) { this.challanPageSize = sz; this.challanCurrentPage = 1; this.updateChallanPagination(); }

  updateReceiptPagination() { const total = this.filteredReceipts.length; this.receiptTotalPages = Math.max(1, Math.ceil(total / this.receiptPageSize)); if (this.receiptCurrentPage > this.receiptTotalPages) this.receiptCurrentPage = this.receiptTotalPages; const start = (this.receiptCurrentPage - 1) * this.receiptPageSize; this.displayedReceipts = this.filteredReceipts.slice(start, start + this.receiptPageSize); }
  changeReceiptPage(p: number) { this.receiptCurrentPage = Math.max(1, Math.min(this.receiptTotalPages, p)); this.updateReceiptPagination(); }
  setReceiptPageSize(sz: number) { this.receiptPageSize = sz; this.receiptCurrentPage = 1; this.updateReceiptPagination(); }

  exportToExcel() { this.message = 'Export coming soon'; setTimeout(() => this.message = '', 2000); }

  printChallan(_challan: JobWorkChallan) { const popup = window.open('', '_blank', 'width=800,height=600'); if (popup) { popup.document.write('<pre>' + 'Challan: ' + (_challan?.challanNo || '-') + '</pre>'); popup.document.close(); } }

}

