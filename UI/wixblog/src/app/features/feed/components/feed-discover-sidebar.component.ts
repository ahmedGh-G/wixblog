import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HlmSeparatorImports } from '@spartan-ng/helm/separator';

import { FOOTER_LINKS } from '../data/feed-ui.data';
import { ReadingListPromo } from './reading-list-promo.component';
import { FeedStaffPicks } from './sections/feed-staff-picks.component';
import { FeedTrending } from './sections/feed-trending.component';
import { FeedTopics } from './sections/feed-topics.component';
import { FeedWhoToFollow } from './sections/feed-who-to-follow.component';

/**
 * The discovery rail beside the feed: staff picks, trending, topics, who-to-follow,
 * plus the reading-list promo and a small footer link list.
 */
@Component({
  selector: 'app-feed-discover-sidebar',
  imports: [
    FeedStaffPicks,
    FeedTrending,
    FeedTopics,
    FeedWhoToFollow,
    ReadingListPromo,
    HlmSeparatorImports,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <aside
      class="sticky top-[var(--shell-topbar-h)] hidden w-[21.5rem] shrink-0 self-start lg:block"
      aria-label="Discover"
    >
      <div class="space-y-8">
        <app-feed-staff-picks />
        <app-feed-trending />
        <app-feed-topics />
        <app-feed-who-to-follow />

        @if (readingListVisible()) {
          <app-reading-list-promo (dismiss)="readingListVisible.set(false)" />
        }
      </div>

      <div hlmSeparator class="my-8"></div>

      <nav aria-label="Footer">
        <ul class="flex flex-wrap gap-x-3 gap-y-1.5">
          @for (link of links; track link) {
            <li>
              <a routerLink="/" class="text-muted-foreground hover:text-foreground text-xs">
                {{ link }}
              </a>
            </li>
          }
        </ul>
      </nav>
    </aside>
  `,
  styles: [],
})
export class FeedDiscoverSidebar {
  protected readonly links = FOOTER_LINKS;
  protected readonly readingListVisible = signal(true);
}
