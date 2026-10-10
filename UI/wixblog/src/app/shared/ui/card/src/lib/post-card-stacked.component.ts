import {ChangeDetectionStrategy, Component, computed, input, output} from '@angular/core';
import {RouterLink} from '@angular/router';
import {HlmCardImports} from '@spartan-ng/helm/card';

import {PostBookmarkButton, PostCategoryChip, PostMeta} from './post-card-parts.component';
import {PostSummary} from './post-summary.model';

/**
 * Vertical card: cover on top, text below, on a boxed card surface.
 *
 *   size "lg" - hero / featured slot
 *   size "md" - recommended grids
 *
 * All card styling comes from `HlmCardImports`, which is the single customised
 * definition of the card directives. Do not import `HlmCardHeader`, `HlmCardFooter`,
 * `HlmCardContent` or `HlmCardTitle` from separate files: those were stale duplicates
 * carrying pre-customisation values (`rounded-t-xl`, `text-base font-medium`) that
 * fought the customised directives for the same elements.
 */
@Component({
  selector: 'app-post-card-stacked',
  imports: [HlmCardImports, RouterLink, PostMeta, PostCategoryChip, PostBookmarkButton],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <hlm-card size="sm" class="relative h-full transition-colors hover:bg-muted/40">
      @if (post().coverUrl; as cover) {
        <img [src]="cover" alt="" loading="lazy" decoding="async" [class]="coverClass()" />
      }
      <hlm-card-header>
        <app-post-meta [post]="post()" [showReadTime]="false" />
        <h3 hlmCardTitle>
          <!-- Typography lives on the anchor, not the heading: see the class note below. -->
          <a
            [routerLink]="['/post', post().slug]"
            class="after:absolute after:inset-0"
            [class]="titleClass()"
            >{{ post().title }}</a
          >
        </h3>
      </hlm-card-header>
      <div hlmCardContent>
        <p [class]="excerptClass()">{{ post().excerpt }}</p>
      </div>
      <hlm-card-footer class="gap-3">
        @if (post().category; as cat) {
          <app-post-category-chip [slug]="cat.slug">{{ cat.name }}</app-post-category-chip>
        }
        <span class="text-xs text-muted-foreground">{{ post().readingMinutes }} min read</span>
        <app-post-bookmark-button
          class="ms-auto"
          [active]="bookmarked()"
          (toggle)="bookmarkToggle.emit(post().id)"
        />
      </hlm-card-footer>
    </hlm-card>
  `,
})
export class PostCardStacked {
  readonly post = input.required<PostSummary>();
  readonly size = input<'lg' | 'md'>('md');
  readonly bookmarked = input(false);
  readonly bookmarkToggle = output<string>();

  protected readonly isLarge = computed(() => this.size() === 'lg');

  /**
   * Applied to the anchor inside the heading, never to the `hlmCardTitle` element
   * itself.
   *
   * `hlmCardTitle` sets `text-base` through `classes()`, which writes directly into the
   * element's class list. Binding `[class]` on that same element to force a larger size
   * races with the directive, and the winner is decided by stylesheet source order
   * rather than by intent, so the hero headline rendered at an unpredictable size. The
   * anchor carries no directive classes, so the sizes below always apply. The explicit
   * `font-semibold` restates the directive's weight so the two variants stay legible
   * even if the shared directive's weight is ever changed.
   */

  /**
   * The hero variant uses a taller crop than 16:9 so the image reads as the focal point
   * of the slot. 16:9 is retained for the grid variant, where uniform tiles matter more
   * than presence.
   */
  protected coverClass = computed(() =>
    this.isLarge() ? 'aspect-[3/2] w-full object-cover' : 'aspect-video w-full object-cover',
  );

  protected titleClass = computed(() =>
    this.isLarge()
      ? 'text-2xl font-semibold leading-tight tracking-tight md:text-3xl'
      : 'text-lg font-semibold leading-snug tracking-tight',
  );

  protected excerptClass = computed(() =>
    this.isLarge()
      ? 'line-clamp-3 text-base text-muted-foreground'
      : 'line-clamp-2 text-sm text-muted-foreground',
  );
}
