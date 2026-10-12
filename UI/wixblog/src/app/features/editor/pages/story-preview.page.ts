import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-story-preview-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto w-full max-w-3xl px-4 py-8">
      <h1 class="text-2xl font-semibold tracking-tight">Story preview</h1>
      <p class="mt-1 text-sm text-muted-foreground">
        Read-only render of the draft, sharing the story renderer with
        <code>features/story</code>.
      </p>
    </section>
  `,
  styles: [],
})
export class StoryPreviewPage {}
