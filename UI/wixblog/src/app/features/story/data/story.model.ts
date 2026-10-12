export type StoryBlock =
  | { kind: 'paragraph'; text: string }
  | { kind: 'heading'; text: string }
  | { kind: 'image'; src: string; alt: string; caption?: string }
  | { kind: 'chart'; src: string; caption: string; source: string };

export interface StoryTag {
  id: string;
  label: string;
  slug: string;
}

export interface StoryAuthor {
  id: string;
  name: string;
  username: string;
  avatarUrl: string | null;
  followersCount: number;
  followingCount: number;
  headline: string;
  bio: string;
  bioLink?: { label: string; url: string };
}

export interface StoryEngagement {
  claps: number;
  clapped: boolean;
  responses: number;
  saved: boolean;
}

export interface StoryComment {
  id: string;
  author: { name: string; avatarUrl: string | null };
  createdAt: string;
  edited: boolean;
  quote?: string;
  body: string;
  claps: number;
  replies: number;
}

export interface StoryPaywall {
  eyebrow: string;
  headline: string;
  benefits: string[];
  ctaLabel: string;
}

export interface StoryDetail {
  id: string;
  slug: string;
  title: string;
  subtitle: string;
  coverUrl: string | null;
  author: StoryAuthor;
  publishedAt: string;
  readingMinutes: number;
  membersOnly: boolean;
  tags: StoryTag[];
  body: StoryBlock[];
  paywall?: StoryPaywall;
  engagement: StoryEngagement;
  comments: StoryComment[];
  moreFromAuthor: string[];
  similarStories: string[];
}
