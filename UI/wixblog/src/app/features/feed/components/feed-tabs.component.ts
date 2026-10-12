import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { DEFAULT_FEED_TABS, type FeedTab } from './feed-tab.model';

/**
 * Route-level tab bar above the feed.
 *
 * Deliberately NOT `hlm-tabs`: `BrnTabsTrigger` is a `<button>` that flips a local
 * `activeTab` signal to drive sibling `hlmTabsContent` panels. It has no `routerLink`.
 * These tabs navigate between two real routes, so the active state has to come from
 * the URL — which `routerLinkActive` already does, and does accessibly.
 */
@Component({
  selector: 'app-feed-tabs',
  imports: [RouterLink, RouterLinkActive],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <nav
      class="bg-background/95 border-border sticky top-[var(--shell-topbar-h)] z-30 flex items-center gap-6 border-b px-1 backdrop-blur"
      aria-label="Feed"
    >
      @for (tab of tabs(); track tab.path) {
        <a
          [routerLink]="tab.path"
          routerLinkActive="font-semibold !text-foreground"
          [routerLinkActiveOptions]="{ exact: true }"
          ariaCurrentWhenActive="page"
          class="text-muted-foreground hover:text-foreground relative pb-3 pt-2 text-base transition-colors"
        >
          {{ tab.label }}
          <span
            routerLinkActive="bg-foreground absolute inset-x-0 -bottom-px hidden h-0.5"
            [routerLinkActiveOptions]="{ exact: true }"
          ></span>
        </a>
      }
    </nav>
  `,
  styles: [],
})
export class FeedTabs {
  readonly tabs = input<readonly FeedTab[]>(DEFAULT_FEED_TABS);
}
