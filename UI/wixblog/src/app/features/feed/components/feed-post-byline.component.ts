import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';
import { feedMetaFor } from '../data/feed-ui.data';

/**
 * Byline for a feed row: 20px avatar, bold author, separator, relative date, then the
 * publication list on its own line ("in A, B and C").
 *
 * The avatar falls back to the initial when the post has no avatar URL, which is the
 * common case for the mock feed.
 */
@Component({
  selector: 'app-feed-post-byline',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="text-muted-foreground flex items-center gap-1.5 text-xs">
      @if (post().author.avatarUrl; as src) {
        <img [src]="src" alt="" loading="lazy" class="size-5 shrink-0 rounded-full object-cover" />
      } @else {
        <span
          class="bg-muted text-foreground grid size-5 shrink-0 place-items-center rounded-full text-[9px] font-semibold"
        >
          {{ initial() }}
        </span>
      }
      <span class="text-foreground truncate font-semibold">{{ post().author.name }}</span>
      <span aria-hidden="true">·</span>
      <span>{{ meta().ago }}</span>
    </div>
    @if (meta().publications.length) {
      <p class="text-muted-foreground text-xs">in {{ publicationList() }}</p>
    }
  `,
  styles: [],
})
export class FeedPostByline {
  readonly post = input.required<PostSummary>();

  protected readonly meta = computed(() => feedMetaFor(this.post()));
  protected readonly initial = computed(() => this.post().author.name.charAt(0).toUpperCase());

  /**
   * "A", "A and B", "A, B and C" — the reference truncates the tail rather than
   * wrapping, and a wrap would push the row heights out of alignment.
   */
  protected readonly publicationList = computed(() => {
    const names = this.meta().publications;
    if (names.length <= 1) return names[0] ?? '';
    if (names.length === 2) return `${names[0]} and ${names[1]}`;
    return `${names[0]}, ${names[1]} and ${names.slice(2).join(', ')}`;
  });
}