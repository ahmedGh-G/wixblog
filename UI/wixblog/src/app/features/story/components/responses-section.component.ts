import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideSettings } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';

import type { StoryComment } from '../data/story.model';
import { CommentComposer } from './comment-composer.component';
import { CommentItem } from './comment-item.component';

@Component({
  selector: 'app-responses-section',
  imports: [CommentComposer, CommentItem, NgIcon, HlmButtonImports, HlmSkeletonImports],
  providers: [provideIcons({ lucideSettings })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section id="responses" class="flex flex-col gap-6 border-t border-border pt-8">
      <header class="flex items-center justify-between gap-3">
        <h2 class="text-xl font-bold tracking-tight">Responses ({{ count() }})</h2>
        <button hlmBtn variant="ghost" size="icon-sm" type="button" aria-label="Response settings">
          <ng-icon name="lucideSettings" />
        </button>
      </header>

      @if (loading()) {
        <div class="flex flex-col gap-6">
          @for (i of [1, 2, 3]; track i) {
            <div class="flex gap-3">
              <div hlmSkeleton class="size-7 shrink-0 rounded-full"></div>
              <div class="flex flex-1 flex-col gap-2">
                <div hlmSkeleton class="h-3 w-32"></div>
                <div hlmSkeleton class="h-3 w-full"></div>
                <div hlmSkeleton class="h-3 w-4/5"></div>
              </div>
            </div>
          }
        </div>
      } @else if (error()) {
        <p class="text-sm text-destructive">{{ error() }}</p>
      } @else {
        <app-comment-composer
          [user]="user()"
          [value]="draft()"
          (valueChange)="draftChange.emit($event)"
          (submit)="submit.emit()"
        />

        <div>
          @for (comment of comments(); track comment.id) {
            <app-comment-item [comment]="comment" (replyClick)="replyClick.emit($event)" />
          } @empty {
            <p class="py-8 text-center text-sm text-muted-foreground">
              No responses yet. Start the conversation.
            </p>
          }
        </div>

        @if (comments().length > 0) {
          <div>
            <button hlmBtn variant="outline" size="sm" type="button" class="rounded-full">
              See all responses
            </button>
          </div>
        }
      }
    </section>
  `,
})
export class ResponsesSection {
  readonly count = input.required<number>();
  readonly comments = input.required<StoryComment[]>();
  readonly user = input.required<{ name: string; avatarUrl: string | null }>();
  readonly draft = input('');
  readonly loading = input(false);
  readonly error = input<string | null>(null);

  readonly draftChange = output<string>();
  readonly submit = output<void>();
  readonly replyClick = output<string>();
}
