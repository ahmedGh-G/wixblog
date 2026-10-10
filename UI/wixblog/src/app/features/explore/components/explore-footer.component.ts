import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EXPLORE_FOOTER_LINKS, type ExploreFooterLink } from '../data/explore-footer.data';

/**
 * The footer link list at the bottom of /explore-topics
 * (`explore-topics-fullpage.png`): Help · Status · About · … · Text to speech.
 */
@Component({
  selector: 'app-explore-footer',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ul class="flex flex-wrap items-center gap-x-5 gap-y-2">
      @for (link of links(); track link.label) {
        <li>
          <a
            [routerLink]="link.path"
            class="text-muted-foreground hover:text-foreground text-sm transition-colors"
          >
            {{ link.label }}
          </a>
        </li>
      }
    </ul>
  `,
  styles: [],
})
export class ExploreFooter {
  readonly links = input<readonly ExploreFooterLink[]>(EXPLORE_FOOTER_LINKS);
}