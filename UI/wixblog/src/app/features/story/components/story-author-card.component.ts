import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucidePenLine, lucideUserPlus } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import type { StoryAuthor } from '../data/story.model';

@Component({
  selector: 'app-story-author-card',
  imports: [DecimalPipe, NgIcon, HlmButtonImports],
  providers: [provideIcons({ lucidePenLine, lucideUserPlus })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col gap-4 border-t border-border pt-8">
      <div class="flex items-start justify-between gap-4">
        @if (author().avatarUrl; as src) {
          <img [src]="src" alt="" class="size-14 shrink-0 rounded-full object-cover" />
        } @else {
          <span
            class="grid size-14 shrink-0 place-items-center rounded-full bg-muted text-lg font-medium"
          >
            {{ author().name.charAt(0) }}
          </span>
        }

        <div class="flex items-center gap-2">
          <button hlmBtn variant="outline" size="sm" type="button" (click)="followToggle.emit()">
            {{ following() ? 'Following' : 'Follow' }}
          </button>
          <button
            hlmBtn
            variant="outline"
            size="icon-sm"
            type="button"
            aria-label="More about this author"
          >
            <ng-icon name="lucidePenLine" />
          </button>
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <div>
          <h2 class="text-lg font-bold tracking-tight">Written by {{ author().name }}</h2>
          <p class="text-sm text-muted-foreground">
            {{ author().followersCount | number }} followers
            <span aria-hidden="true">·</span>
            {{ author().followingCount | number }} following
          </p>
        </div>

        <p class="text-sm leading-relaxed">
          {{ author().headline }}
          @if (author().bioLink; as link) {
            <a
              [href]="link.url"
              target="_blank"
              rel="noopener noreferrer"
              class="underline underline-offset-4 hover:text-muted-foreground"
            >
              {{ link.label }}
            </a>
            |
          }
          {{ author().bio }}
        </p>
      </div>
    </div>
  `,
})
export class StoryAuthorCard {
  readonly author = input.required<StoryAuthor>();
  readonly following = input(false);
  readonly followToggle = output<void>();
}
