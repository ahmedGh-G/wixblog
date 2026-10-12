export interface EditorBlock {
  id: string;
  type: string;
  data: Record<string, unknown>;
}

export interface EditorOutput {
  time: number;
  blocks: EditorBlock[];
  version?: string;
}

export type StoryStatus = 'DRAFT' | 'SCHEDULED' | 'PUBLISHED';

export interface StoryDraft {
  readonly id: string;
  readonly title: string;
  readonly content: EditorOutput;
  readonly subTitle: string;
  readonly coverImageUrl: string | null;
  readonly topics: string[];
  readonly status: StoryStatus;
  readonly scheduledAt: string | null;
  readonly categorySlug: string | null;
  readonly publicationSlug: string | null;
}

export function createEmptyDraft(): StoryDraft {
  return {
    id: `story-${Date.now().toString(36)}`,
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

export function blockText(data: Record<string, unknown>): string {
  const value = data['text'] ?? data['caption'] ?? '';
  return typeof value === 'string' ? value : '';
}

export function hasMeaningfulContent(content: EditorOutput): boolean {
  return content.blocks.some((block) => {
    const value = block.data['text'] ?? block.data['file'] ?? block.data['code'];
    if (typeof value === 'string') return value.trim().length > 0;
    return value != null;
  });
}

export function countWords(content: EditorOutput): number {
  return content.blocks
    .map((block) => blockText(block.data))
    .join(' ')
    .split(/\s+/)
    .filter(Boolean).length;
}

export function countCharacters(content: EditorOutput): number {
  return content.blocks.reduce((total, block) => total + blockText(block.data).length, 0);
}
