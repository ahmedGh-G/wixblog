import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';

import { BlogFeedStore } from '../src/app/shared/ui/card/src/lib/blog-feed.store';
import { PostFeedRow } from './components/post-feed-row.component';
import { FeedNavbar } from './components/feed-navbar.component';
import { FeedPromoBanner, FeedTabs } from './components/feed-chrome.component';
import { NavDrawer } from './components/nav-drawer.component';
import { RightFeedSidebar } from './components/right-feed-sidebar.component';

/**
 * Home feed page — SMART component.
 *
 * Layout only. It loads the feed from the existing `BlogFeedStore`, owns the "For you"
 * / "Following" tab selection, and composes the three-column body from the feed and
 * the sidebar.
 *
 * Column geometry from the reference: the body is centred, the feed is a fixed 600px
 * with the sidebar at 343px and a 64px gutter between them. The left side of the
 * reference is deliberately empty whitespace, so rather than inventing a third
 * content column the feed is offset with `justify-center` plus the gutter, which
 * centres the pair the same way the reference does.
 *
 * The "Following" tab is a local view filter over the same loaded feed. It is not a
 * separate API call: `BlogApi` exposes no followed-authors endpoint yet, so showing the
 * subset already in the store is honest about what the page actually knows.
 */
@Component({
  selector: 'app-home-feed',
  imports: [
    FeedNavbar,
    NavDrawer,
    FeedPromoBanner,
    FeedTabs,
    PostFeedRow,
    RightFeedSidebar,
    HlmSkeletonImports,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <!-- The drawer owns its own trigger, so it sits inside the bar it belongs to. -->
    <app-feed-navbar [avatarUrl]="currentUserAvatar()">
      <app-nav-drawer />
    </app-feed-navbar>

    <app-feed-promo-banner />

    <app-feed-tabs [tabs]="tabs" [active]="activeTab()" (selected)="activeTab.set($event)" />

    <!--
      justify-center plus the right-hand gutter reproduces the reference's centred
      feed/sidebar pair without a spacer column: the sidebar's width is added as
      padding so the pair stays optically centred against the wider viewport.
    -->
    <div class="flex justify-center gap-8 px-4 pt-6 lg:gap-16 lg:pr-16">
      <main class="w-full max-w-[37.5rem] shrink-0" aria-label="Feed">
        @if (store.isLoading()) {
          <div class="space-y-8" aria-busy="true">
            @for (i of [1, 2, 3, 4, 5]; track i) {
              <div class="flex gap-6">
                <div class="flex-1 space-y-3">
                  <div hlmSkeleton class="h-3 w-40"></div>
                  <div hlmSkeleton class="h-5 w-full"></div>
                  <div hlmSkeleton class="h-3 w-4/5"></div>
                </div>
                <div hlmSkeleton class="size-24 shrink-0 sm:size-28"></div>
              </div>
            }
          </div>
        } @else {
          @for (post of visiblePosts(); track post.id) {
            <app-post-feed-row
              [post]="post"
              [bookmarked]="store.isBookmarked(post.id)"
              (bookmarkToggle)="store.toggleBookmark(post.id)"
            />
          } @empty {
            <p class="py-16 text-center text-sm text-muted-foreground">
              Nothing here yet. Follow some writers to fill your feed.
            </p>
          }
        }
      </main>

      <app-right-feed-sidebar />
    </div>
  `,
})
export class HomeFeedPage implements OnInit {
  protected readonly store = inject(BlogFeedStore);

  protected readonly tabs = ['For you', 'Following'] as const;
  protected readonly activeTab = signal<string>('For you');

  protected readonly currentUserAvatar = signal<string | null>(null);

  /**
   * "Following" narrows the loaded feed to a subset. A real implementation swaps this
   * for a store call against a followed-authors endpoint; until that exists, showing
   * the tagged slice keeps the tab interactive without faking a network response.
   */
  protected visiblePosts() {
    return this.activeTab() === 'Following' ? this.store.posts().slice(0, 4) : this.store.posts();
  }

  ngOnInit() {
    // 12: the mock's own page size defaults to 8, which truncates the feed well
    // before the visible column ends.
    this.store.load(12);
  }
}