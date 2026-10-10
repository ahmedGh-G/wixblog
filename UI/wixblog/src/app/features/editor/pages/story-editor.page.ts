import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-story-editor-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto w-full max-w-3xl px-4 py-8">
      <h1 class="text-2xl font-semibold tracking-tight">Edit story</h1>
      <p class="mt-1 text-sm text-muted-foreground">
        Guarded by <code>unsavedChangesGuard</code> — implement
        <code>hasUnsavedChanges()</code> on this component to activate the prompt.
      </p>
    </section>
  `,
  styles: [],
})
export class StoryEditorPage {}