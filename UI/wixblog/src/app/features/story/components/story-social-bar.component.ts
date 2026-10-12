import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBookmark,
  lucideBookmarkCheck,
  lucideCirclePlay,
  lucideHand,
  lucideMessageCircle,
  lucideMoreHorizontal,
  lucideReply,
  lucideShare2,
} from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

@Component({
  selector: 'app-story-social-bar',
  imports: [DecimalPipe, NgIcon, HlmButtonImports],
  providers: [
    provideIcons({
      lucideBookmark,
      lucideBookmarkCheck,
      lucideCirclePlay,
      lucideHand,
      lucideMessageCircle,
      lucideMoreHorizontal,
      lucideReply,
      lucideShare2,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex items-center justify-between gap-2 border-y border-border py-3">
      <div class="flex items-center gap-5">
        <button
          hlmBtn
          variant="ghost"
          size="sm"
          type="button"
          class="gap-1.5 px-0 hover:bg-transparent"
          [attr.aria-pressed]="clapped()"
          [attr.aria-label]="clapped() ? 'Remove clap' : 'Clap'"
          (click)="clapToggle.emit()"
        >
          <ng-icon name="lucideHand" [class.text-primary]="clapped()" class="text-[length:18px]" />
          <span class="tabular-nums">{{ claps() | number }}</span>
        </button>

        <a
          [href]="responsesAnchor()"
          class="text-muted-foreground hover:text-foreground inline-flex items-center gap-1.5 text-sm"
          aria-label="Jump to responses"
        >
          <ng-icon name="lucideMessageCircle" class="text-[length:18px]" />
          <span class="tabular-nums">{{ responses() | number }}</span>
        </a>

        <span class="text-muted-foreground hidden items-center gap-1.5 text-sm sm:inline-flex">
          <ng-icon name="lucideReply" class="text-[length:18px]" />
          <span class="tabular-nums">{{ replies() | number }}</span>
        </span>
      </div>

      <div class="flex items-center gap-1">
        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          [attr.aria-pressed]="saved()"
          [attr.aria-label]="saved() ? 'Remove bookmark' : 'Save story'"
          (click)="saveToggle.emit()"
        >
          <ng-icon [name]="saved() ? 'lucideBookmarkCheck' : 'lucideBookmark'" />
        </button>

        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          aria-label="Listen to this story"
        >
          <ng-icon name="lucideCirclePlay" />
        </button>

        <button
          hlmBtn
          variant="ghost"
          size="icon-sm"
          type="button"
          aria-label="Share"
          (click)="share.emit()"
        >
          <ng-icon name="lucideShare2" />
        </button>

        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="More actions">
          <ng-icon name="lucideMoreHorizontal" />
        </button>
      </div>
    </div>
  `,
})
export class StorySocialBar {
  readonly claps = input.required<number>();
  readonly clapped = input(false);
  readonly responses = input.required<number>();
  readonly replies = input(0);
  readonly saved = input(false);
  readonly responsesAnchor = input('#responses');

  readonly clapToggle = output<void>();
  readonly saveToggle = output<void>();
  readonly share = output<void>();
}
