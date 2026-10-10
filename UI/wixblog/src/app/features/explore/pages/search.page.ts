import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs';

import { SearchDiscoverSection, SearchMainSection } from '@features/explore';

/**
 * `/search?q=<query>` — see PATHS.search.
 *
 * Two columns from `protoyping/explore/search/search-results-page.png`: results on the
 * left, the discover aside on the right, separated by a vertical hairline.
 */
@Component({
  selector: 'app-search-page',
  imports: [SearchMainSection, SearchDiscoverSection],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto w-full max-w-[1496.458px] px-6 py-8">
      <div class="grid gap-x-12 lg:grid-cols-[minmax(0,1fr)_22.5rem]">
        <app-search-main-section [query]="query()" />

        <aside class="border-border lg:border-s lg:pl-8">
          <app-search-discover-section [query]="query()" />
        </aside>
      </div>
    </div>
  `,
  styles: [],
})
export class SearchPage {
  private readonly route = inject(ActivatedRoute);

  protected readonly query = toSignal(
    this.route.queryParamMap.pipe(map((params) => params.get('q') ?? 'web')),
    { initialValue: 'web' },
  );
}
