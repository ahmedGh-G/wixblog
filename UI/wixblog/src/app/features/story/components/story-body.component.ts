import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideCheck } from '@ng-icons/lucide';
import { HlmButtonImports } from '@spartan-ng/helm/button';

import type { StoryBlock, StoryPaywall } from '../data/story.model';

@Component({
  selector: 'app-story-body',
  imports: [NgIcon, HlmButtonImports],
  providers: [provideIcons({ lucideCheck })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col gap-6 text-[1.0625rem] leading-[1.7]">
      @for (block of blocks(); track $index) {
        @switch (block.kind) {
          @case ('paragraph') {
            <p>{{ block.text }}</p>
          }
          @case ('heading') {
            <h2 class="mt-4 text-2xl font-bold tracking-tight">{{ block.text }}</h2>
          }
          @case ('image') {
            <figure class="flex flex-col gap-2">
              <img
                [src]="block.src"
                [alt]="block.alt"
                loading="lazy"
                decoding="async"
                class="w-full rounded-md"
              />
              @if (block.caption) {
                <figcaption class="text-sm text-muted-foreground">{{ block.caption }}</figcaption>
              }
            </figure>
          }
          @case ('chart') {
            <figure class="flex flex-col gap-2">
              <div class="relative">
                <img
                  [src]="block.src"
                  [alt]="block.caption"
                  loading="lazy"
                  decoding="async"
                  class="w-full rounded-md border border-border"
                />
                <span
                  class="absolute end-2 top-2 rounded bg-background/90 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wide text-muted-foreground"
                >
                  Data
                </span>
              </div>
              <figcaption class="flex flex-wrap items-baseline gap-x-2">
                <span class="font-semibold">{{ block.caption }}</span>
                <span class="text-sm text-muted-foreground">Source: {{ block.source }}</span>
              </figcaption>
            </figure>
          }
        }
      }

      @if (paywall(); as wall) {
        <section
          class="my-4 flex flex-col items-center gap-5 rounded-lg border border-border bg-muted/50 px-6 py-10 text-center"
        >
          <span class="rounded bg-amber-500 px-2 py-0.5 text-[11px] font-semibold text-white">
            {{ wall.eyebrow }}
          </span>
          <h2 class="max-w-sm text-2xl font-bold tracking-tight">{{ wall.headline }}</h2>
          <ul class="flex flex-col items-start gap-2 text-left text-sm">
            @for (benefit of wall.benefits; track benefit) {
              <li class="flex items-center gap-2">
                <ng-icon name="lucideCheck" class="text-primary shrink-0 text-[length:16px]" />
                <span>{{ benefit }}</span>
              </li>
            }
          </ul>
          <button hlmBtn variant="default" size="lg" type="button">{{ wall.ctaLabel }}</button>
        </section>
      }
    </div>
  `,
})
export class StoryBody {
  readonly blocks = input.required<StoryBlock[]>();
  readonly paywall = input<StoryPaywall | null>(null);
}
