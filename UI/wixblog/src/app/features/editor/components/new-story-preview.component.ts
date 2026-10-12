import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideX } from '@ng-icons/lucide';
import { HlmBadgeImports } from '@spartan-ng/helm/badge';

import { EditorStateService, MAX_TOPICS } from '../services/editor-state.service';

export interface PreviewCategory {
  readonly slug: string;
  readonly name: string;
}

/**
 * `prototyping/editor/newstory-preview.png`
 *
 * Reads and writes the draft through `EditorStateService` rather than holding its own
 * copies, so anything changed here is immediately visible to the editor canvas and is
 * covered by the same autosave stream. `NewStoryPage` injects the same root-provided
 * instance, so both views stay in step without prop drilling.
 *
 * `backToEditor` and `publishNow` remain outputs: they are navigation and command
 * intents for the page, not draft state.
 */
@Component({
  selector: 'app-new-story-preview',
  imports: [FormsModule, NgIcon, HlmBadgeImports],
  providers: [provideIcons({ lucideX })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="bg-background flex min-h-dvh flex-col">
      <div class="flex justify-end px-8 pt-6">
        <button
          type="button"
          class="text-muted-foreground hover:text-foreground rounded-full p-2 transition-colors"
          aria-label="Close preview"
          (click)="backToEditor.emit()"
        >
          <ng-icon name="lucideX" class="size-6" />
        </button>
      </div>

      <div class="mx-auto grid w-full max-w-5xl flex-1 grid-cols-1 gap-16 px-8 py-6 md:grid-cols-2">
        <div>
          <h2 class="text-sm font-bold tracking-tight">Story preview</h2>

          @if (state.coverImageUrl(); as cover) {
            <img [src]="cover" alt="" class="mt-4 aspect-video w-full rounded-sm object-cover" />
          } @else {
            <div
              class="text-muted-foreground mt-4 flex aspect-video w-full items-center justify-center rounded-sm bg-muted p-8 text-center text-sm"
            >
              Include a high-quality image in your story to make it more inviting to readers.
            </div>
          }

          <p class="text-muted-foreground mt-3 text-xs">{{ state.readingMinutes() }} min read</p>

          <dl class="mt-8 space-y-6">
            <div>
              <dt class="text-sm font-bold underline underline-offset-4">
                {{ state.wordCount() }}
              </dt>
              <dd class="text-muted-foreground mt-1 text-xs">
                {{ state.wordCount() }}/{{ maxTitle() }}
              </dd>
            </div>
            <div>
              <dt class="text-sm font-bold underline underline-offset-4">
                {{ state.charCount() }}
              </dt>
              <dd class="text-muted-foreground mt-1 text-xs">
                {{ state.charCount() }}/{{ maxTitle() }}
              </dd>
            </div>
          </dl>

          <p class="text-muted-foreground mt-8 text-sm leading-relaxed">
            Note: Changes here will reflect how your story appears in public places like Medium's
            homepage and in subscribers' inboxes â€” not the contents of the story itself.
          </p>

          <div class="mt-8">
            <h3 class="text-lg font-bold tracking-tight">Story footer</h3>
            <p class="mt-2 text-sm">You are using the default footer.</p>
            <p class="mt-4 flex flex-wrap items-center gap-2 text-sm">
              <span
                class="bg-secondary text-secondary-foreground rounded px-1.5 py-0.5 text-[10px] font-semibold tracking-wide uppercase"
              >
                Beta
              </span>
              <span class="text-muted-foreground">
                <a class="underline underline-offset-2">Customize the footer</a> displayed at the
                end of your stories.
              </span>
            </p>
          </div>
        </div>

        <div>
          <section>
            <h3 class="text-sm font-bold tracking-tight">Topics</h3>
            <p class="text-muted-foreground mt-1.5 text-sm">
              Add up to {{ maxTopics }} topics to help readers find your story.
            </p>

            <input
              type="text"
              placeholder="Add a topic..."
              class="border-input bg-muted/50 mt-5 h-11 w-full rounded-sm border px-4 text-sm"
              [ngModel]="tagDraft()"
              (ngModelChange)="tagDraft.set($event)"
              (keydown.enter)="commitTag()"
            />

            @if (state.selectedTopics().length) {
              <div class="mt-4 flex flex-wrap gap-2">
                @for (topic of state.selectedTopics(); track topic) {
                  <a
                    hlmBadge
                    variant="secondary"
                    class="gap-1.5 px-3 py-1.5 text-sm"
                    role="button"
                    [attr.aria-label]="'Remove topic ' + topic"
                    (click)="state.removeTopic(topic)"
                  >
                    {{ topic }}
                    <ng-icon name="lucideX" class="size-3" />
                  </a>
                }
              </div>
            }

            @if (exhausted()) {
              <p class="text-muted-foreground mt-3 text-xs">Topic limit reached.</p>
            }

            <label class="mt-6 block text-sm font-bold tracking-tight" for="editor-category">
              Category
            </label>
            <select
              id="editor-category"
              class="border-input bg-muted/50 mt-2 h-11 w-full rounded-sm border px-3 text-sm"
              [ngModel]="state.categorySlug()"
              (ngModelChange)="state.setCategorySlug($event || null)"
            >
              <option value="">No category</option>
              @for (category of categories(); track category.slug) {
                <option [value]="category.slug">{{ category.name }}</option>
              }
            </select>
          </section>

          <hr class="border-border my-8" />

          <section>
            <h3 class="text-sm font-bold tracking-tight">Publication</h3>
            <p class="text-muted-foreground mt-1.5 text-sm">
              <button
                type="button"
                class="underline underline-offset-2"
                (click)="togglePublication()"
              >
                {{ state.publicationSlug() ? 'Remove publication' : 'Submit' }}
              </button>
              your story to connect with community.
            </p>

            @if (state.publicationSlug(); as pub) {
              <p class="text-muted-foreground mt-2 text-sm">{{ pub }}</p>
            }
          </section>

          <hr class="border-border my-8" />

          <p class="text-muted-foreground text-sm">
            <a class="underline underline-offset-2">Learn more</a> about what happens to your post
            when you publish.
          </p>
        </div>
      </div>

      <footer class="border-border flex items-center gap-6 border-t px-8 py-5">
        <button
          type="button"
          class="bg-primary text-primary-foreground hover:bg-primary/90 rounded-full px-6 py-2 text-sm font-semibold transition-colors disabled:opacity-50"
          [disabled]="publishing() || !state.isValidForPublishing()"
          (click)="publishNow.emit()"
        >
          {{ publishing() ? 'Publishingâ€¦' : 'Publish' }}
        </button>
        <button
          type="button"
          class="text-sm underline underline-offset-2"
          (click)="backToEditor.emit()"
        >
          Schedule for later
        </button>

        @if (error(); as message) {
          <p class="text-destructive text-sm">{{ message }}</p>
        }
      </footer>
    </section>
  `,
  styles: [],
})
export class NewStoryPreview {
  readonly categories = input<readonly PreviewCategory[]>([]);
  readonly publishing = input(false);
  readonly error = input<string | null>(null);
  readonly maxTitle = input(140);
  readonly backToEditor = output<void>();
  readonly publishNow = output<void>();
  protected readonly state = inject(EditorStateService);
  protected readonly tagDraft = signal('');
  protected readonly maxTopics = MAX_TOPICS;
  protected readonly exhausted = computed(() => !this.state.canAddTopic());

  /**
   * Delegates to the service, which rejects duplicates and anything past the limit. The
   * draft is cleared either way so a rejected entry does not linger in the input.
   */
  protected commitTag() {
    const label = this.tagDraft().trim();
    this.tagDraft.set('');
    if (label) this.state.addTopic(label);
  }

  protected togglePublication() {
    this.state.setPublicationSlug(this.state.publicationSlug() ? null : 'inkwell');
  }
}
