import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import { PATHS } from '@shared/paths';

import type { StoryAuthor } from '../data/story.model';

@Component({
  selector: 'app-story-author-line',
  imports: [DatePipe, RouterLink, HlmButtonImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-wrap items-center gap-x-3 gap-y-2">
      @if (author().avatarUrl; as src) {
        <img [src]="src" alt="" class="size-8 rounded-full object-cover" />
      } @else {
        <span class="grid size-8 place-items-center rounded-full bg-muted text-xs font-medium">
          {{ author().name.charAt(0) }}
        </span>
      }

      <a [routerLink]="profileLink()" class="text-sm font-semibold hover:underline">
        {{ author().name }}
      </a>

      <button hlmBtn variant="outline" size="xs" type="button" (click)="followToggle.emit()">
        {{ following() ? 'Following' : 'Follow' }}
      </button>

      <span class="text-sm text-muted-foreground">{{ readingMinutes() }} min read</span>
      <span aria-hidden="true" class="text-muted-foreground">·</span>
      <time [attr.datetime]="publishedAt()" class="text-sm text-muted-foreground">
        {{ publishedAt() | date: 'MMM d, y' }}
      </time>
    </div>
  `,
})
export class StoryAuthorLine {
  readonly author = input.required<StoryAuthor>();
  readonly readingMinutes = input.required<number>();
  readonly publishedAt = input.required<string>();
  readonly following = input(false);
  readonly followToggle = output<void>();

  protected profileLink() {
    return PATHS.profile(this.author().username);
  }
}
