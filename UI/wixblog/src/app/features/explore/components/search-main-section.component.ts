import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideChevronDown } from '@ng-icons/lucide';
import { PostCard } from '@shared/ui/card/src/lib/post-card.component';

import { SEARCH_RESULTS, type SearchResult } from '../data/search.data';
import { SearchFilterBar, type SearchTab } from './search-filter-bar.component';

/**
 * `protoyping/explore/search/main-section.png`
 *
 * "Results for <query>", the filter bar, then the result rows, then "Show more".
 *
 * Rows are <app-post-card variant="row"> from shared/ui/card rather than a bespoke
 * component. That component renders a category chip + reading time + bookmark where
 * the reference shows a publication chip, byline, date and clap counts — a deliberate
 * trade for reuse over pixel-exact fidelity.
 */
@Component({
  selector: 'app-search-main-section',
  imports: [NgIcon, PostCard, SearchFilterBar],
  providers: [provideIcons({ lucideChevronDown })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <h1 class="text-3xl font-bold tracking-tight sm:text-4xl">Results for {{ query() }}</h1>

      <app-search-filter-bar
        class="mt-6 block"
        [active]="tab()"
        (activeChange)="tab.set($event)"
      />

      @if (tab() === 'stories') {
        @for (result of results(); track result.id) {
          <app-post-card
            variant="row"
            [post]="result"
            [bookmarked]="bookmarkedIds().has(result.id)"
            (bookmarkToggle)="toggleBookmark($event)"
          />
        }

        <div class="mt-8 text-center">
          <button
            type="button"
            class="text-brand hover:text-brand/80 text-sm font-medium transition-colors"
            (click)="showMore.update((v) => !v)"
          >
            {{ showMore() ? 'Show less' : 'Show more' }}
            <ng-icon
              name="lucideChevronDown"
              class="inline-block size-4 align-middle transition-transform"
              [class.rotate-180]="showMore()"
              aria-hidden="true"
            />
          </button>
        </div>
      }
    </section>
  `,
  styles: [],
})
export class SearchMainSection {
  readonly query = input('web');
  readonly results = input<readonly SearchResult[]>(SEARCH_RESULTS);

  protected readonly tab = signal<SearchTab>('stories');
  protected readonly showMore = signal(false);
  protected readonly bookmarkedIds = signal<ReadonlySet<string>>(new Set());

  protected toggleBookmark = (id: string) =>
    this.bookmarkedIds.update((set) => {
      const next = new Set(set);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
}