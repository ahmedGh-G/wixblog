import { ChangeDetectionStrategy, Component } from '@angular/core';

/**
 * The promo strip that sits between the navbar and the feed.
 *
 * The reference uses a saturated amber band with a white pill label. Rebranded, the band
 * is a brand-tinted wash with a solid brand pill, so the contrast hierarchy of the
 * original (light label block, dark text on it, underlined CTA) is preserved while the
 * colour comes from `--brand` in `styles.scss`.
 */
@Component({
  selector: 'app-feed-promo-banner',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="bg-brand/10 border-brand/20 border-b">
      <div class="flex flex-wrap items-center justify-center gap-2 px-4 py-2.5 text-sm">
        <span class="bg-brand text-brand-foreground rounded-4xl px-3 py-1 text-xs font-semibold">
          Welcome Offer
        </span>
        <span class="text-foreground">Access to everything. Now 30% off.</span>
        <a
          href="#upgrade"
          class="text-brand font-semibold underline underline-offset-2 hover:opacity-80"
        >
          Upgrade now
        </a>
      </div>
    </div>
  `,
  styles: [],
})
export class FeedPromoBanner {}
