import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideCheck, lucidePlus } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import {
  SEARCH_FOOTER_LINKS,
  SEARCH_PEOPLE,
  SEARCH_PUBLICATIONS,
  SEARCH_TOPICS,
} from '../data/search.data';

/**
 * `prototyping/explore/search/discover-section.png`
 *
 * Topics / People / Publications matching the query, each block separated by a
 * hairline, then the footer link list.
 *
 * Topic pills link to the topic page like everywhere else in explore. People and
 * publications carry a Follow button that toggles locally â€” there is no auth service
 * yet, so nothing persists.
 */
import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-search-discover-section',
  imports: [RouterLink, NgIcon, HlmButtonImports, HlmAvatarImports],
  providers: [provideIcons({ lucideCheck, lucidePlus })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <h2 class="text-sm font-semibold">Topics matching {{ query() }}</h2>

    <div class="mt-4 flex flex-wrap gap-2">
      @for (topic of topics(); track topic.slug) {
        <a
          hlmBtn
          variant="outline"
          [routerLink]="PATHS.topic(topic.slug)"
          class="rounded-4xl px-3 py-1.5 text-sm font-normal"
        >
          {{ topic.label }}
          <ng-icon
            [name]="topic.selected ? 'lucideCheck' : 'lucidePlus'"
            class="text-muted-foreground size-3.5"
            aria-hidden="true"
          />
        </a>
      }
    </div>

    <p class="text-muted-foreground hover:text-foreground mt-4 text-sm transition-colors">
      See all
    </p>

    <hr class="border-border my-6" />

    <h2 class="text-sm font-semibold">People matching {{ query() }}</h2>

    <div class="mt-4 space-y-4">
      @for (person of people; track person.handle) {
        <div class="flex items-start gap-3">
          <hlm-avatar size="default" class="shrink-0">
            <img hlmAvatarImage [src]="person.avatarUrl" alt="" />
          </hlm-avatar>

          <div class="min-w-0 flex-1">
            <p class="text-sm font-semibold">{{ person.name }}</p>
            <p class="text-muted-foreground line-clamp-2 text-xs leading-relaxed">
              {{ person.blurb }}
            </p>
          </div>

          <button
            hlmBtn
            variant="outline"
            type="button"
            class="rounded-4xl px-4 py-1.5 text-sm"
            [attr.aria-pressed]="following().has(person.handle)"
            (click)="toggleFollow(person.handle)"
          >
            {{ following().has(person.handle) ? 'Following' : 'Follow' }}
          </button>
        </div>
      }
    </div>

    <p class="text-muted-foreground hover:text-foreground mt-4 text-sm transition-colors">
      See all
    </p>

    <hr class="border-border my-6" />

    <h2 class="text-sm font-semibold">Publications matching {{ query() }}</h2>

    <div class="mt-4 space-y-4">
      @for (pub of publications; track pub.name) {
        <div class="flex items-start gap-3">
          <hlm-avatar size="default" class="shrink-0">
            <img hlmAvatarImage [src]="pub.avatarUrl" alt="" />
          </hlm-avatar>

          <div class="min-w-0 flex-1">
            <p class="text-sm font-semibold">{{ pub.name }}</p>
            <p class="text-muted-foreground text-xs">Publication</p>
            <p class="text-muted-foreground mt-1 line-clamp-2 text-xs leading-relaxed">
              {{ pub.blurb }}
            </p>
          </div>

          <button
            hlmBtn
            variant="outline"
            type="button"
            class="rounded-4xl px-4 py-1.5 text-sm"
            [attr.aria-pressed]="following().has(pub.name)"
            (click)="toggleFollow(pub.name)"
          >
            {{ following().has(pub.name) ? 'Following' : 'Follow' }}
          </button>
        </div>
      }
    </div>

    <p class="text-muted-foreground hover:text-foreground mt-4 text-sm transition-colors">
      See all
    </p>

    <hr class="border-border my-6" />

    <ul class="flex flex-wrap gap-x-4 gap-y-1.5">
      @for (link of footerLinks; track link.label) {
        <li>
          <a
            [routerLink]="link.path"
            class="text-muted-foreground hover:text-foreground text-xs transition-colors"
          >
            {{ link.label }}
          </a>
        </li>
      }
    </ul>
  `,
  styles: [],
})
export class SearchDiscoverSection {
  readonly query = input('web');
  readonly topics = input(SEARCH_TOPICS);

  protected readonly people = SEARCH_PEOPLE;
  protected readonly publications = SEARCH_PUBLICATIONS;
  protected readonly footerLinks = SEARCH_FOOTER_LINKS;
  protected readonly PATHS = PATHS;

  protected readonly following = signal<ReadonlySet<string>>(new Set());

  protected toggleFollow = (key: string) =>
    this.following.update((set) => {
      const next = new Set(set);
      next.has(key) ? next.delete(key) : next.add(key);
      return next;
    });
}
