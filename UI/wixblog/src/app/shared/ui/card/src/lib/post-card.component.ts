import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

import { PostCardCompact } from './post-card-compact.component';
import { PostCardRow } from './post-card-row.component';
import { PostCardStacked } from './post-card-stacked.component';
import { PostSummary } from './post-summary.model';

export type PostCardVariant = 'large' | 'card' | 'row' | 'compact';

/** One entry point: <app-post-card [post]="p" variant="row" />. Variants are separate dumb components. */
@Component({
  selector: 'app-post-card',
  imports: [PostCardStacked, PostCardRow, PostCardCompact],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @switch (variant()) {
      @case ('large') {
        <app-post-card-stacked
          size="lg"
          [post]="post()"
          [bookmarked]="bookmarked()"
          (bookmarkToggle)="bookmarkToggle.emit($event)"
        />
      }
      @case ('card') {
        <app-post-card-stacked
          size="md"
          [post]="post()"
          [bookmarked]="bookmarked()"
          (bookmarkToggle)="bookmarkToggle.emit($event)"
        />
      }
      @case ('row') {
        <app-post-card-row
          [post]="post()"
          [bookmarked]="bookmarked()"
          (bookmarkToggle)="bookmarkToggle.emit($event)"
        />
      }
      @case ('compact') {
        <app-post-card-compact [post]="post()" [rank]="rank()" />
      }
    }
  `,
})
export class PostCard {
  readonly post = input.required<PostSummary>();
  readonly variant = input<PostCardVariant>('row');
  readonly bookmarked = input(false);
  readonly rank = input<number | null>(null); // compact only
  readonly bookmarkToggle = output<string>();
}
