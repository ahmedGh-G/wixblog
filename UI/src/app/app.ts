
import { Component, computed, signal } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { HlmToggleGroupImports } from '@spartan-ng/helm/toggle-group';

@Component({
  selector: 'app-root',
  imports: [HlmCardImports, HlmLabelImports, HlmInputImports, HlmButtonImports, HlmToggleGroupImports],
  host: { class: 'w-full max-w-md grid gap-4 p-6 mx-auto' },
  template: `
    <!-- Theme Spacing Toggle Group -->
    <hlm-toggle-group type="single" size="sm" variant="outline" spacing="2" [(value)]="spacing">
      @for (space of spacingOptions; track $index) {
        <button  hlmToggleGroupItem [value]="space.value" [aria-label]="'Toggle ' + space.label">
          {{ space.label }}
        </button>
      }
    </hlm-toggle-group>

    <!-- Card utilizing your design token backgrounds and fonts -->
    <hlm-card class="w-full font-sans border-brand-border bg-brand-bg shadow-sm" [class]="spacingClass()">
      <hlm-card-header>
        <!-- Title using your elegant serif brand font -->
        <h3 hlmCardTitle class="font-serif text-2xl font-bold tracking-tight text-brand-primary">
          Login to your account
        </h3>
        <p hlmCardDescription class="text-brand-muted">
          Enter your email below to login to your account
        </p>

        <div hlmCardAction>
          <!-- Using your green brand accent token natively via standard tailwind -->
          <button hlmBtn variant="link" class="text-brand-accent hover:text-brand-accent-hover font-medium">
            Sign Up
          </button>
        </div>
      </hlm-card-header>

      <div hlmCardContent>
        <form id="login-form">
          <div class="flex flex-col gap-6">
            <div class="grid gap-2">
              <label hlmLabel for="email" class="text-brand-primary font-medium">Email</label>
              <input type="email" id="email" placeholder="m@example.com" required hlmInput class="bg-brand-surface border-brand-border focus-visible:ring-brand-accent" />
            </div>

            <div class="grid gap-2">
              <div class="flex items-center">
                <label hlmLabel for="password" class="text-brand-primary font-medium">Password</label>
                <a href="#" class="ml-auto inline-block text-xs font-medium text-brand-accent hover:text-brand-accent-hover underline underline-offset-4">
                  Forgot your password?
                </a>
              </div>
              <input type="password" id="password" hlmInput class="bg-brand-surface border-brand-border focus-visible:ring-brand-accent" />
            </div>
          </div>
        </form>
      </div>

      <hlm-card-footer class="flex-col gap-3">
        <!-- Integrated your exact custom utility ink primary button -->
        <button type="submit" class="btn-ink-primary w-full" form="login-form">
          Login
        </button>

        <!-- Integrated your exact custom utility ink secondary button with a custom Google icon slot -->
        <button class="btn-ink-secondary w-full">
          <svg class="h-4 w-4" aria-hidden="true" viewBox="0 0 24 24">
            <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
            <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
            <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22c-.87-2.6-2.6-4.53-5.44-4.53z" />
            <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
          </svg>
          Login with Google
        </button>
      </hlm-card-footer>
    </hlm-card>
  `,
})
export class App {
  public readonly spacing = signal('4');
  public readonly spacingOptions = [
    {
      className: '[--card-spacing:--spacing(4)]',
      label: '16px',
      value: '4',
    },
    {
      className: '[--card-spacing:--spacing(5)]',
      label: '20px',
      value: '5',
    },
    {
      className: '[--card-spacing:--spacing(6)]',
      label: '24px',
      value: '6',
    },
    {
      className: '[--card-spacing:--spacing(8)]',
      label: '32px',
      value: '8',
    },
  ];

  public readonly spacingClass = computed(
    () => this.spacingOptions.find((option) => option.value === this.spacing())?.className,
  );
}
