import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

export type SearchTab = 'stories' | 'people' | 'publications' | 'topics' | 'lists';

/**
 * `protoyping/explore/search/search-results-filter-bar.png`
 *
 * Stories / People / Publications / Topics / Lists with the active tab underlined.
 * Only Stories renders results for now; the other tabs carry the active state but have
 * no result pages built yet.
 */
@Component({
  selector: 'app-search-filter-bar',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="border-border flex items-center gap-6 border-b" role="tablist">
      @for (tab of tabs; track tab.id) {
        <button
          type="button"
          role="tab"
          class="relative pb-3 text-sm transition-colors"
          [class.font-semibold]="active() === tab.id"
          [class.text-foreground]="active() === tab.id"
          [class.text-muted-foreground]="active() !== tab.id"
          [attr.aria-selected]="active() === tab.id"
          (click)="activeChange.emit(tab.id)"
        >
          {{ tab.label }}
          @if (active() === tab.id) {
            <span class="bg-foreground absolute inset-x-0 -bottom-px h-0.5"></span>
          }
        </button>
      }
    </div>
  `,
  styles: [],
})
export class SearchFilterBar {
  readonly active = input.required<SearchTab>();
  readonly activeChange = output<SearchTab>();

  protected readonly tabs: readonly { id: SearchTab; label: string }[] = [
    { id: 'stories', label: 'Stories' },
    { id: 'people', label: 'People' },
    { id: 'publications', label: 'Publications' },
    { id: 'topics', label: 'Topics' },
    { id: 'lists', label: 'Lists' },
  ];
}