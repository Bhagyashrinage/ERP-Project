import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FormsModule,RouterModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App implements AfterViewInit, OnDestroy{
  private _resizeHandler = () => this.updateOverflowIndicators();
  private _ro: ResizeObserver | null = null;

  ngAfterViewInit(): void {
    // initial check and setup observers
    this.updateOverflowIndicators();

    // observe size changes of any table-responsive containers
    try {
      this._ro = new ResizeObserver(() => this.updateOverflowIndicators());
      document.querySelectorAll('.table-responsive').forEach(el => this._ro!.observe(el as Element));
    } catch (e) {
      // ResizeObserver may not be available in some environments; fallback to resize event
    }

    window.addEventListener('resize', this._resizeHandler);
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', this._resizeHandler);
    if (this._ro) { this._ro.disconnect(); this._ro = null; }
  }

  private updateOverflowIndicators() {
    // Add or remove `is-overflowing` class based on scrollWidth vs clientWidth
    document.querySelectorAll('.table-responsive').forEach(el => {
      const e = el as HTMLElement;
      // Only consider elements that are rendered and have positive clientWidth
      const isVisible = e.offsetParent !== null && e.clientWidth > 0;
      const isOverflowing = isVisible && e.scrollWidth > e.clientWidth + 1; // small tolerance
      if (isOverflowing) e.classList.add('is-overflowing'); else e.classList.remove('is-overflowing');
    });
  }
}
