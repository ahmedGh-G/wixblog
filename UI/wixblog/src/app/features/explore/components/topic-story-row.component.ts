import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBookmark,
  lucideEllipsis,
  lucideMessageCircle,
  lucideRepeat2,
  lucideSparkles,
} from '@ng-icons/lucide';
import { PostCardRow } from '@shared/ui/card/src/lib/post-card-row.component';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';

import type { TopicStory } from '../data/topic-detail.data';

/**
 * One story in `selected(featured)-topic-stories.png`.
 *
 * The row itself is <app-post-card-row> from shared/ui/card — reused rather than
 * reimplemented, per the explore-slice rule. It carries the byline, bold title, muted
 * excerpt and square thumbnail.
 *
 * Its title link paints an `after:absolute after:inset-0` overlay, so anything added
 * here must sit outside that stacking context or it becomes unclickable. That is why
 * the byline and the action row below are siblings of the card rather than children,
 * and why the action buttons sit at z-10.
 */
@Component({
  selector: 'app-topic-story-row',
  imports: [NgIcon, HlmAvatarImports, PostCardRow],
  providers: [
    provideIcons({
      lucideSparkles,
      lucideMessageCircle,
      lucideRepeat2,
      lucideBookmark,
      lucideEllipsis,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <article class="relative border-b border-border py-6">
      <div class="flex items-center gap-2 text-sm">
        @if (story().publication; as pub) {
          <span
            class="bg-secondary text-secondary-foreground rounded px-1.5 py-0.5 text-[10px] font-semibold tracking-wide uppercase"
          >
            {{ pub }}
          </span>
        }

        @if (story().author.avatarUrl; as src) {
          <hlm-avatar size="sm" class="size-5 shrink-0">
            <img hlmAvatarImage [src]="src" alt="" />
          </hlm-avatar>
        }

        <span class="truncate font-medium">{{ story().author.name }}</span>
        <span class="text-muted-foreground shrink-0">·</span>
        <span class="text-muted-foreground shrink-0">{{ story().relativeDate }}</span>
      </div>

      <div class="relative z-10 mt-4">
        <app-post-card-row [post]="story()" [bookmarked]="bookmarked()" />
      </div>

      @if (story().aiSummary; as summary) {
        <div class="mt-4 flex gap-3 border-l-2 border-border ps-4">
          <ng-icon
            name="lucideSparkles"
            class="text-brand mt-0.5 size-4 shrink-0"
            aria-hidden="true"
          />
          <p class="text-sm leading-relaxed text-muted-foreground">{{ summary }}</p>
        </div>
      }

      <div class="text-muted-foreground relative z-10 mt-4 flex items-center gap-4 text-xs">
        <span class="flex items-center gap-1.5">
          <ng-icon name="lucideSparkles" class="text-brand size-4" aria-hidden="true" />
          <span class="tabular-nums">{{ compact(story().claps) }}</span>
        </span>
        <span class="flex items-center gap-1.5">
          <ng-icon name="lucideMessageCircle" class="size-4" aria-hidden="true" />
          <span class="tabular-nums">{{ compact(story().comments) }}</span>
        </span>
        <span class="flex items-center gap-1.5">
          <ng-icon name="lucideRepeat2" class="size-4" aria-hidden="true" />
          <span class="tabular-nums">{{ compact(story().reposts) }}</span>
        </span>

        <div class="ms-auto flex items-center gap-1">
          <button
            type="button"
            class="hover:text-foreground rounded p-1.5 transition-colors"
            [attr.aria-pressed]="bookmarked()"
            [attr.aria-label]="bookmarked() ? 'Remove bookmark' : 'Bookmark'"
            (click)="bookmarkToggle.emit()"
          >
            <ng-icon name="lucideBookmark" class="size-4" aria-hidden="true" />
          </button>
          <button
            type="button"
            class="hover:text-foreground rounded p-1.5 transition-colors"
            aria-label="More options"
          >
            <ng-icon name="lucideEllipsis" class="size-4" aria-hidden="true" />
          </button>
        </div>
      </div>
    </article>
  `,
  styles: [],
})
export class TopicStoryRow {
  readonly story = input.required<TopicStory>();
  readonly bookmarked = input(false);
  readonly bookmarkToggle = output<void>();

  /** 16700 -> 16.7K, matching the reference's abbreviated counts. */
  protected compact = (n: number) =>
    n >= 1000 ? `${(n / 1000).toFixed(1).replace(/\.0$/, '')}K` : String(n);
}
