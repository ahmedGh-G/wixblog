import { computed, inject, Injectable, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { BlogApi } from './blog.api';
import { PostSummary } from './post-summary.model';

type Status = 'idle' | 'loading' | 'error';

/** Feature-scoped state (provided on the route). Dies when you leave /blog. */
@Injectable()
export class BlogFeedStore {
  readonly posts = signal<PostSummary[]>([]);
  readonly recommended = signal<PostSummary[]>([]);
  readonly trending = signal<PostSummary[]>([]);
  readonly bookmarkedIds = signal<ReadonlySet<string>>(new Set());
  readonly status = signal<Status>('idle');
  readonly featured = computed(() => this.posts()[0] ?? null);
  readonly rest = computed(() => this.posts().slice(1));
  readonly isLoading = computed(() => this.status() === 'loading');
  private api = inject(BlogApi);
  private toast = 'testservice';

  isBookmarked = (id: string) => this.bookmarkedIds().has(id);

  /**
   * `size` is forwarded to the API page size. The mock's own default is 8, which is
   * short of a full Medium-style feed, so callers that render the whole feed pass an
   * explicit size instead of the mock defaulting under them.
   */
  load(size?: number) {
    this.status.set('loading');
    forkJoin({
      posts: size === undefined ? this.api.feed() : this.api.feed(0, size),
      recommended: this.api.recommended(),
      trending: this.api.trending(),
    }).subscribe({
      next: (r) => {
        this.posts.set(r.posts);
        this.recommended.set(r.recommended);
        this.trending.set(r.trending);
        this.status.set('idle');
      },
      error: () => {
        this.status.set('error');
        /* this.toast.error('Could not load your feed');*/
      },
    });
  }

  /** Optimistic: update the UI first, roll back if the API fails. */
  toggleBookmark(id: string) {
    const was = this.isBookmarked(id);
    this.setBookmarked(id, !was);
    this.api.setBookmark(id, !was).subscribe({
      error: () => {
        this.setBookmarked(id, was);
        /*this.toast.error('Could not update bookmark');*/
      },
    });
  }

  private setBookmarked(id: string, on: boolean) {
    this.bookmarkedIds.update((s) => {
      const next = new Set(s);
      if (on) next.add(id);
      else next.delete(id);
      return next;
    });
  }
}
