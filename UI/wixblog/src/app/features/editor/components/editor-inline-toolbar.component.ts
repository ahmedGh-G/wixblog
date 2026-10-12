import { ChangeDetectionStrategy, Component, computed, input, output, OutputEmitterRef, signal } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBold,
  lucideHeading2,
  lucideImagePlus,
  lucideItalic,
  lucideMinus,
  lucidePlus,
  lucideQuote,
  lucideX
} from '@ng-icons/lucide';

/** One tool in the expanding menu. Holds its own emitter so the template stays flat. */
interface EditorToolbarTool {
  readonly id: string;
  readonly icon: string;
  readonly label: string;
  readonly emit: OutputEmitterRef<void>;
}

/**
 * `app-editor-inline-toolbar` — the animated floating toolbar for the story editor.
 *
 * Collapsed it is a single `+` circle. Clicking expands the panel, staggers the tool
 * buttons in, and rotates `+` 45 degrees so it reads as an `✕`. Clicking again
 * collapses it. There is no separate close button: the toggle doubles as close.
 *
 * Presentation only. Every action is emitted upward so the page can call the Editor.js
 * API; this component never touches the editor instance.
 *
 * All template and styles are inline, per `AGENT.md` section 1.
 */
@Component({
  selector: 'app-editor-inline-toolbar',
  imports: [NgIcon],
  providers: [
    provideIcons({
      lucidePlus,
      lucideX,
      lucideImagePlus,
      lucideBold,
      lucideItalic,
      lucideHeading2,
      lucideQuote,
      lucideMinus,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="bg-card border-border relative inline-flex overflow-hidden rounded-full border shadow-sm transition-[width] duration-300 ease-out motion-reduce:transition-none"
      [style.width.rem]="panelWidth()"
      [attr.data-state]="open() ? 'open' : 'closed'"
      role="toolbar"
      aria-label="Editor formatting"
    >
      <!-- Toggle: '+' rotates into '✕' -->
      <button
        type="button"
        class="text-brand hover:bg-brand/10 relative grid size-9 shrink-0 place-items-center rounded-full transition-colors"
        [attr.aria-expanded]="open()"
        aria-controls="editor-inline-toolbar-items"
        [attr.aria-label]="open() ? 'Close toolbar' : 'Open toolbar'"
        (click)="toggle()"
      >
        <ng-icon
          name="lucidePlus"
          class="size-4 transition-transform duration-300 ease-out motion-reduce:transition-none"
          [class.rotate-45]="open()"
          aria-hidden="true"
        />
      </button>

      <div id="editor-inline-toolbar-items" class="flex items-center gap-1.5 py-1.5 pr-4 pl-1">
        @for (tool of tools; track tool.id; let i = $index) {
          <button
            type="button"
            class="editor-tool border-brand text-brand hover:bg-brand/10 grid size-9 shrink-0 place-items-center rounded-full border transition-colors"
            [style.--i]="i"
            [attr.data-visible]="open()"
            [attr.aria-label]="tool.label"
            [disabled]="disabled()"
            (mousedown)="holdFocus($event)"
            (click)="tool.emit.emit()"
          >
            <ng-icon [name]="tool.icon" class="size-4" aria-hidden="true" />
          </button>
        }
      </div>
    </div>
  `,
  styles: [
    `
      /* Tailwind v4 requires an explicit reference for @apply in component styles. */
      @reference '../../../../styles.scss';

      :host {
        display: inline-block;
      }

      /* ---- toolbarState: collapsed width is just the toggle circle ---------- */
      :host ::ng-deep .editor-tool {
        opacity: 0;
        transform: translateX(-6px);
      }

      :host ::ng-deep .editor-tool[data-visible='true'] {
        animation: staggerItems 260ms cubic-bezier(0.22, 1, 0.36, 1) both;
        animation-delay: calc(var(--i, 0) * 45ms);
      }

      @keyframes staggerItems {
        from {
          opacity: 0;
          transform: translateX(-6px);
        }
        to {
          opacity: 1;
          transform: translateX(0);
        }
      }

      @media (prefers-reduced-motion: reduce) {
        :host ::ng-deep .editor-tool[data-visible='true'] {
          animation: none;
          opacity: 1;
          transform: none;
        }
      }
    `,
  ],
})
export class EditorInlineToolbar {
  readonly image = output<void>();
  readonly bold = output<void>();
  readonly italic = output<void>();
  readonly heading = output<void>();
  readonly quote = output<void>();
  readonly separator = output<void>();

  readonly disabled = input(false);

  /** Whether the panel is expanded. */
  readonly open = signal(false);
  protected readonly tools: readonly EditorToolbarTool[] = [
    { id: 'image', icon: 'lucideImagePlus', label: 'Insert image', emit: this.image },
    { id: 'bold', icon: 'lucideBold', label: 'Bold', emit: this.bold },
    { id: 'italic', icon: 'lucideItalic', label: 'Italic', emit: this.italic },
    { id: 'heading', icon: 'lucideHeading2', label: 'Heading', emit: this.heading },
    { id: 'quote', icon: 'lucideQuote', label: 'Quote', emit: this.quote },
    { id: 'separator', icon: 'lucideMinus', label: 'Insert separator', emit: this.separator },
  ];
  /** Collapsed fits the single toggle; expanded fits the toggle plus six 2.25rem tools. */
  private readonly collapsedWidth = 2.25;
  private readonly expandedWidth = 18.5;
  protected readonly panelWidth = computed(() =>
    this.open() ? this.expandedWidth : this.collapsedWidth,
  );

  protected toggle = () => {
    if (this.disabled()) return;
    this.open.update((isOpen) => !isOpen);
  };

  /**
   * Stops the button from taking focus.
   *
   * This bar lives outside the editor's contenteditable, so the default mousedown
   * would blur the field and collapse the selection before the click handler runs.
   * Bold and italic apply to whatever is selected, so losing that selection would make
   * them silently no-op. `open()` is reset separately once a tool has fired.
   */
  protected holdFocus = (event: MouseEvent) => event.preventDefault();
}
