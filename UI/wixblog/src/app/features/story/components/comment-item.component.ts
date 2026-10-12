import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideHand, lucideMessageCircle, lucideMoreHorizontal } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import type { StoryComment } from '../data/story.model';

@Component({
  selector: 'app-comment-item',
  imports: [DatePipe, NgIcon, HlmButtonImports],
  providers: [provideIcons({ lucideHand, lucideMessageCircle, lucideMoreHorizontal })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <article class="flex gap-3 border-b border-border py-6 last:border-b-0">
      @if (comment().author.avatarUrl; as src) {
        <img [src]="src" alt="" class="size-7 shrink-0 rounded-full object-cover" />
      } @else {
        <span
          class="grid size-7 shrink-0 place-items-center rounded-full bg-muted text-xs font-medium"
        >
          {{ comment().author.name.charAt(0) }}
        </span>
      }

      <div class="flex min-w-0 flex-1 flex-col gap-2">
        <header class="flex items-center justify-between gap-2">
          <div class="flex flex-wrap items-center gap-x-2 text-xs text-muted-foreground">
            <span class="font-semibold text-foreground">{{ comment().author.name }}</span>
            <time [attr.datetime]="comment().createdAt">{{
              comment().createdAt | date: 'MMM d'
            }}</time>
            @if (comment().edited) {
              <span>(edited)</span>
            }
          </div>

          <button hlmBtn variant="ghost" size="icon-xs" type="button" aria-label="Comment actions">
            <ng-icon name="lucideMoreHorizontal" />
          </button>
        </header>

        @if (comment().quote; as quote) {
          <blockquote class="border border-border bg-muted px-3 py-2 text-sm text-muted-foreground">
            {{ quote }}
          </blockquote>
        }

        <p class="text-[0.9375rem] leading-relaxed">{{ comment().body }}</p>

        <div class="flex items-center gap-4">
          <span class="text-muted-foreground inline-flex items-center gap-1.5 text-xs">
            <ng-icon name="lucideHand" class="text-[length:15px]" />
            <span class="tabular-nums">{{ comment().claps }}</span>
          </span>

          @if (comment().replies > 0) {
            <span class="text-muted-foreground inline-flex items-center gap-1.5 text-xs">
              <ng-icon name="lucideMessageCircle" class="text-[length:15px]" />
              {{ comment().replies }} {{ comment().replies === 1 ? 'reply' : 'replies' }}
            </span>
          }

          <button
            hlmBtn
            variant="link"
            size="xs"
            type="button"
            class="px-0"
            (click)="replyClick.emit(comment().id)"
          >
            Reply
          </button>
        </div>
      </div>
    </article>
  `,
})
export class CommentItem {
  readonly comment = input.required<StoryComment>();
  readonly replyClick = output<string>();
}
