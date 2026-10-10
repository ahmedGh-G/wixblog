import { ChangeDetectionStrategy, Component, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideBookmark, lucideEllipsis, lucideThumbsUp, lucideX } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

/**
 * The reading-list promo card at the foot of the discovery sidebar.
 *
 * Structure from the reference: a bordered box whose upper half is empty white space
 * holding a right-aligned icon row plus a close button, and whose lower edge carries a
 * filled brand band, with the heading and body copy below the band.
 *
 * The reference's band is an amber gradient; this uses a brand gradient so it stays
 * consistent with the promo banner.
 */
@Component({
  selector: 'app-reading-list-promo',
  imports: [HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucideThumbsUp, lucideBookmark, lucideEllipsis, lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="bg-background border-border overflow-hidden rounded-lg border">
      <div class="flex items-center justify-end gap-1 px-3 pt-3">
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="Like">
          <ng-icon name="lucideThumbsUp" class="text-muted-foreground size-4" />
        </button>
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="Save">
          <ng-icon name="lucideBookmark" class="text-muted-foreground size-4" />
        </button>
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="More options">
          <ng-icon name="lucideEllipsis" class="text-muted-foreground size-4" />
        </button>
        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          class="text-muted-foreground ms-1"
          aria-label="Dismiss reading list"
          (click)="dismiss.emit()"
        >
          <ng-icon name="lucideX" class="size-4" />
        </button>
      </div>

      <div class="from-brand/25 via-brand/40 to-brand/70 h-24 bg-gradient-to-r"></div>

      <div class="p-4">
        <h3 class="text-sm font-bold">Reading list</h3>
        <p class="text-muted-foreground mt-1 text-[13px] leading-relaxed">
          Click the save icon on any story to add it to your reading list or a custom list that you can
          share.
        </p>
      </div>
    </div>
  `,
  styles: [],
})
export class ReadingListPromo {
  readonly dismiss = output<void>();
}