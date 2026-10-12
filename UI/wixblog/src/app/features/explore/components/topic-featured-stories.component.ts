import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';

import { POPULAR_STORIES, SELECTED_STORIES, type TopicStory } from '../data/topic-detail.data';
import { TopicStoryRow } from './topic-story-row.component';

/**
 * `prototyping/explore/topic/selected(featured)-topic-stories.png`
 *
 * Two equal columns â€” "Selected by Medium" and "Popular this week" â€” each a heading,
 * a hairline, then five rows.
 */
@Component({
  selector: 'app-topic-featured-stories',
  imports: [TopicStoryRow],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="grid grid-cols-1 gap-x-16 gap-y-10 lg:grid-cols-2">
      <section>
        <h2 class="text-xl font-bold tracking-tight">Selected by Medium</h2>
        <div class="border-border mt-4 border-t"></div>

        @for (story of selected(); track story.id) {
          <app-topic-story-row [story]="story" [bookmarked]="bookmarkedIds().has(story.id)" />
        }
      </section>

      <section>
        <h2 class="text-xl font-bold tracking-tight">Popular this week</h2>
        <div class="border-border mt-4 border-t"></div>

        @for (story of popular(); track story.id) {
          <app-topic-story-row [story]="story" [bookmarked]="bookmarkedIds().has(story.id)" />
        }
      </section>
    </div>
  `,
  styles: [],
})
export class TopicFeaturedStories {
  readonly selected = input<readonly TopicStory[]>(SELECTED_STORIES);
  readonly popular = input<readonly TopicStory[]>(POPULAR_STORIES);

  protected readonly bookmarkedIds = signal<ReadonlySet<string>>(new Set());
}
