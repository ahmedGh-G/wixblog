import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideChevronDown } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import { PATHS } from '../../../../../context/paths';
import type { ExploreCategory } from '../data/explore-taxonomy.data';
import { ExploreSubcategoryComponent } from './explore-subcategory.component';

/**
 * One category column of the taxonomy grid — the "Life" cell in
 * `protoyping/explore/category-subcategory-tags-list-example.png`.
 *
 * The reference already lists every subcategory (Life shows all three), so the green
 * "Show all in <Category>" control expands the tags *within* those subcategories rather
 * than revealing more subcategories. When expanded, each subcategory receives its
 * hidden tags and drops its own "More".
 */
@Component({
  selector: 'app-explore-category-cell',
  imports: [RouterLink, NgIcon, HlmButtonImports, ExploreSubcategoryComponent],
  providers: [provideIcons({ lucideChevronDown })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="border-border border-b pb-6">
      <h2 class="text-xl font-bold tracking-tight">
        <a
          [routerLink]="PATHS.topic(category().slug)"
          class="hover:text-muted-foreground transition-colors"
        >
          {{ category().label }}
        </a>
      </h2>

      @for (sub of category().subcategories; track sub.slug) {
        <div class="mt-8 first:mt-6">
          <app-explore-subcategory [subcategory]="sub" [showAllTags]="expanded()" />
        </div>
      } @empty {
        <p class="text-muted-foreground mt-6 text-sm">No subcategories for this topic yet.</p>
      }

      @if (hasHiddenTags()) {
        <button
          hlmBtn
          variant="ghost"
          type="button"
          class="text-brand hover:text-brand/80 mt-6 h-auto gap-1.5 px-0 font-medium"
          [attr.aria-expanded]="expanded()"
          (click)="expanded.set(!expanded())"
        >
          <ng-icon
            name="lucideChevronDown"
            class="size-4 transition-transform"
            [class.rotate-180]="expanded()"
            aria-hidden="true"
          />
          Show all in "{{ category().label }}"
        </button>
      }
    </section>
  `,
  styles: [],
})
export class ExploreCategoryCell {
  readonly category = input.required<ExploreCategory>();

  protected readonly expanded = signal(false);
  protected readonly PATHS = PATHS;

  protected hasHiddenTags = () =>
    this.category().subcategories.some((s) => s.hiddenTags.length > 0);
}
