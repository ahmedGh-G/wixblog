/**
 * Static, view-only data for the home feed chrome.
 *
 * Posts are NOT defined here: the feed itself is fed by the real `BlogFeedStore` /
 * `BlogApi` / `MOCK_POST_DTOS` pipeline. This file only holds the sidebar blocks and
 * the per-post presentation metadata (claps, comments, relative time, publication
 * list, thumbnail shape) that `PostSummary` does not carry but the Medium feed
 * layout requires.
 *
 * Everything here is keyed by the ids in `MOCK_POST_DTOS` so the sidebar can never
 * drift from the feed it describes.
 */
import { MOCK_POST_DTOS } from '../../src/app/shared/ui/card/src/lib/mock-posts';
import { toPostSummary } from '../../src/app/shared/ui/card/src/lib/post.mapper';
import type { PostSummary } from '../../src/app/shared/ui/card/src/lib/post-summary.model';

const avatar = (n: number) => `https://i.pravatar.cc/80?img=${n}`;

/* ------------------------------------------------------------------ *
 * Icons
 * ------------------------------------------------------------------ */

/**
 * Medium's clap mark. Not in `lucide`, so it ships as an inline SVG string: `ng-icons`
 * accepts a raw markup string wherever it accepts a named icon, so it registers and
 * renders through `<ng-icon name="feedClap" />` exactly like a packaged icon.
 */
export const FEED_CLAP_ICON = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6"><path d="M11.5 4.6a1.25 1.25 0 0 1 2.5 0V10"/><path d="M14 6a1.25 1.25 0 0 1 2.5 0v3.5"/><path d="M11.5 4.6a1.25 1.25 0 0 0-2.5 0v6.6"/><path d="M9 11.2a1.25 1.25 0 0 0-2.5 0v2.1"/><path d="M6.5 13.3a1.25 1.25 0 0 0-2.2 1.2l3.4 3.9A3.6 3.6 0 0 0 10.3 20H14a3.6 3.6 0 0 0 3.5-2.7l1.3-4.3a1.5 1.5 0 0 0-2.9-.8L14.6 14V8.6"/></svg>`;

/* ------------------------------------------------------------------ *
 * FeedPage post presentation metadata
 * ------------------------------------------------------------------ */

/** How the row thumbnail is cropped. Mirrors the mixed aspect ratios in the reference. */
export type FeedThumbShape = 'wide' | 'standard' | 'tall';

export interface FeedPostPresentation {
  /** Relative time shown after the author name. */
  readonly ago: string;
  /** Publications that carried this post, rendered as "in A, B and C". */
  readonly publications: readonly string[];
  readonly claps: number;
  readonly comments: number;
  readonly thumb: FeedThumbShape;
  readonly tagged?: string;
}

const META: Readonly<Record<string, FeedPostPresentation>> = {
  '1': {
    ago: 'Sep 07',
    publications: ['Generative AI', 'IoT Chips Monitor'],
    claps: 12,
    comments: 4,
    thumb: 'wide',
  },
  '2': {
    ago: 'Sep 04',
    publications: ['Deep Learning Weekly'],
    claps: 18,
    comments: 7,
    thumb: 'wide',
  },
  '3': {
    ago: 'Aug 27',
    publications: ['DevOps Journal'],
    claps: 31,
    comments: 12,
    thumb: 'wide',
    tagged: 'AI',
  },
  '4': {
    ago: 'Sep 27',
    publications: ['Performance Engineering'],
    claps: 1_042,
    comments: 42,
    thumb: 'wide',
  },
  '5': {
    ago: 'Sep 09',
    publications: ['Data Science Collective', 'Analytics Notes'],
    claps: 1_336,
    comments: 21,
    thumb: 'wide',
  },
  '6': {
    ago: 'Aug 30',
    publications: ['The Newsletter'],
    claps: 1_255,
    comments: 30,
    thumb: 'wide',
  },
  '7': {
    ago: 'Sep 10',
    publications: ['JavaScript Weekly', 'Frontend Digest'],
    claps: 1_502,
    comments: 34,
    thumb: 'wide',
  },
  '8': {
    ago: 'Aug 18',
    publications: ['Lean Engineering'],
    claps: 19,
    comments: 8,
    thumb: 'wide',
  },
  '9': {
    ago: 'Aug 31',
    publications: ['Developer Productivity'],
    claps: 2_531,
    comments: 141,
    thumb: 'standard',
  },
  '10': {
    ago: 'Jun 20',
    publications: ['Platform Engineering'],
    claps: 928,
    comments: 19,
    thumb: 'standard',
  },
  '11': {
    ago: 'Sep 15',
    publications: ['Product Craft'],
    claps: 1_055,
    comments: 23,
    thumb: 'standard',
  },
  '12': {
    ago: 'Sep 22',
    publications: ['Typography Review'],
    claps: 611,
    comments: 11,
    thumb: 'standard',
  },
};

