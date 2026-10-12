import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import type { ExploreCategory } from '../data/explore-taxonomy.data';
import { ExploreCategoryCell } from './explore-category-cell.component';

/**
 * The category â†’ subcategory â†’ tag grid
 * (`prototyping/explore/explore-topics-fullpage.png`).
 *
 * Geometry is taken from `prototyping/explore/taxonomy-grid layring.png`, where a
 * devtools inspection reports the grid element itself as **1080 Ã— 904.51** with three
 * equal-width columns:
 *
 *     (1080 âˆ’ 2 Ã— 40px gap) / 3 = 333.33px per column
 *
 * The parent page wraps this in a 1080px container, so the grid fills it exactly. Cells
 * carry no inner padding â€” headings, subcategory names and tags all sit flush to the
 * 1080px edges, as the overlay shows.
 *
 * This is a real CSS grid, not masonry: cells in a row stretch to equal height, which
 * is what keeps the `border-b` rules aligned across the row in the reference.
 *
 * The three-column layout steps down to two then one so the cell stays legible on
 * narrow viewports.
 */
@Component({
  selector: 'app-explore-taxonomy-grid',
  imports: [ExploreCategoryCell],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (categories().length) {
      <div class="grid grid-cols-1 gap-x-10 gap-y-10 md:grid-cols-2 lg:grid-cols-3">
        @for (category of categories(); track category.slug) {
          <app-explore-category-cell [category]="category" />
        }
      </div>
    } @else {
      <p class="text-muted-foreground py-16 text-center text-sm">
        No topics match "{{ query() }}".
      </p>
    }
  `,
  styles: [],
})
export class ExploreTaxonomyGrid {
  readonly categories = input.required<readonly ExploreCategory[]>();
  /** Echoed in the empty state so the message names what was searched for. */
  readonly query = input('');
}
