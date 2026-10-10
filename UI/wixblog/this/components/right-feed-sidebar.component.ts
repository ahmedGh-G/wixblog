import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideTrendingUp, lucideX } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmBadgeImports } from '@spartan-ng/helm/badge';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmSeparatorImports } from '@spartan-ng/helm/separator';

import {
  FOOTER_LINKS,
  SUGGESTED_AUTHORS,
  STAFF_PICKS,
  TOPICS,
  TRENDING,
  type SuggestedAuthor,
  type StaffPick,
  type TrendingEntry,
} from '../data/feed-ui.data';
import { ReadingListCard } from './feed-chrome.component';

/**
 * Heading + "see all" link shared by every sidebar block. The reference repeats this
 * pair five times with different labels, so it is one component rather than five
 * hand-copied header rows.
 */
@Component({
  selector: 'app-sidebar-heading',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mb-3 flex items-baseline justify-between gap-2">
      <h2 class="text-base font-bold tracking-tight">{{ title() }}</h2>
      @if (action()) {
        <a routerLink="/" class="shrink-0 text-xs text-muted-foreground hover:text-foreground">
          {{ action() }}
        </a>
      }
    </div>
  `,
})
export class SidebarHeading {
  readonly title = input.required<string>();
  readonly action = input<string | null>(null);
}

/** "Staff Picks": publication avatar, bold 2-line title, relative time. */
@Component({
  selector: 'app-sidebar-staff-picks',
  imports: [SidebarHeading, HlmAvatarImports, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-sidebar-heading title="Staff Picks" action="See the full list" />

      <ul class="space-y-4">
        @for (pick of picks(); track pick.title) {
          <li class="flex gap-3">
            <hlm-avatar size="sm" class="size-6">
              @if (pick.publicationAvatar; as src) {
                <img hlmAvatarImage [src]="src" alt="" />
              } @else {
                <span hlmAvatarFallback class="text-[9px]">{{ initial(pick) }}</span>
              }
            </hlm-avatar>
            <div class="min-w-0">
              <a routerLink="/" class="line-clamp-2 text-[13px] font-bold leading-snug hover:underline">
                {{ pick.title }}
              </a>
              <p class="mt-0.5 text-xs text-muted-foreground">{{ pick.ago }}</p>
            </div>
          </li>
        }
      </ul>
    </section>
  `,
})
export class SidebarStaffPicks {
  readonly picks = input<readonly StaffPick[]>(STAFF_PICKS);
  protected initial = (p: StaffPick) => p.publication.charAt(0).toUpperCase();
}

/**
 * "Trending": the same row shape as staff picks, plus an optional tinted chip.
 *
 * The reference numbers these with a small yellow glyph; here the glyph is the brand
 * trend icon, and the optional tag is a brand-tinted badge.
 */
