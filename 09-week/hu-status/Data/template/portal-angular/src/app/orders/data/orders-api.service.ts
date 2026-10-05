import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Order, OrderStatus, Page, PlaceOrderRequest } from '../model/order';

/**
 * Typed calls to this domain's endpoints. The injected HttpClient is the SHELL's:
 * its interceptor completes the '/api/v1/...' path, attaches the token and the
 * correlation id, applies the timeout and normalises every error.
 */
@Injectable({ providedIn: 'root' })
export class OrdersApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/v1/orders';

  list(page: number, limit: number, status?: OrderStatus): Observable<Page<Order>> {
    let params = new HttpParams().set('page', page).set('limit', limit);
    if (status) params = params.set('status', status);
    return this.http.get<Page<Order>>(this.base, { params });
  }

  get(id: string): Observable<Order> {
    return this.http.get<Order>(`${this.base}/${encodeURIComponent(id)}`);
  }

  /** The key comes from the caller: the same attempt, retried, reuses it. */
  place(body: PlaceOrderRequest, idempotencyKey: string): Observable<{ id: string }> {
    return this.http.post<{ id: string }>(this.base, body, { headers: { 'Idempotency-Key': idempotencyKey } });
  }

  confirm(id: string): Observable<Order> {
    return this.http.post<Order>(`${this.base}/${encodeURIComponent(id)}/confirm`, null);
  }

  cancel(id: string): Observable<Order> {
    return this.http.post<Order>(`${this.base}/${encodeURIComponent(id)}/cancel`, null);
  }
}
