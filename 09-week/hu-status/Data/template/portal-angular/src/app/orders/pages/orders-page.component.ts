import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription } from 'rxjs';
import { asApiError } from '../../shell-contract';
import { OrderFormComponent } from '../components/order-form.component';
import { OrdersApiService } from '../data/orders-api.service';
import { formatCents } from '../model/money';
import { Order, OrderStatus, Page } from '../model/order';

const LIMIT = 20;

/** Every view that loads data has four states, and all four are designed. */
type View =
  | { state: 'loading' }
  | { state: 'error'; message: string }
  | { state: 'empty' }
  | { state: 'ready'; page: Page<Order> };

@Component({
  selector: 'app-orders-page',
  imports: [OrderFormComponent],
  template: `
    <section aria-labelledby="orders-title">
      <h1 id="orders-title">Orders</h1>

      <app-order-form (placed)="onPlaced($event)" />
      @if (notice()) { <p role="status">{{ notice() }}</p> }

      <label for="status-filter">Status</label>
      <select id="status-filter" [value]="status()" (change)="filter($any($event.target).value)">
        <option value="">All</option>
        <option value="PENDING">Pending</option>
        <option value="CONFIRMED">Confirmed</option>
        <option value="CANCELLED">Cancelled</option>
      </select>

      @switch (view().state) {
        @case ('loading') { <p role="status">Loading orders…</p> }
        @case ('error') {
          <div role="alert">
            <p>{{ errorMessage() }}</p>
            <button type="button" (click)="load()">Try again</button>
          </div>
        }
        @case ('empty') { <p>There are no orders yet.</p> }
        @case ('ready') {
          <table>
            <caption>Orders, newest first</caption>
            <thead>
              <tr><th scope="col">Order</th><th scope="col">Customer</th><th scope="col">Total</th>
                  <th scope="col">Status</th><th scope="col">Created</th></tr>
            </thead>
            <tbody>
              @for (o of orders(); track o.id) {
                <tr>
                  <td>{{ o.id }}</td><td>{{ o.customerId }}</td><td>{{ money(o.totalCents) }}</td>
                  <td>{{ o.status }}</td><td>{{ date(o.createdAt) }}</td>
                </tr>
              }
            </tbody>
          </table>
          <nav aria-label="Pages">
            <button type="button" [disabled]="page() <= 1" (click)="go(page() - 1)">Previous</button>
            <span>Page {{ page() }} of {{ totalPages() }}</span>
            <button type="button" [disabled]="page() >= totalPages()" (click)="go(page() + 1)">Next</button>
          </nav>
        }
      }
    </section>
  `,
})
export class OrdersPageComponent {
  private readonly api = inject(OrdersApiService);
  private readonly destroyRef = inject(DestroyRef);
  private request?: Subscription;

  readonly page = signal(1);
  readonly status = signal<OrderStatus | ''>('');
  readonly view = signal<View>({ state: 'loading' });
  readonly notice = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.request?.unsubscribe(); // a newer request replaces an older one
    this.view.set({ state: 'loading' });
    this.request = this.api.list(this.page(), LIMIT, this.status() || undefined)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (page) => this.view.set(page.data.length ? { state: 'ready', page } : { state: 'empty' }),
        error: (err: unknown) => this.view.set({ state: 'error', message: asApiError(err).userMessage }),
      });
  }

  filter(value: string): void {
    this.status.set(value as OrderStatus | '');
    this.page.set(1);
    this.load();
  }

  go(page: number): void {
    this.page.set(page);
    this.load();
  }

  onPlaced(id: string): void {
    this.notice.set(`Order ${id} placed.`);
    this.load();
  }

  orders(): Order[] {
    const v = this.view();
    return v.state === 'ready' ? v.page.data : [];
  }

  totalPages(): number {
    const v = this.view();
    return v.state === 'ready' ? v.page.meta.totalPages : 1;
  }

  errorMessage(): string {
    const v = this.view();
    return v.state === 'error' ? v.message : '';
  }

  money(cents: number): string {
    return formatCents(cents);
  }

  date(iso: string): string {
    return new Date(iso).toLocaleString();
  }
}
