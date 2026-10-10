import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideBookmark, lucideBookmarkCheck } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { PostSummary } from './post-summary.model';

/** Byline: avatar, author, date, reading time. Pure display. */
@Component({
  selector: 'app-post-meta',
  imports: [DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-w-0 items-center gap-2 text-xs text-muted-foreground">
      @if (showAvatar()) {
        @if (post().author.avatarUrl; as src) {
          <img [src]="src" alt="" loading="lazy" class="size-5 rounded-full object-cover" />
        } @else {
          <span
            class="grid size-5 place-items-center rounded-full bg-muted text-[10px] font-medium text-foreground"
          >
            {{ initial() }}
          </span>
        }
      }
      <span class="truncate font-medium text-foreground">{{ post().author.name }}</span>
      <span aria-hidden="true">·</span>
      <time [attr.datetime]="post().publishedAt">{{ post().publishedAt | date: 'MMM d' }}</time>
      @if (showReadTime()) {
        <span aria-hidden="true">·</span>
        <span>{{ post().readingMinutes }} min read</span>
      }
    </div>
  `,
})
export class PostMeta {
  readonly post = input.required<PostSummary>();
  readonly showAvatar = input(true);
  readonly showReadTime = input(true);
  protected initial = computed(() => this.post().author.name.charAt(0).toUpperCase());
}

/** Category pill that links to the category page. */
@Component({
  selector: 'app-post-category-chip',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <a
      [routerLink]="['/category', slug()]"
      class="relative z-10 rounded-full bg-muted px-2.5 py-0.5 text-xs text-muted-foreground hover:bg-accent hover:text-accent-foreground"
    >
      <ng-content />
    </a>
  `,
})
export class PostCategoryChip {
  readonly slug = input.required<string>();
}

/** Bookmark toggle. Emits; the smart parent decides what that means. */
@Component({
  selector: 'app-post-bookmark-button',
  imports: [HlmButtonImports, NgIcon],
  providers: [provideIcons({ lucideBookmark, lucideBookmarkCheck })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <button
      hlmBtn
      variant="ghost"
      size="icon-sm"
      type="button"
      class="relative z-10"
      [attr.aria-pressed]="active()"
      [attr.aria-label]="active() ? 'Remove bookmark' : 'Save for later'"
      (click)="toggle.emit()"
    >
      <ng-icon [name]="active() ? 'lucideBookmarkCheck' : 'lucideBookmark'" />
    </button>
  `,
})
export class PostBookmarkButton {
  readonly active = input(false);
  readonly toggle = output<void>();
}
