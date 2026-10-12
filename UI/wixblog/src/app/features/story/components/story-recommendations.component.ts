import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { PostCardStacked } from '@shared/ui/card/src/lib/post-card-stacked.component';
import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';
import { HlmButtonImports } from '@spartan-ng/helm/button';

@Component({
  selector: 'app-story-recommendations',
  imports: [PostCardStacked, HlmButtonImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="flex flex-col gap-5 border-t border-border pt-8">
      <h2 class="text-lg font-bold tracking-tight">{{ title() }}</h2>

      @if (loading()) {
        <div class="grid grid-cols-2 gap-6">
          @for (i of [1, 2, 3, 4]; track i) {
            <div hlmSkeleton class="h-56 w-full rounded-lg"></div>
          }
        </div>
      } @else if (error()) {
        <p class="text-sm text-destructive">{{ error() }}</p>
      } @else if (posts().length === 0) {
        <p class="py-8 text-center text-sm text-muted-foreground">{{ emptyLabel() }}</p>
      } @else {
        <div class="grid grid-cols-2 gap-x-6 gap-y-8">
          @for (post of posts(); track post.id) {
            <app-post-card-stacked [post]="post" />
          }
        </div>

        <div>
          <button hlmBtn variant="outline" size="sm" type="button" class="rounded-full">
            {{ ctaLabel() }}
          </button>
        </div>
      }
    </section>
  `,
})
export class StoryRecommendations {
  readonly title = input.required<string>();
  readonly posts = input.required<PostSummary[]>();
  readonly ctaLabel = input.required<string>();
  readonly emptyLabel = input('Nothing to show here yet.');
  readonly loading = input(false);
  readonly error = input<string | null>(null);
}
