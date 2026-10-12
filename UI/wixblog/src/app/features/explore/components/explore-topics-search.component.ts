import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideSearch, lucideX } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmBadgeImports } from '@spartan-ng/helm/badge';
import { HlmInputGroupImports } from '@spartan-ng/helm/input-group';

import {
  EXPLORE_TOPICS_SEARCH_PLACEHOLDER,
  RECOMMENDED_TOPICS,
  type ExploreTopic,
} from '../data/explore-topics.data';

/**
 * Heading + pill search field + recommended row
 * (`prototyping/exploer/exploer-topics-section.png`).
 *
 * The search filters the recommended row in place. It is deliberately not wired to
 * /search â€” a form that navigates nowhere would be worse than one that filters what is
 * already on screen.
 */
import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-explore-topics-search',
  imports: [RouterLink, NgIcon, HlmBadgeImports, HlmInputGroupImports],
  providers: [provideIcons({ lucideSearch, lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto w-full max-w-2xl text-center">
      <h1 class="text-4xl font-bold tracking-tight text-balance sm:text-5xl">Explore topics</h1>

      <div class="mt-8">
        <label hlmInputGroup class="bg-muted/50 h-12 rounded-full border-0 px-5">
          <ng-icon
            name="lucideSearch"
            class="text-muted-foreground size-4 shrink-0"
            aria-hidden="true"
          />
          <input
            hlmInputGroupInput
            type="search"
            class="placeholder:text-muted-foreground h-full text-base"
            [placeholder]="placeholder()"
            [value]="query()"
            (input)="onInput($event)"
          />
          @if (query()) {
            <button
              type="button"
              class="text-muted-foreground hover:text-foreground shrink-0 rounded-full p-1 transition-colors"
              aria-label="Clear search"
              (click)="clear.emit()"
            >
              <ng-icon name="lucideX" class="size-4" aria-hidden="true" />
            </button>
          }
        </label>
      </div>

      <div class="mt-6 flex flex-wrap items-center justify-center gap-2">
        <span class="text-sm text-muted-foreground">Recommended:</span>

        @for (topic of filtered(); track topic.slug) {
          <a hlmBadge variant="outline" [routerLink]="topicPath(topic.slug)" class="text-sm">
            {{ topic.label }}
          </a>
        } @empty {
          <span class="text-sm text-muted-foreground">No topics match "{{ query() }}".</span>
        }
      </div>
    </section>
  `,
  styles: [],
})
export class ExploreTopicsSearch {
  readonly recommended = input<readonly ExploreTopic[]>(RECOMMENDED_TOPICS);
  readonly placeholder = input(EXPLORE_TOPICS_SEARCH_PLACEHOLDER);

  /** Owned by the page so the taxonomy grid filters off the same signal. */
  readonly query = input.required<string>();
  /** The page owns the signal; the field reports changes back up. */
  readonly queryChange = output<string>();
  readonly clear = output<void>();

  protected readonly filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    if (!q) return this.recommended();
    return this.recommended().filter((t) => t.label.toLowerCase().includes(q));
  });

  protected topicPath = (slug: string) => PATHS.topic(slug);

  protected onInput = (event: Event) =>
    this.queryChange.emit((event.target as HTMLInputElement).value);
}
