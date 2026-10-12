import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmItemImports } from '@spartan-ng/helm/item';

import type { StaffPick } from '../../data/feed-ui.data';
import { STAFF_PICKS } from '../../data/feed-ui.data';
import { FeedDiscoverHeading } from './feed-discover-heading.component';

/** "Staff Picks": publication avatar, bold 2-line title, relative time. */
@Component({
  selector: 'app-feed-staff-picks',
  imports: [FeedDiscoverHeading, HlmAvatarImports, HlmItemImports, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-feed-discover-heading title="Staff Picks" action="See the full list" />

      <hlm-item-group>
        @for (pick of picks(); track pick.title) {
          <hlm-item size="sm">
            <hlm-item-media>
              <hlm-avatar size="sm" class="size-6">
                @if (pick.publicationAvatar; as src) {
                  <img hlmAvatarImage [src]="src" alt="" />
                } @else {
                  <span hlmAvatarFallback class="text-[9px]">{{ initial(pick) }}</span>
                }
              </hlm-avatar>
            </hlm-item-media>

            <hlm-item-content>
              <hlm-item-title>
                <a routerLink="/" class="line-clamp-2 text-[13px] leading-snug hover:underline">
                  {{ pick.title }}
                </a>
              </hlm-item-title>
              <hlm-item-description class="text-xs">{{ pick.ago }}</hlm-item-description>
            </hlm-item-content>
          </hlm-item>
        }
      </hlm-item-group>
    </section>
  `,
  styles: [],
})
export class FeedStaffPicks {
  readonly picks = input<readonly StaffPick[]>(STAFF_PICKS);
  protected initial = (p: StaffPick) => p.publication.charAt(0).toUpperCase();
}