const FALLBACK_META: FeedPostPresentation = {
  ago: 'recently',
  publications: [],
  claps: 3,
  comments: 0,
  thumb: 'standard',
};

/**
 * Presentation metadata for a post. Defaults rather than throwing, so a post added to
 * `MOCK_POST_DTOS` without a `META` entry still renders.
 */
export function feedMetaFor(post: PostSummary): FeedPostPresentation {
  return META[post.id] ?? FALLBACK_META;
}

/** DTOs as view models. The page uses this instead of the store for anything not in `PostSummary`. */
export const ALL_FEED_POSTS: readonly PostSummary[] = MOCK_POST_DTOS.map(toPostSummary);

/* ------------------------------------------------------------------ *
 * Sidebar: Staff Picks
 * ------------------------------------------------------------------ */

export interface StaffPick {
  readonly publication: string;
  readonly publicationAvatar: string | null;
  readonly title: string;
  readonly ago: string;
}

export const STAFF_PICKS: readonly StaffPick[] = [
  {
    publication: 'The Guardian',
    publicationAvatar: null,
    title: 'Trump makes it clear his power is becoming entwined with AI',
    ago: '6h ago',
  },
  {
    publication: 'Elizabeth Spiers',
    publicationAvatar: null,
    title: 'I Was Raised on a Fast Party, and My Experience Is a Horrifyingly Common Narrative',
    ago: '2d ago',
  },
  {
    publication: 'The Ugly Monster by Lexi Solidgold',
    publicationAvatar: null,
    title: "Rockstar Games' Development Saga Is Oddness",
    ago: 'Sep 23',
  },
];

/* ------------------------------------------------------------------ *
 * Sidebar: Trending
 * ------------------------------------------------------------------ */

export interface TrendingEntry {
  readonly publication: string;
  readonly publicationAvatar: string | null;
  readonly title: string;
  readonly ago: string;
  /** Small tinted chip under the title, e.g. a series name. */
  readonly tag?: string;
}

export const TRENDING: readonly TrendingEntry[] = [
  {
    publication: 'Abbas Meherzad',
    publicationAvatar: avatar(14),
    title: 'What does an improved upper bound on ω mean',
    ago: '22h ago',
  },
  {
    publication: 'AI, Entrepreneurship Handbook',
    publicationAvatar: null,
    title: 'In Entrepreneurship Handle... by Joe Procop...',
    ago: '2d ago',
  },
  {
    publication: 'Data "Creation" is the New Gold Rush',
    publicationAvatar: null,
    title: 'Alberto Romero',
    ago: '1d ago',
  },
  {
    publication: 'Data "Creation" is the New Gold Rush',
    publicationAvatar: null,
    title: 'Something Has Gone Terribly Wrong at Anthropic',
    ago: '1d ago',
    tag: 'AI',
  },
];

/* ------------------------------------------------------------------ *
 * Sidebar: Recommended topics
 * ------------------------------------------------------------------ */

export const TOPICS: readonly string[] = [
  'Programming',
  'Self improvement',
  'Data Science',
  'Writing',
  'Technology',
  'Relationships',
  'Politics',
];

/* ------------------------------------------------------------------ *
 * Sidebar: Who to follow
 * ------------------------------------------------------------------ */

export interface SuggestedAuthor {
  readonly name: string;
  readonly avatarUrl: string | null;
  readonly tagline: string;
  readonly following: boolean;
}

export const SUGGESTED_AUTHORS: readonly SuggestedAuthor[] = [
  {
    name: 'Michal Malewicz',
    avatarUrl: avatar(3),
    tagline: 'I share my secrets about building side businesses.',
    following: false,
  },
  {
    name: 'ILLUMINATION',
    avatarUrl: avatar(16),
    tagline: 'We curate & disseminate outstanding stories from…',
    following: true,
  },
  {
    name: 'Abhijit',
    avatarUrl: avatar(20),
    tagline: 'A middle-class Indian guy with a passion for financi…',
    following: false,
  },
];

/* ------------------------------------------------------------------ *
 * Reading list card + footer
 * ------------------------------------------------------------------ */

export const FOOTER_LINKS: readonly string[] = [
  'Help',
  'Status',
  'About',
  'Careers',
  'Press',
  'Blog',
  'Privacy',
  'Rules',
  'Terms',
  'Text to speech',
];
