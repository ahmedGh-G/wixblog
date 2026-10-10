import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { BlogFeedStore } from '../../../shared/ui/card/src/lib/blog-feed.store';
import { FeedTabs } from '../components/feed-tabs.component';
import { FeedList } from '../components/feed-list.component';
import { FeedDiscoverSidebar } from '../components/feed-discover-sidebar.component';

@Component({
  selector: 'app-following',
  imports: [FeedTabs, FeedList, FeedDiscoverSidebar],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex justify-center gap-8 px-4 pt-6 lg:gap-16 lg:pr-16">
      <div class="w-full max-w-[37.5rem] shrink-0">
        <app-feed-tabs />

        <div aria-label="Following feed">
          <app-feed-list
            [posts]="posts()"
            [loading]="store.isLoading()"
            [bookmarkedIds]="store.bookmarkedIds()"
            (bookmarkToggle)="store.toggleBookmark($event)"
            emptyMessage="Follow writers to see their stories here."
          />
        </div>
      </div>

      <app-feed-discover-sidebar />
    </div>
  `,
})
export class FollowingPage implements OnInit {
  protected readonly store = inject(BlogFeedStore);

  protected readonly currentUserAvatar = signal<string | null>(null);

  ngOnInit() {
    this.store.load(0);
  }

  /**
   * Posts from writers the current user follows.
   *
   * There is no followed-authors endpoint on `BlogApi` yet, so this reads the same
   * feed the store loaded rather than inventing a response shape the API would not
   * honour. Swap this for a store call when that endpoint lands.
   */
  protected posts = () => this.store.posts();
}
