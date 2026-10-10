import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * Centered card surface for unauthenticated flows (routes branch ③).
 * Parent route is guarded by guestGuard.
 */
@Component({
  selector: 'app-auth-layout',
  imports: [RouterOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-h-dvh items-center justify-center bg-muted/40 px-4 py-12">
      <div class="w-full max-w-sm">
        <router-outlet />
      </div>
    </div>
  `,
  styles: [],
})
export class AuthLayout {}