import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { PostCard } from './post-card.component';
import { MOCK_POST_DTOS } from './mock-posts';
import { toPostSummary } from './post.mapper';

/** DEV ONLY. Delete before release. Route: /playground/post-cards */
@Component({
  selector: 'app-post-cards-gallery',
  imports: [PostCard, HlmButtonImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'block min-h-dvh bg-background' },
  template: `
    <div class="mx-auto max-w-5xl space-y-12 px-4 py-8">
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-bold tracking-tight">Post cards gallery</h1>
        <button hlmBtn variant="outline" (click)="toggleDark()">Toggle dark</button>
      </div>

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

      <section>
        <h2 class="mb-3 text-sm font-semibold uppercase tracking-wider text-muted-foreground">
          card (recommended grid)
        </h2>
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          @for (p of [posts[1], posts[3], posts[4]]; track p.id) {
            <app-post-card
              variant="card"
              [post]="p"
              [bookmarked]="has(p.id)"
              (bookmarkToggle)="toggle($event)"
            />
          }
        </div>
      </section>

      <section>
        <h2 class="mb-3 text-sm font-semibold uppercase tracking-wider text-muted-foreground">
          row (feed)
        </h2>
        <div class="max-w-3xl">
          @for (p of [posts[2], posts[3], posts[4]]; track p.id) {
            <app-post-card
              variant="row"
              [post]="p"
              [bookmarked]="has(p.id)"
              (bookmarkToggle)="toggle($event)"
            />
          }
        </div>
      </section>

      <section>
        <h2 class="mb-3 text-sm font-semibold uppercase tracking-wider text-muted-foreground">
          compact (sidebar)
        </h2>
        <div class="max-w-xs">
          @for (p of posts.slice(0, 4); track p.id; let i = $index) {
            <app-post-card variant="compact" [post]="p" [rank]="i + 1" />
          }
        </div>
      </section>
      <button hlmBtn variant="default">Button</button>
    </div>
  `,
})
export class PostCardsGallery {
  protected posts = MOCK_POST_DTOS.map(toPostSummary);
  private saved = signal<ReadonlySet<string>>(new Set());

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
