import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-new-story-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto w-full max-w-3xl px-4 py-8">
      <h1 class="text-2xl font-semibold tracking-tight">New story</h1>
      <p class="mt-1 text-sm text-muted-foreground">
        Compose surface. Draft state belongs in
        <code>features/editor/data</code>.
      </p>
    </section>
  `,
  styles: [],
})
export class NewStoryPage {}