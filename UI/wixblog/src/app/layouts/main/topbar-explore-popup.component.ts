import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideChevronRight, lucideCompass } from '@ng-icons/lucide';
import { Router } from '@angular/router';

import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-topbar-explore-popup',
  imports: [NgIcon],
  providers: [provideIcons({ lucideCompass, lucideChevronRight })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="bg-popover text-popover-foreground border-border absolute start-0 top-full z-50 mt-2 w-72 origin-top overflow-hidden rounded-xl border p-1 shadow-lg"
      role="dialog"
      aria-label="Quick links"
    >
      <button
        type="button"
        class="hover:bg-accent focus-visible:ring-ring flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-start transition-colors focus-visible:ring-2 focus-visible:outline-none"
        (click)="go()"
      >
        <span
          class="border-border flex size-7 shrink-0 items-center justify-center rounded-full border"
        >
          <ng-icon name="lucideCompass" class="size-3.5" aria-hidden="true" />
        </span>
        <span class="flex-1 text-sm font-medium">Explore topics</span>
        <ng-icon
          name="lucideChevronRight"
          class="text-muted-foreground size-4"
          aria-hidden="true"
        />
      </button>
    </div>
  `,
  styles: [
    `
      :host {
        animation: popup-in 120ms ease-out;
      }
      @keyframes popup-in {
        from {
          opacity: 0;
          transform: translateY(-4px) scale(0.98);
        }
        to {
          opacity: 1;
          transform: translateY(0) scale(1);
        }
      }
      @media (prefers-reduced-motion: reduce) {
        :host {
          animation: none;
        }
      }
    `,
  ],
})
export class TopbarExplorePopup {
  readonly dismissed = output<void>();
  private readonly router = inject(Router);

  protected go = () => {
    this.router.navigateByUrl(PATHS.topics);
    this.dismissed.emit();
  };
}
