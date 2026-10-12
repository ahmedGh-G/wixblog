import { computed, inject, Injectable, signal } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

import {
  blockText,
  countCharacters,
  countWords,
  createEmptyDraft,
  type EditorOutput,
  hasMeaningfulContent,
  type StoryDraft,
  type StoryStatus,
} from '../data/story-draft.model';
import { EditorApiService } from './editor-api.service';

export type SaveStatus = 'idle' | 'saving' | 'saved' | 'error';

const AUTOSAVE_DEBOUNCE_MS = 800;

export const MAX_TOPICS = 5;

/** A save request paired with the revision it captured, for staleness checks. */
interface SaveRequest {
  readonly draft: StoryDraft;
  readonly revision: number;
}

/**
 * Single source of truth for the story being authored.
 *
 * Root-provided, so `NewStoryPage` and `NewStoryPreview` hold the same instance and read
 * and write one draft concurrently. Components never mutate state directly; they call the
 * named methods below.
 */
@Injectable({ providedIn: 'root' })
export class EditorStateService {
  readonly readingMinutes = computed(() => Math.max(1, Math.ceil(this.wordCount() / 200)));
  private readonly api = inject(EditorApiService);
  /**
   * The draft itself. Private by design: every read goes through a computed slice and
   * every write through a method, so autosave can rely on this being the only mutation
   * point.
   */
  private readonly draft = signal<StoryDraft>(createEmptyDraft());
  readonly title = computed(() => this.draft().title);
  readonly content = computed(() => this.draft().content);
  readonly coverImageUrl = computed(() => this.draft().coverImageUrl);
  readonly subTitle = computed(() => this.draft().subTitle);
  readonly status = computed(() => this.draft().status);
  readonly scheduledAt = computed(() => this.draft().scheduledAt);
  readonly categorySlug = computed(() => this.draft().categorySlug);
  readonly publicationSlug = computed(() => this.draft().publicationSlug);
  readonly selectedTopics = computed<readonly string[]>(() => this.draft().topics);
  readonly abstract = computed(() =>
    this.draft()
      .content.blocks.map((block) => blockText(block.data))
      .join(' ')
      .trim(),
  );
  readonly isValidForPublishing = computed(
    () => this.draft().title.trim().length > 0 && hasMeaningfulContent(this.draft().content),
  );
  readonly wordCount = computed(() => countWords(this.draft().content));
  readonly charCount = computed(() => countCharacters(this.draft().content));
  readonly canAddTopic = computed(() => this.draft().topics.length < MAX_TOPICS);
  /**
   * Monotonic bump on every edit. A save response is only allowed to write status if its
   * captured revision is still current — this is what stops a slow earlier response from
   * overwriting the state set by a newer one.
   */
  private revision = 0;
  private readonly contentChanges = new Subject<SaveRequest>();
  private readonly saveState = signal<SaveStatus>('idle');
  readonly saveStatus = this.saveState.asReadonly();
  private readonly saveError = signal<string | null>(null);
  readonly autosaveError = this.saveError.asReadonly();

  constructor() {
    this.contentChanges
      .pipe(
        debounceTime(AUTOSAVE_DEBOUNCE_MS),
        // switchMap, not mergeMap or concatMap: when newer content arrives mid-flight it
        // unsubscribes the in-flight save, so a stale response is discarded rather than
        // applied over a newer save.
        switchMap((request) => this.persist(request)),
      )
      .subscribe();
  }

  setTitle(title: string) {
    this.patch({ title });
  }

  setSubTitle(subTitle: string) {
    this.patch({ subTitle });
  }

  /** Called on every Editor.js change. This is what drives autosave. */
  updateDraftContent(content: EditorOutput) {
    this.patch({ content });
    this.contentChanges.next({ draft: this.draft(), revision: this.revision });
  }

  setCoverImage(coverImageUrl: string | null) {
    this.patch({ coverImageUrl });
  }

  /**
   * Adds a topic. Duplicates (case-insensitive) and anything past `MAX_TOPICS` are
   * rejected, so a rapid double-tap on Enter cannot push the array over the limit the
   * preview advertises.
   */
  addTopic(topic: string): boolean {
    const label = topic.trim();
    if (!label) return false;

    const existing = this.draft().topics;
    if (existing.length >= MAX_TOPICS) return false;
    if (existing.some((value) => value.toLowerCase() === label.toLowerCase())) return false;

    this.patch({ topics: [...existing, label] });
    return true;
  }

  removeTopic(topic: string): boolean {
    const next = this.draft().topics.filter((value) => value !== topic);
    if (next.length === this.draft().topics.length) return false;

    this.patch({ topics: next });
    return true;
  }

  setCategorySlug(categorySlug: string | null) {
    this.patch({ categorySlug });
  }

  setPublicationSlug(publicationSlug: string | null) {
    this.patch({ publicationSlug });
  }

  /** Applies the publish transition locally; the API call is the caller's. */
  setPublicationState(status: StoryStatus, scheduledAt: string | null = null) {
    this.patch({ status, scheduledAt });
  }

  reset() {
    this.draft.set(createEmptyDraft());
    this.saveState.set('idle');
    this.saveError.set(null);
  }

  /** A defensive copy of the whole draft, for callers that send it wholesale. */
  snapshot(): StoryDraft {
    return { ...this.draft() };
  }

  private patch(partial: Partial<StoryDraft>) {
    this.revision += 1;
    this.draft.update((current) => ({ ...current, ...partial }));
  }

  private persist(request: SaveRequest) {
    this.saveState.set('saving');
    const isCurrent = () => request.revision === this.revision;

    return this.api.saveDraft(request.draft).then(
      () => {
        if (!isCurrent()) return;
        this.saveState.set('saved');
        this.saveError.set(null);
      },
      (error: unknown) => {
        if (!isCurrent()) return;
        this.saveState.set('error');
        this.saveError.set(error instanceof Error ? error.message : 'Autosave failed.');
      },
    );
  }
}
