import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';

import { TOPIC_SUBTOPICS, type TopicSubtopic } from '../data/topic-detail.data';
import { TopicStoryRow } from './topic-story-row.component';

/**
 * `prototyping/explore/topic/topic-exploer-section.png`
 *
 * "Explore the Topic" heading, then a per-subtopic split: description on the left,
 * story list on the right. The last two subtopics in the reference are still loading,
 * which `pending` reproduces with hlmSkeleton blocks.
 */
@Component({
  selector: 'app-topic-explorer-section',
  imports: [TopicStoryRow, HlmSkeletonImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <h2 class="text-2xl font-bold tracking-tight">Explore the Topic</h2>
      <button type="button" class="text-muted-foreground hover:text-foreground mt-1 text-sm">
        Suggested reads, summarized by AI â€º
      </button>
    </section>

    @for (subtopic of subtopics(); track subtopic.label) {
      <section class="border-border mt-12 border-t pt-8">
        <h3 class="text-lg font-bold tracking-tight">{{ subtopic.label }}</h3>

        <div class="grid grid-cols-1 gap-x-12 gap-y-8 lg:grid-cols-[minmax(0,22rem)_1fr]">
          <div>
            <p class="text-muted-foreground text-sm">{{ subtopic.storyCount }} stories</p>
            <p class="mt-1 text-sm font-semibold">{{ subtopic.description }}</p>
          </div>

          <div>
            @for (story of subtopic.stories; track story.id) {
              <app-topic-story-row [story]="story" />
            } @empty {
              @if (subtopic.pending) {
                <div class="space-y-8" aria-busy="true">
                  @for (i of [1, 2, 3]; track i) {
                    <div class="flex gap-6">
                      <div class="flex-1 space-y-3">
                        <div class="flex items-center gap-2">
                          <div hlmSkeleton class="size-5 shrink-0 rounded-full"></div>
                          <div hlmSkeleton class="h-3 w-32"></div>
                        </div>
                        <div hlmSkeleton class="h-4 w-4/5"></div>
                        <div hlmSkeleton class="h-3 w-full"></div>
                      </div>
                      <div hlmSkeleton class="size-20 shrink-0 sm:size-24"></div>
                    </div>
                  }
                </div>
              } @else {
                <p class="text-muted-foreground py-8 text-sm">No stories yet.</p>
              }
            }
          </div>
        </div>
      </section>
    }
  `,
  styles: [],
})
export class TopicExplorerSection {
  readonly subtopics = input<readonly TopicSubtopic[]>(TOPIC_SUBTOPICS);
}
