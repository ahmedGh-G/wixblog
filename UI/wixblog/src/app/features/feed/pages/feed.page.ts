import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { BlogFeedStore } from '@shared/ui/card/src/lib/blog-feed.store';
import { FeedDiscoverSidebar } from '../components/feed-discover-sidebar.component';
import { FeedList } from '../components/feed-list.component';
import { FeedPromoBanner } from '../components/feed-promo-banner.component';
import { FeedTabs } from '../components/feed-tabs.component';

@Component({
  selector: 'app-home-feed',
  imports: [FeedTabs, FeedList, FeedDiscoverSidebar, FeedPromoBanner],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-feed-promo-banner />
    <div class="flex justify-center gap-8 px-4 pt-6 lg:gap-16 lg:pr-16">
      <div class="w-full max-w-[37.5rem] shrink-0">
        <app-feed-tabs />

        <div aria-label="Feed">
          <app-feed-list
            [posts]="store.posts()"
            [loading]="store.isLoading()"
            [bookmarkedIds]="store.bookmarkedIds()"
            (bookmarkToggle)="store.toggleBookmark($event)"
          />
        </div>
      </div>

      <app-feed-discover-sidebar />
    </div>
  `,
})
export class FeedPage implements OnInit {
  protected readonly store = inject(BlogFeedStore);

  protected readonly currentUserAvatar = signal<string | null>(null);

  ngOnInit() {
    this.store.load(12);
  }
}
