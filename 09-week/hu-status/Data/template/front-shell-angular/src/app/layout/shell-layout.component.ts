import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SessionService } from '../core/auth/session.service';

@Component({
  selector: 'app-shell-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <header>
      <nav aria-label="Main">
        <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Home</a>
        <a routerLink="/orders" routerLinkActive="active">Orders</a>
      </nav>
      @if (session.token()) {
        <button type="button" (click)="session.clear()">Sign out</button>
      }
    </header>
    <main><router-outlet /></main>
  `,
})
export class ShellLayoutComponent {
  readonly session = inject(SessionService);
}
