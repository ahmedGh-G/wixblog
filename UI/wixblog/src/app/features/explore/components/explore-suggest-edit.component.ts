import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * The "See a topic you think should be added or removed here? Suggest an edit." strip
 * that closes the taxonomy grid (`explore-topics-fullpage.png`).
 */
@Component({
  selector: 'app-explore-suggest-edit',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="bg-muted/60 text-muted-foreground flex flex-wrap items-center justify-center gap-x-1 gap-y-1 rounded-md px-4 py-3.5 text-center text-sm"
    >
      <span>See a topic you think should be added or removed here?</span>
      <a
        [routerLink]="suggestPath()"
        class="text-foreground font-medium underline underline-offset-2 transition-colors hover:text-brand"
      >
        Suggest an edit
      </a>
    </div>
  `,
  styles: [],
})
export class ExploreSuggestEdit {
  /** No dedicated route exists for this yet; the page itself is the honest target. */
  readonly suggestPath = input('/');
}