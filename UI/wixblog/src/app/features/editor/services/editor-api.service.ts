import { Injectable, signal } from '@angular/core';

import type { EditorBlock, EditorOutput, StoryDraft, StoryStatus } from '../data/story-draft.model';

export type { StoryStatus };

export interface CoverUploadResponse {
  coverImageUrl: string;
  fileName: string;
  bytes: number;
}

export interface InlineImageResponse {
  blockId: string;
  block: EditorBlock;
}

export interface StoryResponse {
  storyId: string;
  slug: string;
  status: StoryStatus;
  createdAt: string;
  request: Partial<StoryDraft>;
}

export type DraftValidationError = 'EMPTY_TITLE' | 'EMPTY_CONTENT' | 'TITLE_TOO_LONG';

const KNOWN_TOPICS = [
  'Web Development',
  'Productivity',
  'Mindfulness',
  'Writing',
  'Design',
  'AI',
  'Self Improvement',
  'Technology',
  'Life',
  'Media',
  'Culture',
  'Society',
] as const;

const MAX_TITLE = 140;

/**
 * Backend gateway for the editor.
 *
 * Bodies are simulated rather than issued over HttpClient so the feature stays
 * demonstrable with no server running. The signatures are the real contract: moving to
 * HttpClient later is a change to each method body, not to any caller. Note that
 * `provideHttpClient` is not registered in app.config.ts, so real transport would need
 * that added first.
 */
@Injectable({ providedIn: 'root' })
export class EditorApiService {
  readonly lastDraft = signal<StoryResponse | null>(null);
  private counter = 0;

  uploadCoverImage(file: File): Promise<CoverUploadResponse> {
    return this.simulate(() => ({
      coverImageUrl: `https://picsum.photos/seed/${encodeURIComponent(file.name)}/1600/900`,
      fileName: file.name,
      bytes: file.size,
    }));
  }

  uploadInlineImage(file: File): Promise<InlineImageResponse> {
    return this.simulate(() => {
      const blockId = this.nextId('block');
      return {
        blockId,
        block: {
          id: blockId,
          type: 'image',
          data: {
            file: { url: `https://picsum.photos/seed/${encodeURIComponent(file.name)}/1200/800` },
            caption: file.name.replace(/\.[^.]+$/, ''),
            withBorder: false,
          },
        },
      };
    });
  }

  /** PUT /stories/:id — the debounced autosave target. */
  saveDraft(draft: StoryDraft): Promise<StoryResponse> {
    return this.simulate(() => this.toResponse(draft));
  }

  /** POST /stories/:id/publish */
  publishStory(id: string, metadata: Partial<StoryDraft>): Promise<StoryResponse> {
    return this.simulate(() => this.toResponse({ ...this.blank(id), ...metadata }, 'PUBLISHED'));
  }

  /** GET /topics?query= */
  searchTopics(query: string): Promise<string[]> {
    const needle = query.trim().toLowerCase();
    return this.simulate(() =>
      needle
        ? KNOWN_TOPICS.filter((topic) => topic.toLowerCase().includes(needle))
        : [...KNOWN_TOPICS],
    );
  }

  createDraft(draft: Partial<StoryDraft>): Promise<StoryResponse> {
    const failure = this.validate(draft);
    if (failure) return Promise.reject(new Error(failure));
    return this.simulate(() =>
      this.toResponse({ ...this.blank(this.nextId('story')), ...draft }, 'DRAFT'),
    );
  }

  private readonly now = () => new Date().toISOString();

  private validate(draft: Partial<StoryDraft>): DraftValidationError | null {
    const title = (draft.title ?? '').trim();
    if (!title) return 'EMPTY_TITLE';
    if (title.length > MAX_TITLE) return 'TITLE_TOO_LONG';
    const content: EditorOutput | undefined = draft.content;
    if (!content?.blocks.length) return 'EMPTY_CONTENT';
    return null;
  }

  private blank(id: string): StoryDraft {
    return {
      id,
      title: '',
      content: { time: Date.now(), blocks: [] },
      subTitle: '',
      coverImageUrl: null,
      topics: [],
      status: 'DRAFT',
      scheduledAt: null,
      categorySlug: null,
      publicationSlug: null,
    };
  }

  private toResponse(draft: StoryDraft, status: StoryStatus = draft.status): StoryResponse {
    const response: StoryResponse = {
      storyId: draft.id,
      slug: draft.title
        .trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, ''),
      status,
      createdAt: this.now(),
      request: { ...draft, status },
    };

    this.lastDraft.set(response);
    return response;
  }

  /** Round-trips the payload so callers exercise a real async boundary. */
  private simulate<T>(produce: () => T): Promise<T> {
    return new Promise((resolve) => setTimeout(() => resolve(produce()), 320));
  }

  private nextId(prefix: string): string {
    this.counter += 1;
    return `${prefix}-${Date.now().toString(36)}-${this.counter}`;
  }
}
