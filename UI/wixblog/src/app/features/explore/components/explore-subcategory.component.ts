import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import type { ExploreSubcategory } from '../data/explore-taxonomy.data';

/**
 * A subcategory heading, its tag list, and the grey underlined "More"
 * (`prototyping/explore/category-subcategory-tags-list-example.png`).
 *
 * A topic is the root of all three levels in this app â€” category, subcategory and tag
 * all resolve to the same page â€” so the heading and every tag link to ['/topic', slug].
 *
 * "More" expands the subcategory in place, revealing `hiddenTags`. When the parent
 * category cell is expanded it passes `showAllTags`, which reveals them without
 * needing the per-subcategory control. In a filtered grid there is nothing hidden, so
 * neither control is rendered.
 */
import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-explore-subcategory',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h3 class="text-sm font-bold text-foreground">
      <a
        [routerLink]="PATHS.topic(subcategory().slug)"
        class="hover:text-muted-foreground transition-colors"
      >
        {{ subcategory().label }}
      </a>
    </h3>

    <ul class="mt-3 space-y-2.5 ps-4">
      @for (tag of visibleTags(); track tag.slug) {
        <li>
          <a
            [routerLink]="PATHS.topic(tag.slug)"
            class="hover:text-foreground text-sm text-muted-foreground transition-colors"
          >
            {{ tag.label }}
          </a>
        </li>
      }
    </ul>

    @if (showMoreControl()) {
      <button
        type="button"
        class="text-muted-foreground hover:text-foreground ps-4 pt-3 text-sm underline underline-offset-2 transition-colors"
        [attr.aria-expanded]="expanded()"
        (click)="expanded.set(!expanded())"
      >
        {{ expanded() ? 'Less' : 'More' }}
      </button>
    }
  `,
  styles: [],
})
export class ExploreSubcategoryComponent {
  readonly subcategory = input.required<ExploreSubcategory>();
  /** Set by the category cell when "Show all in X" expands every subcategory at once. */
  readonly showAllTags = input(false);

  protected readonly expanded = signal(false);
  protected readonly PATHS = PATHS;

  protected readonly showMoreControl = () =>
    this.subcategory().hiddenTags.length > 0 && !this.showAllTags();

  protected visibleTags = () => {
    const sub = this.subcategory();
    return this.expanded() || this.showAllTags() ? [...sub.tags, ...sub.hiddenTags] : sub.tags;
  };
}
