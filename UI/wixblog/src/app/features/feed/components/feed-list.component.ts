import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideInbox } from '@ng-icons/lucide';
import { HlmEmptyImports } from '@spartan-ng/helm/empty';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';

import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';
import { FeedPostRow } from './feed-post-row.component';

@Component({
  selector: 'app-feed-list',
  imports: [FeedPostRow, HlmSkeletonImports, HlmEmptyImports, NgIcon],
  providers: [provideIcons({ lucideInbox })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (loading()) {
      <div class="space-y-8" aria-busy="true">
        @for (i of [1, 2, 3, 4, 5]; track i) {
          <div class="flex gap-6">
            <div class="flex-1 space-y-3">
              <div hlmSkeleton class="h-3 w-40"></div>
              <div hlmSkeleton class="h-5 w-full"></div>
              <div hlmSkeleton class="h-3 w-4/5"></div>
            </div>
            <div hlmSkeleton class="size-24 shrink-0 sm:size-28"></div>
          </div>
        }
      </div>
    } @else {
      @for (post of posts(); track post.id) {
        <app-feed-post-row
          [post]="post"
          [bookmarked]="bookmarkedIds().has(post.id)"
          (bookmarkToggle)="bookmarkToggle.emit(post.id)"
        />
      } @empty {
        <div hlmEmpty>
          <div hlmEmptyHeader>
            <div hlmEmptyMedia variant="icon">
              <ng-icon name="lucideInbox" />
            </div>
            <h3 hlmEmptyTitle>{{ emptyTitle() }}</h3>
            <p hlmEmptyDescription>{{ emptyMessage() }}</p>
          </div>
        </div>
      }
    }
  `,
  styles: [],
})
export class FeedList {
  readonly posts = input.required<readonly PostSummary[]>();
  readonly loading = input(false);
  readonly bookmarkedIds = input<ReadonlySet<string>>(new Set());
  readonly bookmarkToggle = output<string>();
  readonly emptyTitle = input('No stories yet');
  readonly emptyMessage = input('Follow some writers to fill your feed.');
}
