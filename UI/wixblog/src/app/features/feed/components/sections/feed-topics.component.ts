import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideX } from '@ng-icons/lucide';
import { HlmItemImports } from '@spartan-ng/helm/item';

import { TOPICS } from '../../data/feed-ui.data';
import { FeedDiscoverHeading } from './feed-discover-heading.component';

/**
 * "Recommended topics": a two-column grid of outlined pills, each with a `+` on the
 * right. Follow state is local to this component — nothing on the feed persists a topic
 * subscription yet.
 */
@Component({
  selector: 'app-feed-topics',
  imports: [FeedDiscoverHeading, HlmItemImports, NgIcon],
  providers: [provideIcons({ lucidePlus, lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section>
      <app-feed-discover-heading title="Recommended topics" action="See more topics" />

      <hlm-item-group class="grid grid-cols-2">
        @for (topic of topics(); track topic) {
          <hlm-item size="xs" variant="outline">
            <hlm-item-content>
              <button
                type="button"
                class="flex w-full items-center justify-between gap-1 text-start text-[13px]"
                [attr.aria-pressed]="following().has(topic)"
                (click)="toggle(topic)"
              >
                <span class="truncate">{{ topic }}</span>
                @if (following().has(topic)) {
                  <ng-icon name="lucideX" class="text-muted-foreground size-3.5 shrink-0" />
                } @else {
                  <ng-icon name="lucidePlus" class="text-muted-foreground size-3.5 shrink-0" />
                }
              </button>
            </hlm-item-content>
          </hlm-item>
        }
      </hlm-item-group>
    </section>
  `,
  styles: [],
})
export class FeedTopics {
  readonly topics = input<readonly string[]>(TOPICS);
  protected readonly following = signal<ReadonlySet<string>>(new Set());

  protected toggle = (topic: string) =>
    this.following.update((set) => {
      const next = new Set(set);
      next.has(topic) ? next.delete(topic) : next.add(topic);
      return next;
    });
}
