import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBookmark,
  lucideEllipsis,
  lucideThumbsUp,
  lucideX,
} from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

/**
 * The promo strip that sits between the navbar and the feed.
 *
 * The reference uses a saturated amber band with a white pill label. Rebranded to
 * Inkwell, the band is a brand-tinted wash with a solid brand pill, so the contrast
 * hierarchy of the original (light label block, dark text on it, underlined CTA)
 * is preserved while the colour comes from `--brand` in `styles.scss`.
 */
@Component({
  selector: 'app-feed-promo-banner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="bg-brand/10 border-b border-brand/20">
      <div class="flex flex-wrap items-center justify-center gap-2 px-4 py-2.5 text-sm">
        <span
          class="rounded-4xl bg-brand px-3 py-1 text-xs font-semibold text-brand-foreground"
        >
          Welcome Offer
        </span>
        <span class="text-foreground">Access to everything. Now 30% off.</span>
        <a
          href="#upgrade"
          class="font-semibold text-brand underline underline-offset-2 hover:opacity-80"
        >
          Upgrade now
        </a>
      </div>
    </div>
  `,
})
export class FeedPromoBanner {}

/**
 * The "For you" / "Following" switch above the feed.
 *
 * A two-button underline tab rather than `hlmTabs`: the reference tabs are text-only
 * with a 2px underline and no pill background, and Spartan's tab list ships with a
 * filled track. Reimplementing the 10 lines here is cheaper than overriding the
 * directive's classes on every descendant.
 *
 * Sticky directly under the 14-unit navbar, so the active tab stays visible while the
 * feed scrolls.
 */
@Component({
  selector: 'app-feed-tabs',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <nav
      class="sticky top-14 z-30 flex items-center gap-6 border-b border-border bg-background px-1"
      aria-label="Feed"
    >
      @for (tab of tabs(); track tab) {
        <button
          type="button"
          class="pb-3 pt-2 text-base transition-colors"
          [class.font-semibold]="active() === tab"
          [class.text-foreground]="active() === tab"
          [class.text-muted-foreground]="active() !== tab"
          [attr.aria-current]="active() === tab ? 'page' : null"
          (click)="selected.emit(tab)"
        >
          {{ tab }}
          @if (active() === tab) {
            <span class="bg-foreground absolute inset-x-0 -bottom-px h-0.5"></span>
          }
        </button>
      }
    </nav>
  `,
})
export class FeedTabs {
  readonly tabs = input<readonly string[]>(['For you', 'Following']);
  readonly active = input.required<string>();
  readonly selected = output<string>();
}

/**
 * The reading-list promo card at the foot of the sidebar.
 *
 * Structure from the reference: a bordered box whose upper half is empty white space
 * holding a right-aligned icon row plus a close button, and whose lower edge carries a
 * filled brand band, with the heading and body copy below the band.
 *
 * The reference's band is an amber gradient; this uses a brand gradient so it stays
 * consistent with the promo banner.
 */
@Component({
  selector: 'app-reading-list-card',
  imports: [HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucideThumbsUp, lucideBookmark, lucideEllipsis, lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="overflow-hidden rounded-lg border border-border bg-background">
      <div class="flex items-center justify-end gap-1 px-3 pt-3">
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="Like">
          <ng-icon name="lucideThumbsUp" class="size-4 text-muted-foreground" />
        </button>
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="Save">
          <ng-icon name="lucideBookmark" class="size-4 text-muted-foreground" />
        </button>
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="More options">
          <ng-icon name="lucideEllipsis" class="size-4 text-muted-foreground" />
        </button>
        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          class="ms-1 text-muted-foreground"
          aria-label="Dismiss reading list"
          (click)="dismiss.emit()"
        >
          <ng-icon name="lucideX" class="size-4" />
        </button>
      </div>

      <div class="h-24 bg-gradient-to-r from-brand/25 via-brand/40 to-brand/70"></div>

      <div class="p-4">
        <h3 class="text-sm font-bold">Reading list</h3>
        <p class="mt-1 text-[13px] leading-relaxed text-muted-foreground">
          Click the save icon on any story to add it to your reading list or a custom list
          that you can share.
        </p>
      </div>
    </div>
  `,
})
export class ReadingListCard {
  readonly dismiss = output<void>();
}