@Component({
  selector: 'app-sidebar-trending',
  imports: [SidebarHeading, HlmBadgeImports, HlmAvatarImports, NgIcon, RouterLink],
  providers: [provideIcons({ lucideTrendingUp })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-sidebar-heading title="Trending on WixBlog" action="See all trending stories" />

      <ul class="space-y-4">
        @for (entry of entries(); track entry.title) {
          <li class="flex gap-3">
            <hlm-avatar size="sm" class="size-6">
              @if (entry.publicationAvatar; as src) {
                <img hlmAvatarImage [src]="src" alt="" />
              } @else {
                <span
                  class="grid size-full place-items-center text-brand"
                >
                  <ng-icon name="lucideTrendingUp" class="size-4" />
                </span>
              }
            </hlm-avatar>
            <div class="min-w-0">
              <a routerLink="/" class="line-clamp-2 text-[13px] font-bold leading-snug hover:underline">
                {{ entry.title }}
              </a>
              @if (entry.tag; as tag) {
                <span hlmBadge variant="secondary" class="mt-1 text-brand">{{ tag }}</span>
              }
              <p class="mt-0.5 text-xs text-muted-foreground">{{ entry.ago }}</p>
            </div>
          </li>
        }
      </ul>
    </section>
  `,
})
export class SidebarTrending {
  readonly entries = input<readonly TrendingEntry[]>(TRENDING);
}

/**
 * "Recommended topics": a two-column grid of outlined pills, each with a `+` on the
 * right. Follow state is local to this component — nothing on the home feed persists
 * a topic subscription yet.
 */
@Component({
  selector: 'app-sidebar-topics',
  imports: [SidebarHeading, HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucidePlus, lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-sidebar-heading title="Recommended topics" action="See more topics" />

      <ul class="grid grid-cols-2 gap-2">
        @for (topic of topics(); track topic) {
          <li>
            <button
              type="button"
              class="flex w-full items-center justify-between gap-1 rounded-md border border-border px-3 py-2 text-start text-[13px] transition-colors hover:bg-muted"
              [attr.aria-pressed]="following().has(topic)"
              (click)="toggle(topic)"
            >
              <span class="truncate">{{ topic }}</span>
              @if (following().has(topic)) {
                <ng-icon name="lucideX" class="size-3.5 shrink-0 text-muted-foreground" />
              } @else {
                <ng-icon name="lucidePlus" class="size-3.5 shrink-0 text-muted-foreground" />
              }
            </button>
          </li>
        }
      </ul>
    </section>
  `,
})
export class SidebarTopics {
  readonly topics = input<readonly string[]>(TOPICS);
  protected readonly following = signal<ReadonlySet<string>>(new Set());

  protected toggle = (topic: string) =>
    this.following.update((set) => {
      const next = new Set(set);
      next.has(topic) ? next.delete(topic) : next.add(topic);
      return next;
    });
}

/** "Who to follow": 32px avatar, bold name, 2-line tagline, Follow pill. */
@Component({
  selector: 'app-sidebar-who-to-follow',
  imports: [SidebarHeading, HlmAvatarImports, HlmButtonImports, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-sidebar-heading title="Who to follow" action="See more suggestions" />

      <ul class="space-y-5">
        @for (author of authors(); track author.name) {
          <li class="flex items-start gap-3">
            <hlm-avatar size="default" class="size-8">
              @if (author.avatarUrl; as src) {
                <img hlmAvatarImage [src]="src" alt="" />
              } @else {
                <span hlmAvatarFallback class="text-xs">{{ author.name.charAt(0) }}</span>
              }
            </hlm-avatar>

            <div class="min-w-0 flex-1">
              <a routerLink="/" class="text-[13px] font-bold hover:underline">{{ author.name }}</a>
              <p class="mt-0.5 line-clamp-2 text-xs leading-relaxed text-muted-foreground">
                {{ author.tagline }}
              </p>
            </div>

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
          </li>
        }
      </ul>
    </section>
  `,
})
export class SidebarWhoToFollow {
  readonly authors = input<readonly SuggestedAuthor[]>(SUGGESTED_AUTHORS);

  private readonly overrides = signal<ReadonlyMap<string, boolean>>(new Map());

  /** Seeded from the data file, then overridden locally as the user clicks. */
  protected isFollowing = (a: SuggestedAuthor) =>
    this.overrides().get(a.name) ?? a.following;

  protected toggle = (a: SuggestedAuthor) =>
    this.overrides.update((map) => {
      const next = new Map(map);
      next.set(a.name, !this.isFollowing(a));
      return next;
    });
}

/**
 * The whole right-hand column.
 *
 * Composition only: each block is a separate component and this one owns the stack
 * spacing, the dismissible reading-list card, and the footer link row.
 *
 * Hidden below `lg`, where the reference collapses to a single feed column.
 */
@Component({
  selector: 'app-right-feed-sidebar',
  imports: [
    SidebarStaffPicks,
    SidebarTrending,
    SidebarTopics,
    SidebarWhoToFollow,
    ReadingListCard,
    HlmSeparatorImports,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <aside class="hidden w-[21.5rem] shrink-0 lg:block" aria-label="Discover">
      <div class="space-y-8">
        <app-sidebar-staff-picks />
        <app-sidebar-trending />
        <app-sidebar-topics />
        <app-sidebar-who-to-follow />

        @if (readingListVisible()) {
          <app-reading-list-card (dismiss)="readingListVisible.set(false)" />
        }
      </div>

      <div hlmSeparator class="my-8"></div>

      <nav aria-label="Footer">
        <ul class="flex flex-wrap gap-x-3 gap-y-1.5">
          @for (link of links; track link) {
            <li>
              <a routerLink="/" class="text-xs text-muted-foreground hover:text-foreground">
                {{ link }}
              </a>
            </li>
          }
        </ul>
      </nav>
    </aside>
  `,
})
export class RightFeedSidebar {
  protected readonly links = FOOTER_LINKS;
  protected readonly readingListVisible = signal(true);
}