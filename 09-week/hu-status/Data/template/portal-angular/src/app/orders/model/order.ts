/** The contract of <abbr>-orders-api. Field names match the API exactly (camelCase). */
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

export interface Order {
  id: string;
  customerId: string;
  totalCents: number; // minor units: never a float
  status: OrderStatus;
  createdAt: string;
}

export interface PlaceOrderRequest {
  customerId: string;
  totalCents: number;
}

/** The shared pagination shape: every list of the system answers like this. */
export interface Page<T> {
  data: T[];
  meta: { page: number; limit: number; total: number; totalPages: number };
}
