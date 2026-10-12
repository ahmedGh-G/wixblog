import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBookmark,
  lucideBookmarkCheck,
  lucideEllipsis,
  lucideMessageCircle,
  lucidePlus,
  lucideThumbsUp,
} from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmDropdownMenuImports } from '@spartan-ng/helm/dropdown-menu';
import { HlmTooltipImports } from '@spartan-ng/helm/tooltip';

import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';
import { FEED_CLAP_ICON, feedMetaFor } from '../data/feed-ui.data';

/**
 * The row footer: clap and comment counts on the left, and the save / list / more
 * actions pushed to the right — matching the reference, where the actions sit on the
 * same baseline as the counts rather than on the title line.
 *
 * "Save for later" is the only action with state, so it is the only one that emits.
 */
@Component({
  selector: 'app-feed-post-toolbar',
  imports: [HlmButtonImports, HlmTooltipImports, HlmDropdownMenuImports, NgIcon],
  providers: [
    provideIcons({
      FEED_CLAP_ICON: FEED_CLAP_ICON,
      lucideThumbsUp,
      lucideMessageCircle,
      lucideBookmark,
      lucideBookmarkCheck,
      lucideEllipsis,
      lucidePlus,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="text-muted-foreground mt-3 flex items-center gap-4 text-xs">
      <button
        hlmBtn
        variant="ghost"
        size="sm"
        type="button"
        class="text-muted-foreground h-7 gap-1.5 px-1.5 font-normal"
        [attr.aria-label]="'Clap for this story, ' + meta().claps + ' claps'"
      >
        <ng-icon name="FEED_CLAP_ICON" class="size-4" />
        <span class="tabular-nums">{{ meta().claps }}</span>
      </button>

      <span class="inline-flex items-center gap-1.5">
        <ng-icon name="lucideMessageCircle" class="size-4" />
        <span class="tabular-nums">{{ meta().comments }}</span>
      </span>

      <div class="ms-auto flex items-center gap-0.5">
        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          class="text-muted-foreground size-7"
          [hlmTooltip]="bookmarked() ? 'Remove bookmark' : 'Save for later'"
          [attr.aria-pressed]="bookmarked()"
          (click)="bookmarkToggle.emit()"
        >
          <ng-icon [name]="bookmarkIcon()" class="size-4" />
        </button>

        <span hlmTooltip="Add to a list">
          <button
            hlmBtn
            variant="ghost"
            size="icon-sm"
            type="button"
            class="text-muted-foreground size-7"
            aria-label="Add to a list"
          >
            <ng-icon name="lucidePlus" class="size-4" />
          </button>
        </span>

        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          class="text-muted-foreground size-7"
          [hlmDropdownMenuTrigger]="postMenu"
          aria-label="More options for this story"
        >
          <ng-icon name="lucideEllipsis" class="size-4" />
        </button>

        <ng-template #postMenu>
          <div hlmDropdownMenu class="min-w-48">
            <button hlmDropdownMenuItem type="button">
              <ng-icon name="lucidePlus" />
              Add to a list
            </button>
            <button hlmDropdownMenuItem type="button">
              <ng-icon name="lucideThumbsUp" />
              Recommend this story
            </button>
            <div hlmDropdownMenuSeparator></div>
            <button hlmDropdownMenuItem type="button" (click)="bookmarkToggle.emit()">
              <ng-icon [name]="bookmarked() ? 'lucideBookmarkCheck' : 'lucideBookmark'" />
              {{ bookmarked() ? 'Remove bookmark' : 'Save for later' }}
            </button>
            <button hlmDropdownMenuItem variant="destructive" type="button">Report</button>
          </div>
        </ng-template>
      </div>
    </div>
  `,
  styles: [],
})
export class FeedPostToolbar {
  readonly post = input.required<PostSummary>();
  readonly bookmarked = input(false);
  readonly bookmarkToggle = output<void>();

  protected readonly meta = computed(() => feedMetaFor(this.post()));
  /** Icons are looked up by the `NgIcon` key that `provideIcons` registered them under. */
  protected readonly bookmarkIcon = computed(() =>
    this.bookmarked() ? 'lucideBookmarkCheck' : 'lucideBookmark',
  );
}
