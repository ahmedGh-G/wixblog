import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { BlogFeedStore } from './blog-feed.store';
import { PostCard } from './post-card.component';
import { MOCK_POST_DTOS } from './mock-posts';
import { toPostSummary } from './post.mapper';
import { HlmButton } from '@spartan-ng/helm/button';

/** SMART: injects the store, passes data down, handles events. Only layout classes live here. */
@Component({
  selector: 'app-home-page',
  imports: [PostCard, HlmButton],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto grid max-w-6xl gap-10 px-4 py-8 lg:grid-cols-[1fr_20rem]">
      <div class="mx-auto max-w-5xl space-y-12 px-4 py-8">
        <div class="flex items-center justify-end">
          <button hlmBtn variant="outline" (click)="toggleDark()">Toggle dark</button>
        </div>

        <main class="min-w-0">
          @if (store.isLoading()) {
            @for (_ of [1, 2, 3]; track $index) {
              <div class="mb-6 h-28 animate-pulse rounded-md bg-muted"></div>
            }
          } @else {
            @if (store.featured(); as featured) {
              <section>
                <div class="max-w-2xl">
                  <app-post-card
                    variant="large"
                    [post]="posts[0]"
                    [bookmarked]="has(posts[0].id)"
                    (bookmarkToggle)="toggle($event)"
                  />
                </div>
              </section>
            }

            <section class="mt-6" aria-label="Latest stories">
              @for (post of store.rest(); track post.id) {
                <app-post-card
                  variant="row"
                  [post]="post"
                  [bookmarked]="store.isBookmarked(post.id)"
                  (bookmarkToggle)="store.toggleBookmark($event)"
                />
              } @empty {
                <p class="py-12 text-center text-muted-foreground">No stories yet.</p>
              }
            </section>

            @if (store.recommended().length) {
              <section class="mt-10" aria-label="Recommended for you">
                <h2 class="mb-4 text-lg font-bold tracking-tight">Recommended for you</h2>
                <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                  @for (post of store.recommended(); track post.id) {
                    <app-post-card
                      variant="card"
                      [post]="post"
                      [bookmarked]="store.isBookmarked(post.id)"
                      (bookmarkToggle)="store.toggleBookmark($event)"
                    />
                  }
                </div>
              </section>
            }
          }
        </main>

        <aside class="hidden lg:block" aria-label="Trending">
          <h2 class="mb-2 text-sm font-bold uppercase tracking-wider text-muted-foreground">
            Trending
          </h2>
          @for (post of store.trending(); track post.id; let i = $index) {
            <app-post-card variant="compact" [post]="post" [rank]="i + 1" />
          }
        </aside>
      </div>
    </div>
  `,
})
export class HomePage implements OnInit {
  protected store = inject(BlogFeedStore);
  protected posts = MOCK_POST_DTOS.map(toPostSummary);
  private saved = signal<ReadonlySet<string>>(new Set());

  ngOnInit() {
    this.store.load();
  }

  protected has = (id: string) => this.saved().has(id);
  protected toggle(id: string) {
    this.saved.update((s) => {
      const n = new Set(s);
      n.has(id) ? n.delete(id) : n.add(id);
      return n;
    });
  }
  protected toggleDark() {
    document.documentElement.classList.toggle('dark');
  }
}
