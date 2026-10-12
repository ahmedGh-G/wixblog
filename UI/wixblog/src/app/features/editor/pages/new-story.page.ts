import {
  afterNextRender,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  signal,
  viewChild,
} from '@angular/core';
import type EditorJS from '@editorjs/editorjs';
import type { OutputData } from '@editorjs/editorjs';

import { EditorInlineToolbar } from '../components/editor-inline-toolbar.component';
import { EditorTopbar } from '../components/editor-topbar.component';
import { NewStoryPreview, type PreviewCategory } from '../components/new-story-preview.component';
import { loadEditorTools } from '../data/editor-tools';
import { EditorApiService } from '../services/editor-api.service';
import { EditorStateService } from '../services/editor-state.service';

const CATEGORIES: readonly PreviewCategory[] = [
  { slug: 'technology', name: 'Technology' },
  { slug: 'life', name: 'Life' },
  { slug: 'self-improvement', name: 'Self Improvement' },
  { slug: 'media', name: 'Media' },
  { slug: 'culture', name: 'Culture' },
];

const MAX_TITLE = 140;

@Component({
  selector: 'app-new-story-page',
  imports: [EditorTopbar, EditorInlineToolbar, NewStoryPreview],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (!isPreviewVisible()) {
      <section class="bg-background flex min-h-dvh flex-col">
        <app-editor-topbar (publish)="isPreviewVisible.set(true)" />

        <div class="flex flex-1 flex-col items-center px-6 py-12">
          <input
            #titleInput
            type="text"
            placeholder="Title"
            aria-label="Story title"
            class="w-full max-w-2xl border-0 bg-transparent text-4xl font-bold tracking-tight outline-none placeholder:text-muted-foreground focus:ring-0"
            [value]="state.title()"
            (input)="onTitle($any($event.target).value)"
          />

          <p class="text-muted-foreground mt-3 w-full max-w-2xl text-sm">
            {{ state.title().length }}/{{ maxTitle }} Â· {{ state.wordCount() }} words
          </p>

          <app-editor-inline-toolbar
            class="mt-8"
            (image)="pickImage()"
            (bold)="wrapInline('bold')"
            (italic)="wrapInline('italic')"
            (heading)="insertHeading()"
            (quote)="insertQuote()"
            (separator)="insertSeparator()"
          />

          <div #editorHost class="codex-editor mt-8 w-full max-w-2xl"></div>
        </div>
      </section>
    } @else {
      <app-new-story-preview
        [categories]="categories"
        [maxTitle]="maxTitle"
        [publishing]="publishing()"
        [error]="publishError()"
        (backToEditor)="isPreviewVisible.set(false)"
        (publishNow)="publishNow()"
      />
    }
  `,
  styles: [
    `
      /* Required for @apply inside a component stylesheet with Tailwind v4. */
      @reference '../../../../styles.scss';

      :host ::ng-deep .codex-editor__redactor {
        padding-bottom: 40vh;
      }
      :host ::ng-deep .ce-block__content,
      :host ::ng-deep .cdx-block {
        max-width: none;
      }

      /* Editor.js plugins ship their own class names; align them with the theme. */
      :host ::ng-deep .ce-header {
        @apply font-bold tracking-tight;
      }
      :host ::ng-deep .ce-paragraph {
        @apply text-lg leading-relaxed;
      }
      :host ::ng-deep .cdx-quote {
        @apply border-s-4 border-border text-muted-foreground italic;
      }
      :host ::ng-deep .ce-delimiter {
        @apply text-center text-2xl text-muted-foreground;
      }
      :host ::ng-deep .cdx-list {
        @apply my-3 pl-6;
      }
      :host ::ng-deep .cdx-list__item {
        @apply my-1;
      }
      :host ::ng-deep .cdx-mark {
        @apply bg-brand/20 rounded-sm px-0.5;
      }
      :host ::ng-deep .cdx-inline-code {
        @apply bg-muted rounded px-1.5 py-0.5 font-mono text-[0.9em];
      }
      :host ::ng-deep .ce-code__code {
        @apply bg-muted text-foreground overflow-x-auto rounded-lg p-4 font-mono text-sm;
      }
      :host ::ng-deep .ce-toolbar__plus,
      :host ::ng-deep .ce-toolbar__settings-btn {
        @apply text-muted-foreground rounded;
      }
      :host ::ng-deep .ce-popover,
      :host ::ng-deep .ce-popover-item__icon {
        @apply bg-popover text-popover-foreground rounded;
      }
      :host ::ng-deep .ce-popover-item:hover,
      :host ::ng-deep .ce-popover-item--hovered {
        @apply bg-accent;
      }
      :host ::ng-deep .cdx-warning {
        @apply rounded-md border border-border bg-muted px-4 py-3;
      }
      :host ::ng-deep table.table {
        @apply w-full border-collapse text-left text-sm;
      }
      :host ::ng-deep table.table td,
      :host ::ng-deep table.table th {
        @apply border border-border px-3 py-2;
      }
      :host ::ng-deep table.table tr:first-child td {
        @apply bg-muted font-semibold;
      }
      :host ::ng-deep .embed-tool {
        @apply mx-auto w-full;
      }
    `,
  ],
})
export class NewStoryPage implements OnDestroy {
  protected readonly state = inject(EditorStateService);
  protected readonly isPreviewVisible = signal(false);
  protected readonly publishing = signal(false);
  protected readonly publishError = signal<string | null>(null);
  protected readonly maxTitle = MAX_TITLE;
  protected readonly categories = CATEGORIES;
  private readonly api = inject(EditorApiService);
  private readonly editorHost = viewChild.required<ElementRef<HTMLElement>>('editorHost');
  private editor: EditorJS | null = null;

  constructor() {
    afterNextRender(() => this.mount());
  }

  ngOnDestroy() {
    this.destroy();
  }

  protected onTitle(value: string) {
    this.state.setTitle(value.slice(0, MAX_TITLE));
  }

  /** Triggers a hidden file input, pipes the file to the mock endpoint, inserts a block. */
  protected pickImage() {
    const picker = document.createElement('input');
    picker.type = 'file';
    picker.accept = 'image/*';

    picker.onchange = async () => {
      const file = picker.files?.[0];
      if (!file) return;

      try {
        const uploaded = await this.api.uploadInlineImage(file);
        await this.editor?.blocks.insert(uploaded.block.type, uploaded.block.data, {});
      } catch {
        this.publishError.set('Could not upload that image.');
      }
    };

    picker.click();
  }

  /**
   * Editor.js exposes no command API for inline marks â€” `inlineToolbar` only opens and
   * closes. Marks are applied through the document execCommand instead, which is what the
   * underlying contenteditable actually honours.
   */
  protected wrapInline(command: 'bold' | 'italic' | 'formatBlock') {
    const value = command === 'formatBlock' ? '<h2>' : undefined;
    document.execCommand(command, false, value);
  }

  protected insertSeparator() {
    // 'delimiter' ships with Editor.js core, so no extra tool has to be registered.
    void this.editor?.blocks.insert('delimiter', {}, this.insertPosition());
  }

  protected insertHeading() {
    void this.editor?.blocks.insert('header', { text: '', level: 2 }, this.insertPosition());
  }

  protected insertQuote() {
    void this.editor?.blocks.insert(
      'quote',
      { text: '', caption: '', alignment: 'left' },
      this.insertPosition(),
    );
  }

  protected publishNow() {
    if (!this.state.isValidForPublishing()) return;

    this.publishing.set(true);
    this.publishError.set(null);

    this.api.publishStory(this.state.snapshot().id, this.state.snapshot()).then(
      () => {
        this.state.setPublicationState('PUBLISHED');
        this.publishing.set(false);
        this.publishError.set(null);
      },
      (error: unknown) => {
        this.publishing.set(false);
        this.publishError.set(error instanceof Error ? error.message : 'Could not publish.');
      },
    );
  }

  private insertPosition() {
    const at = this.editor?.blocks.getCurrentBlockIndex();
    return typeof at === 'number' ? { at } : {};
  }

  /**
   * Editor.js and every plugin touch `window` at module-evaluation time, so neither may
   * be part of the SSR bundle. Both are pulled in through dynamic imports inside this
   * browser-only lifecycle hook rather than static top-level imports.
   *
   * Registering the plugins also fixes toolbar buttons that were previously inserting
   * stubs: `delimiter`, `quote`, `header` and `image` are not part of Editor.js core,
   * so without `tools` the editor logged
   * `Tool Â«delimiterÂ» is not found. Check 'tools' property` and inserted an empty block.
   */
  private async mount() {
    const el = this.editorHost().nativeElement;
    if (!el) return;

    const [{ default: EditorJsCtor }, tools] = await Promise.all([
      import('@editorjs/editorjs'),
      loadEditorTools(),
    ]);

    this.editor = new EditorJsCtor({
      holder: el,

      // Block tools: available from Editor.js's built-in plus-block menu.
      tools,

      // Editor.js's own selection toolbar is off. It pops up over the text on every
      // selection, which duplicates the custom bar in prototyping/editor/interractive-bar.png.
      // Inline marks are applied from that bar instead, via document.execCommand.
      inlineToolbar: false,

      placeholder: 'Tell your story...',
      onReady: () => this.sync(),
      onChange: () => this.sync(),
    });
  }

  private destroy() {
    if (!this.editor) return;
    this.editor.destroy();
    this.editor = null;
  }

  private sync() {
    void this.editor?.save().then((output: OutputData) => {
      this.state.updateDraftContent({
        time: output.time ?? Date.now(),
        blocks: output.blocks.map((b) => ({
          id: String(b.id ?? ''),
          type: b.type,
          data: b.data as Record<string, unknown>,
        })),
      });
    });
  }
}
