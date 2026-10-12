import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmTextareaImports } from '@spartan-ng/helm/textarea';

@Component({
  selector: 'app-comment-composer',
  imports: [FormsModule, HlmButtonImports, HlmTextareaImports],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex gap-3">
      @if (user().avatarUrl; as src) {
        <img [src]="src" alt="" class="size-7 shrink-0 rounded-full object-cover" />
      } @else {
        <span
          class="grid size-7 shrink-0 place-items-center rounded-full bg-muted text-xs font-medium"
        >
          {{ user().name.charAt(0) }}
        </span>
      }

      <div class="flex min-w-0 flex-1 flex-col gap-3">
        <span class="text-sm font-semibold">{{ user().name }}</span>

        <textarea
          hlmTextarea
          rows="3"
          name="comment"
          placeholder="What are your thoughts?"
          class="resize-none bg-muted"
          [ngModel]="value()"
          (ngModelChange)="valueChange.emit($event)"
        ></textarea>

        <div class="flex justify-end">
          <button
            hlmBtn
            size="sm"
            type="button"
            [disabled]="!value().trim()"
            (click)="submit.emit()"
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  `,
})
export class CommentComposer {
  readonly user = input.required<{ name: string; avatarUrl: string | null }>();
  readonly value = input('');
  readonly valueChange = output<string>();
  readonly submit = output<void>();
}
