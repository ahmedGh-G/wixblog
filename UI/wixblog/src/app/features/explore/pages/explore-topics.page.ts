import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { ExploreFooter } from '../components/explore-footer.component';
import { ExploreSuggestEdit } from '../components/explore-suggest-edit.component';
import { ExploreTaxonomyGrid } from '../components/explore-taxonomy-grid.component';
import { ExploreTopicsBar } from '../components/explore-topics-bar.component';
import { ExploreTopicsSearch } from '../components/explore-topics-search.component';
import { EXPLORE_TAXONOMY } from '../data/explore-taxonomy.data';
import { filterTaxonomy } from '../data/explore-taxonomy.filter';

/**
 * `/explore-topics` â€” see EXPLORE_ROUTES in ../explore.routes.ts.
 *
 * Full-page composition of `prototyping/explore/`, top to bottom:
 *   1. the scrolling topic pill bar                     (topics-bar.png)
 *   2. heading, pill search, recommended row   (exploer-topics-section.png)
 *   3. the category â†’ subcategory â†’ tag grid  (explore-topics-fullpage.png)
 *   4. the "Suggest an edit" strip
 *   5. the footer link list
 *
 * The whole page lives in one centered 1496.458px column with 24px side gutters â€”
 * the content box measured off `explore-topics-fullpage.png`. `max-w` plus `mx-auto`
 * keeps it fluid: it fills narrower viewports and centers on wider ones.
 *
 * The search query lives here so the field and the taxonomy grid filter off one signal.
 */
@Component({
  selector: 'app-explore-topics-page',
  imports: [
    ExploreTopicsBar,
    ExploreTopicsSearch,
    ExploreTaxonomyGrid,
    ExploreSuggestEdit,
    ExploreFooter,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto w-full max-w-[1496.458px] px-6 pt-6">
      <app-explore-topics-bar />

      <app-explore-topics-search
        class="mt-14 block"
        [query]="query()"
        (queryChange)="query.set($event)"
        (clear)="query.set('')"
      />

      <hr class="border-border mx-auto mt-14 w-full max-w-[1080px]" />

      <div class="mx-auto w-full max-w-[1080px]">
        <app-explore-taxonomy-grid
          class="mt-12 block"
          [categories]="filtered()"
          [query]="query()"
        />

        <app-explore-suggest-edit class="mt-14 block" />
      </div>

      <app-explore-footer class="mt-14 block" />
    </div>
  `,
  styles: [],
})
export class ExploreTopicsPage {
  protected readonly query = signal('');

  protected readonly filtered = computed(() => filterTaxonomy(EXPLORE_TAXONOMY, this.query()));
}
