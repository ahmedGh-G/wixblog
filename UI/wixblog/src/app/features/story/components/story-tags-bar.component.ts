import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideSparkles } from '@ng-icons/lucide';

import type { StoryTag } from '../data/story.model';

@Component({
  selector: 'app-story-tags-bar',
  imports: [NgIcon],
  providers: [provideIcons({ lucidePlus, lucideSparkles })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col gap-4">
      @if (membersOnly()) {
        <span
          class="inline-flex w-fit items-center gap-1.5 rounded-full bg-amber-500/10 px-2.5 py-1 text-xs font-medium text-amber-700 dark:text-amber-400"
        >
          <ng-icon name="lucideSparkles" class="text-[length:14px]" />
          Member-only story
        </span>
      }

      <div class="flex flex-wrap items-center gap-2">
        @for (tag of tags(); track tag.id) {
          <span
            class="inline-flex items-center gap-1 rounded-full border border-border px-3 py-1 text-sm"
          >
            {{ tag.label }}
            <button
              type="button"
              class="text-muted-foreground hover:text-foreground"
              [attr.aria-label]="'Add to ' + tag.label"
            >
              <ng-icon name="lucidePlus" class="text-[length:14px]" />
            </button>
          </span>
        }
      </div>
    </div>
  `,
})
export class StoryTagsBar {
  readonly tags = input.required<StoryTag[]>();
  readonly membersOnly = input(false);
}
