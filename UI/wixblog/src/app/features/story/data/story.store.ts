import { Injectable, computed, signal } from '@angular/core';
import { MOCK_POST_DTOS } from '@shared/ui/card/src/lib/mock-posts';
import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';

import { buildStoryDetail } from './story.mock';
import type { StoryComment, StoryDetail } from './story.model';

export type StoryStatus = 'idle' | 'loading' | 'ready' | 'empty' | 'error';

const toSummary = (d: (typeof MOCK_POST_DTOS)[number]): PostSummary => ({
  id: String(d.id),
  slug: d.slug,
  title: d.title,
  excerpt: d.excerpt,
  coverUrl: d.coverImageUrl,
  author: { id: String(d.authorId), name: d.authorName, avatarUrl: d.authorAvatarUrl },
  category:
    d.categorySlug && d.categoryName ? { slug: d.categorySlug, name: d.categoryName } : null,
  publishedAt: d.publishedAt,
  readingMinutes: d.readingTimeMinutes,
});

@Injectable()
export class StoryStore {
  private readonly _story = signal<StoryDetail | null>(null);
  private readonly _status = signal<StoryStatus>('idle');
  private readonly _error = signal<string | null>(null);
  private readonly _draft = signal('');
  private readonly _localComments = signal<StoryComment[]>([]);
  private readonly _currentUser = {
    name: 'Ahmed Ghorbel',
    avatarUrl: 'https://i.pravatar.cc/80?img=15',
  };

  readonly story = this._story.asReadonly();
  readonly status = this._status.asReadonly();
  readonly error = this._error.asReadonly();
  readonly draft = this._draft.asReadonly();
  readonly currentUser = this._currentUser;

  readonly isLoading = computed(() => this._status() === 'loading');
  readonly isEmpty = computed(() => this._status() === 'empty');
  readonly isError = computed(() => this._status() === 'error');
  readonly isReady = computed(() => this._status() === 'ready');

  readonly comments = computed(() => {
    const story = this._story();
    return story ? [...story.comments, ...this._localComments()] : [];
  });

  readonly responsesCount = computed(() => {
    const story = this._story();
    if (!story) return 0;
    return story.engagement.responses + this._localComments().length;
  });

  readonly moreFromAuthor = computed<PostSummary[]>(() =>
    this.resolveSlugs(this._story()?.moreFromAuthor ?? []),
  );
  readonly similarStories = computed<PostSummary[]>(() =>
    this.resolveSlugs(this._story()?.similarStories ?? []),
  );

  private resolveSlugs(slugs: string[]): PostSummary[] {
    return slugs
      .map((s) => MOCK_POST_DTOS.find((p) => p.slug === s))
      .filter((p): p is (typeof MOCK_POST_DTOS)[number] => !!p)
      .map(toSummary);
  }

  load(slug: string): void {
    this._status.set('loading');
    this._error.set(null);
    this._localComments.set([]);
    this._draft.set('');

    const detail = buildStoryDetail(slug);

    if (!detail) {
      this._story.set(null);
      this._status.set('empty');
      return;
    }

    this._story.set(detail);
    this._status.set('ready');
  }

  toggleClap(): void {
    this._story.update((s) =>
      s
        ? {
            ...s,
            engagement: {
              ...s.engagement,
              clapped: !s.engagement.clapped,
              claps: s.engagement.clapped ? s.engagement.claps - 1 : s.engagement.claps + 1,
            },
          }
        : s,
    );
  }

  toggleSave(): void {
    this._story.update((s) =>
      s ? { ...s, engagement: { ...s.engagement, saved: !s.engagement.saved } } : s,
    );
  }

  toggleFollow(): void {
    this._following.update((f) => !f);
  }

  private readonly _following = signal(false);
  readonly following = this._following.asReadonly();

  setDraft(value: string): void {
    this._draft.set(value);
  }

  submitComment(): void {
    const body = this._draft().trim();
    if (!body) return;

    this._localComments.update((list) => [
      ...list,
      {
        id: `local-${list.length + 1}`,
        author: { name: this._currentUser.name, avatarUrl: this._currentUser.avatarUrl },
        createdAt: new Date().toISOString(),
        edited: false,
        body,
        claps: 0,
        replies: 0,
      },
    ]);

    this._draft.set('');
  }
}
