import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Heading + "see all" link shared by every sidebar block. The reference repeats this
 * pair five times with different labels, so it is one component rather than five
 * hand-copied header rows.
 */
@Component({
  selector: 'app-feed-discover-heading',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mb-3 flex items-baseline justify-between gap-2">
      <h2 class="text-base font-bold tracking-tight">{{ title() }}</h2>
      @if (action()) {
        <a routerLink="/" class="text-muted-foreground hover:text-foreground shrink-0 text-xs">
          {{ action() }}
        </a>
      }
    </div>
  `,
  styles: [],
})
export class FeedDiscoverHeading {
  readonly title = input.required<string>();
  readonly action = input<string | null>(null);
}
