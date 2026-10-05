import { Routes } from '@angular/router';
import { OrdersPageComponent } from './pages/orders-page.component';

/** Exposed to the shell as './routes'. */
export const ORDERS_ROUTES: Routes = [{ path: '', component: OrdersPageComponent }];
