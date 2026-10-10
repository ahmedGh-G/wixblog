import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmItemImports } from '@spartan-ng/helm/item';

import { SUGGESTED_AUTHORS, type SuggestedAuthor } from '../../data/feed-ui.data';
import { FeedDiscoverHeading } from './feed-discover-heading.component';

/** "Who to follow": 32px avatar, bold name, 2-line tagline, Follow pill. */
@Component({
  selector: 'app-feed-who-to-follow',
  imports: [FeedDiscoverHeading, HlmAvatarImports, HlmButtonImports, HlmItemImports, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-feed-discover-heading title="Who to follow" action="See more suggestions" />

      <hlm-item-group>
        @for (author of authors(); track author.name) {
          <hlm-item size="sm">
            <hlm-item-media>
              <hlm-avatar size="default" class="size-8">
                @if (author.avatarUrl; as src) {
                  <img hlmAvatarImage [src]="src" alt="" />
                } @else {
                  <span hlmAvatarFallback class="text-xs">{{ author.name.charAt(0) }}</span>
                }
              </hlm-avatar>
            </hlm-item-media>

            <hlm-item-content>
              <hlm-item-title>
                <a routerLink="/" class="text-[13px] font-bold hover:underline">{{ author.name }}</a>
              </hlm-item-title>
              <hlm-item-description class="line-clamp-2 text-xs leading-relaxed">
                {{ author.tagline }}
              </hlm-item-description>
            </hlm-item-content>

            <hlm-item-actions>
              <button
                hlmBtn
                [variant]="isFollowing(author) ? 'outline' : 'default'"
                size="xs"
                type="button"
                class="shrink-0 rounded-4xl px-3"
                (click)="toggle(author)"
              >
                {{ isFollowing(author) ? 'Following' : 'Follow' }}
              </button>
            </hlm-item-actions>
          </hlm-item>
        }
      </hlm-item-group>
    </section>
  `,
  styles: [],
})
export class FeedWhoToFollow {
  readonly authors = input<readonly SuggestedAuthor[]>(SUGGESTED_AUTHORS);

  private readonly overrides = signal<ReadonlyMap<string, boolean>>(new Map());

  /** Seeded from the data file, then overridden locally as the user clicks. */
  protected isFollowing = (a: SuggestedAuthor) => this.overrides().get(a.name) ?? a.following;

  protected toggle = (a: SuggestedAuthor) =>
    this.overrides.update((map) => {
      const next = new Map(map);
      next.set(a.name, !this.isFollowing(a));
      return next;
    });
}