import { Component, inject, output, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { asApiError } from '../../shell-contract';
import { OrdersApiService } from '../data/orders-api.service';
import { toCents } from '../model/money';

const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const AMOUNT = /^\d{1,9}([.,]\d{1,2})?$/;

@Component({
  selector: 'app-order-form',
  imports: [ReactiveFormsModule],
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()" novalidate aria-labelledby="new-order-title">
      <h2 id="new-order-title">New order</h2>

      <label for="customerId">Customer id</label>
      <input id="customerId" formControlName="customerId" autocomplete="off"
             [attr.aria-invalid]="shows('customerId')" [attr.aria-describedby]="shows('customerId') ? 'customerId-error' : null" />
      @if (shows('customerId')) {
        <p id="customerId-error">{{ serverError('customerId') ?? 'Enter the customer id (a UUID).' }}</p>
      }

      <label for="total">Total</label>
      <input id="total" formControlName="total" inputmode="decimal"
             [attr.aria-invalid]="shows('total')" [attr.aria-describedby]="shows('total') ? 'total-error' : null" />
      @if (shows('total')) {
        <p id="total-error">{{ serverError('total') ?? 'Enter a positive amount with at most two decimals.' }}</p>
      }

      <button type="submit" [disabled]="pending()" [attr.aria-busy]="pending()">
        {{ pending() ? 'Placing…' : 'Place order' }}
      </button>
      @if (failure()) { <p role="alert">{{ failure() }}</p> }
    </form>
  `,
})
export class OrderFormComponent {
  readonly placed = output<string>();
  private readonly api = inject(OrdersApiService);
  readonly form = inject(NonNullableFormBuilder).group({
    customerId: ['', [Validators.required, Validators.pattern(UUID)]],
    total: ['', [Validators.required, Validators.pattern(AMOUNT)]],
  });
  readonly pending = signal(false);
  readonly failure = signal<string | null>(null);
  // One key per intention: a retry of the same data reuses it, so a timeout
  // followed by a second click never creates two orders. Editing starts a new one.
  private attemptKey: string | null = null;

  constructor() {
    this.form.valueChanges.subscribe(() => (this.attemptKey = null));
  }

  shows(name: 'customerId' | 'total'): boolean {
    const control = this.form.controls[name];
    return control.invalid && (control.touched || control.dirty);
  }

  serverError(name: 'customerId' | 'total'): string | null {
    return this.form.controls[name].errors?.['server'] ?? null;
  }

  submit(): void {
    this.form.markAllAsTouched();
    const cents = toCents(this.form.controls.total.value);
    if (this.form.invalid || cents === null || this.pending()) return;

    this.attemptKey ??= crypto.randomUUID();
    this.pending.set(true);
    this.failure.set(null);
    this.api.place({ customerId: this.form.controls.customerId.value.trim(), totalCents: cents }, this.attemptKey)
      .subscribe({
        next: ({ id }) => {
          this.pending.set(false);
          this.form.reset();
          this.attemptKey = null;
          this.placed.emit(id);
        },
        error: (err: unknown) => {
          const error = asApiError(err);
          this.pending.set(false);
          // The server has the last word: its field errors go next to each field.
          const fields: Record<string, 'customerId' | 'total'> = { customerId: 'customerId', totalCents: 'total' };
          for (const d of error.details) {
            const control = fields[d.field] && this.form.controls[fields[d.field]];
            control?.setErrors({ server: d.message });
          }
          this.failure.set(error.userMessage);
        },
      });
  }
}
