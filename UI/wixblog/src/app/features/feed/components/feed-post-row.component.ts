import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';
import { feedMetaFor } from '../data/feed-ui.data';
import { FeedPostByline } from './feed-post-byline.component';
import { FeedPostToolbar } from './feed-post-toolbar.component';

/**
 * One row of the feed: text block left, thumbnail right.
 *
 * Deliberately NOT the existing `PostCardRow` from `shared/ui/card`: that variant is a
 * bordered article with a category chip and reading-time label in a footer strip. The
 * Medium feed has no card chrome at all — the separator between rows is a 1px rule
 * spanning only the text column, and the metadata is an author byline plus a
 * publication list. Reusing it would have meant fighting its layout rather than
 * matching the reference.
 *
 * The whole row is a link target via `after:absolute after:inset-0` on the title, with
 * the action buttons lifted back above it with `relative z-10`.
 */
@Component({
  selector: 'app-feed-post-row',
  imports: [RouterLink, FeedPostByline, FeedPostToolbar],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <article class="border-border relative flex gap-6 border-b py-7">
      <div class="min-w-0 flex-1">
        <app-feed-post-byline [post]="post()" />

        <h2 class="mt-2 line-clamp-2 text-xl leading-[1.25] font-bold tracking-tight">
          <a [routerLink]="['/post', post().slug]" class="after:absolute after:inset-0">{{
            post().title
          }}</a>
        </h2>

        @if (post().excerpt) {
          <p class="text-muted-foreground mt-2 line-clamp-3 text-[13px] leading-[1.55]">
            {{ post().excerpt }}
          </p>
        }

        <app-feed-post-toolbar
          [post]="post()"
          [bookmarked]="bookmarked()"
          (bookmarkToggle)="bookmarkToggle.emit()"
        />
      </div>

      @if (post().coverUrl; as cover) {
        <img [src]="cover" alt="" loading="lazy" decoding="async" [class]="thumbClass()" />
      }
    </article>
  `,
  styles: [],
})
export class FeedPostRow {
  readonly post = input.required<PostSummary>();
  readonly bookmarked = input(false);
  readonly bookmarkToggle = output<void>();

  /**
   * The reference feed mixes crop shapes row to row (16:9, 4:3, squarer), and that
   * irregularity is what makes it read as a real feed rather than a grid.
   */
  protected readonly thumbClass = computed(() => {
    const shape = feedMetaFor(this.post()).thumb;
    return (
      'h-[6.5rem] w-[8.5rem] shrink-0 rounded-md object-cover sm:h-[8.75rem] sm:w-[11rem] ' +
      (shape === 'wide' ? 'sm:aspect-video' : shape === 'tall' ? 'sm:aspect-[4/3]' : '')
    );
  });
}