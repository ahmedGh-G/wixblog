import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import { TOPIC_HEADER, type TopicHeaderData } from '../data/topic-detail.data';

/**
 * `protoyping/explore/topic/topic-header-section.png`
 *
 * Title and the Follow / Mute pair on the left, cover image on the right, and a
 * hairline above the three-up stat row.
 */
@Component({
  selector: 'app-topic-header',
  imports: [HlmButtonImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="grid gap-8 lg:grid-cols-[1fr_auto] lg:items-start">
      <div>
        <h1 class="text-4xl font-bold tracking-tight sm:text-5xl">{{ header().title }}</h1>

        <div class="mt-6 flex items-center gap-3">
          <button
            hlmBtn
            type="button"
            [variant]="following() ? 'outline' : 'default'"
            class="rounded-4xl px-6"
            [attr.aria-pressed]="following()"
            (click)="following.update((v) => !v)"
          >
            {{ following() ? 'Following' : 'Follow' }}
          </button>

          <button
            hlmBtn
            variant="outline"
            type="button"
            class="rounded-4xl px-6"
            [attr.aria-pressed]="muted()"
            (click)="muted.update((v) => !v)"
          >
            {{ muted() ? 'Unmute' : 'Mute' }}
          </button>
        </div>
      </div>

      <img
        [src]="header().coverUrl"
        [alt]="''"
        class="aspect-3/2 w-full rounded-lg object-cover sm:w-80 lg:aspect-auto lg:h-56 lg:w-[30rem]"
      />
    </section>

    <div class="border-border mt-10 border-t pt-6">
      <dl class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div class="flex items-baseline gap-2">
          <dt class="text-sm text-muted-foreground">Followers</dt>
          <dd class="text-sm font-semibold">{{ header().followers }}</dd>
        </div>
        <div class="flex items-baseline gap-2">
          <dt class="text-sm text-muted-foreground">Stories</dt>
          <dd class="text-sm font-semibold">{{ header().stories }}</dd>
        </div>
        <div class="flex items-baseline gap-2">
          <dt class="text-sm text-muted-foreground">Writers</dt>
          <dd class="text-sm font-semibold">{{ header().writers }}</dd>
        </div>
      </dl>
    </div>
  `,
  styles: [],
})
export class TopicHeader {
  readonly header = input<TopicHeaderData>(TOPIC_HEADER);

  protected readonly following = signal(false);
  protected readonly muted = signal(false);
}