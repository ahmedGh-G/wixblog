import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-story-headline',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col gap-3">
      <h1 class="text-4xl font-bold leading-[1.15] tracking-tight text-balance">
        {{ title() }}
      </h1>
      @if (subtitle()) {
        <p class="text-lg text-muted-foreground">{{ subtitle() }}</p>
      }
    </div>
  `,
})
export class StoryHeadline {
  readonly title = input.required<string>();
  readonly subtitle = input('');
}
