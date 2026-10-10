import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PostBookmarkButton, PostCategoryChip, PostMeta } from './post-card-parts.component';
import { PostSummary } from './post-summary.model';

/** Horizontal feed row: text left, thumbnail right, hairline divider. Medium-style, borderless. */
@Component({
  selector: 'app-post-card-row',
  imports: [RouterLink, PostMeta, PostCategoryChip, PostBookmarkButton],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <article class="relative flex gap-6 border-b border-border py-6">
      <div class="flex min-w-0 flex-1 flex-col gap-2 ">
        <app-post-meta [post]="post()" [showReadTime]="false" />
        <h3 class="text-lg font-bold leading-snug tracking-tight sm:text-xl">
          <a [routerLink]="['/post', post().slug]" class="after:absolute after:inset-0">{{
            post().title
          }}</a>
        </h3>
        <p class="line-clamp-2 hidden text-muted-foreground sm:block">{{ post().excerpt }}</p>
        <div class="mt-2 flex items-center gap-3">
          @if (post().category; as cat) {
            <app-post-category-chip [slug]="cat.slug">{{ cat.name }}</app-post-category-chip>
          }
          <span class="text-xs text-muted-foreground">{{ post().readingMinutes }} min read</span>
          <app-post-bookmark-button
            class="ms-auto"
            [active]="bookmarked()"
            (toggle)="bookmarkToggle.emit(post().id)"
          />
        </div>
      </div>
      @if (post().coverUrl; as cover) {
        <img
          [src]="cover"
          alt=""
          loading="lazy"
          decoding="async"
          class="h-24 w-24 shrink-0 rounded-md object-cover sm:h-28 sm:w-40"
        />
      }
    </article>
  `,
})
export class PostCardRow {
  readonly post = input.required<PostSummary>();
  readonly bookmarked = input(false);
  readonly bookmarkToggle = output<string>();
}
