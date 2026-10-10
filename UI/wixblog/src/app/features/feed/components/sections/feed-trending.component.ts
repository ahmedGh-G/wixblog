import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideTrendingUp } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmBadgeImports } from '@spartan-ng/helm/badge';
import { HlmItemImports } from '@spartan-ng/helm/item';

import { TRENDING, type TrendingEntry } from '../../data/feed-ui.data';
import { FeedDiscoverHeading } from './feed-discover-heading.component';

/**
 * "Trending": the same row shape as staff picks, plus an optional tinted chip.
 *
 * The reference numbers these with a small yellow glyph; here the glyph is the brand
 * trend icon, and the optional tag is a brand-tinted badge.
 */
@Component({
  selector: 'app-feed-trending',
  imports: [
    FeedDiscoverHeading,
    HlmBadgeImports,
    HlmAvatarImports,
    HlmItemImports,
    NgIcon,
    RouterLink,
  ],
  providers: [provideIcons({ lucideTrendingUp })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-feed-discover-heading title="Trending on WixBlog" action="See all trending stories" />

      <hlm-item-group>
        @for (entry of entries(); track entry.title) {
          <hlm-item size="sm">
            <hlm-item-media>
              <hlm-avatar size="sm" class="size-6">
                @if (entry.publicationAvatar; as src) {
                  <img hlmAvatarImage [src]="src" alt="" />
                } @else {
                  <span class="text-brand grid size-full place-items-center">
                    <ng-icon name="lucideTrendingUp" class="size-4" />
                  </span>
                }
              </hlm-avatar>
            </hlm-item-media>

            <hlm-item-content>
              <hlm-item-title>
                <a routerLink="/" class="line-clamp-2 text-[13px] leading-snug hover:underline">
                  {{ entry.title }}
                </a>
              </hlm-item-title>

              @if (entry.tag; as tag) {
                <hlm-item-description>
                  <span hlmBadge variant="secondary" class="text-brand mt-1">{{ tag }}</span>
                </hlm-item-description>
              }
            </hlm-item-content>
          </hlm-item>
        }
      </hlm-item-group>
    </section>
  `,
  styles: [],
})
export class FeedTrending {
  readonly entries = input<readonly TrendingEntry[]>(TRENDING);
}