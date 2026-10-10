import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { PostMeta } from './post-card-parts.component';
import { PostSummary } from './post-summary.model';

/** Sidebar suggestion: byline + 2-line title, optional rank number. No image, no actions. */
@Component({
  selector: 'app-post-card-compact',
  imports: [RouterLink, DecimalPipe, PostMeta],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <article class="relative flex gap-3 py-3">
      @if (rank(); as r) {
        <span aria-hidden="true" class="w-7 shrink-0 text-2xl font-bold tabular-nums text-border">{{
          r | number: '2.0'
        }}</span>
      }
      <div class="min-w-0">
        <app-post-meta [post]="post()" [showReadTime]="false" />
        <h4 class="mt-1 line-clamp-2 text-sm font-bold leading-snug">
          <a [routerLink]="['/post', post().slug]" class="after:absolute after:inset-0">{{
            post().title
          }}</a>
        </h4>
      </div>
    </article>
  `,
})
export class PostCardCompact {
  readonly post = input.required<PostSummary>();
  readonly rank = input<number | null>(null);
}
