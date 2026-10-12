import { ChangeDetectionStrategy, Component, computed, inject, output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideBell, lucideEllipsis } from '@ng-icons/lucide';

import { EditorStateService } from '../services/editor-state.service';

/**
 * `prototyping/editor/editor-topbar.png`
 *
 * Wordmark left-of-centre, draft status and the save indicator, then the overflow,
 * notification and avatar cluster on the right.
 */
@Component({
  selector: 'app-editor-topbar',
  imports: [NgIcon],
  providers: [provideIcons({ lucideEllipsis, lucideBell })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header class="border-border bg-background flex h-14 shrink-0 items-center gap-4 border-b px-6">
      <div class="flex flex-1 items-center justify-center gap-3">
        <span class="text-xl font-bold tracking-tight">Medium</span>
        <span class="text-sm font-medium">{{ statusLabel() }}</span>
        <span
          class="text-sm"
          [class.text-muted-foreground]="state.saveStatus() !== 'error'"
          [class.text-destructive]="state.saveStatus() === 'error'"
        >
          {{ saveLabel() }}
        </span>
      </div>

      <div class="flex flex-1 items-center justify-end gap-4">
        <button
          type="button"
          class="bg-brand text-brand-foreground hover:bg-brand/90 rounded-full px-4 py-1.5 text-sm font-semibold transition-colors"
          (click)="publish.emit()"
        >
          Publish
        </button>

        <button
          type="button"
          class="text-muted-foreground hover:text-foreground rounded-full p-2 transition-colors"
          aria-label="More options"
        >
          <ng-icon name="lucideEllipsis" class="size-5" />
        </button>

        <button
          type="button"
          class="text-muted-foreground hover:text-foreground rounded-full p-2 transition-colors"
          aria-label="Notifications"
        >
          <ng-icon name="lucideBell" class="size-5" />
        </button>

        <span class="bg-muted grid size-8 place-items-center rounded-full text-xs font-semibold"
          >A</span
        >
      </div>
    </header>
  `,
  styles: [],
})
export class EditorTopbar {
  readonly publish = output<void>();
  protected readonly state = inject(EditorStateService);
  protected readonly statusLabel = computed(() =>
    this.state.status() === 'DRAFT' ? 'Draft' : 'Published',
  );

  protected readonly saveLabel = computed(() => {
    switch (this.state.saveStatus()) {
      case 'saving':
        return 'Savingâ€¦';
      case 'error':
        return 'Not saved';
      default:
        return 'Saved';
    }
  });
}
