import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';

const cover = (seed: string) => `https://picsum.photos/seed/${seed}/800/450`;
const avatar = (n: number) => `https://i.pravatar.cc/80?img=${n}`;

/** A story hit, as shown in `prototyping/explore/search/main-section.png`. */
export interface SearchResult extends PostSummary {
  readonly claps: number;
  readonly comments: number;
  readonly relativeDate: string;
}

/** `prototyping/explore/search/discover-section.png` â€” Topics matching <query>. */
export const SEARCH_TOPICS: readonly ExploreTopicMatch[] = [
  { label: 'Web Development', slug: 'web-development', selected: true },
  { label: 'Web3', slug: 'web3', selected: false },
  { label: 'Web Design', slug: 'web-design', selected: false },
  { label: 'Website', slug: 'website', selected: false },
  { label: 'Web', slug: 'web', selected: false },
  { label: 'Website Design', slug: 'website-design', selected: false },
];

export interface ExploreTopicMatch {
  readonly label: string;
  readonly slug: string;
  readonly selected: boolean;
}

export interface SearchPerson {
  readonly name: string;
  readonly handle: string;
  readonly blurb: string;
  readonly avatarUrl: string;
}

export interface SearchPublication {
  readonly name: string;
  readonly blurb: string;
  readonly avatarUrl: string;
}

/** People matching <query>. */
export const SEARCH_PEOPLE: readonly SearchPerson[] = [
  {
    name: 'Web Designer Depot',
    handle: 'web-designer-depot',
    blurb: 'Web Designer Depot is the ultimate resource forâ€¦',
    avatarUrl: avatar(11),
  },
  {
    name: 'Web Design Ledger',
    handle: 'web-design-ledger',
    blurb: 'The Web Design Ledger is a publication written byâ€¦',
    avatarUrl: avatar(60),
  },
  {
    name: 'Web Summit',
    handle: 'web-summit',
    blurb: "We're a company that holds events across theâ€¦",
    avatarUrl: avatar(38),
  },
];

/** Publications matching <query>. */
export const SEARCH_PUBLICATIONS: readonly SearchPublication[] = [
  {
    name: "Jim's Tech / Blockchain / Web3â€¦",
    blurb: 'This publication features articles about web3,â€¦',
    avatarUrl: avatar(13),
  },
  {
    name: 'Reinventing Website Creation',
    blurb: 'A journal exploring how websites are conceived,â€¦',
    avatarUrl: avatar(22),
  },
  {
    name: 'Web2 with Clarity',
    blurb: 'Clear explanations of the logic behind frameworksâ€¦',
    avatarUrl: avatar(47),
  },
];

/** Footer links, matching the topic page's. */
export const SEARCH_FOOTER_LINKS = [
  { label: 'Help', path: '/' },
  { label: 'Status', path: '/' },
  { label: 'About', path: '/about' },
  { label: 'Careers', path: '/' },
  { label: 'Press', path: '/' },
  { label: 'Blog', path: '/' },
  { label: 'Store', path: '/' },
  { label: 'Privacy', path: '/legal/privacy' },
  { label: 'Rules', path: '/legal/rules' },
  { label: 'Terms', path: '/legal/terms' },
  { label: 'Text to speech', path: '/' },
];

/**
 * `prototyping/explore/search/main-section.png`, transcribed.
 * The reference shows a faded tenth row mid-load; it is omitted here.
 */
export const SEARCH_RESULTS: readonly SearchResult[] = [
  {
    id: 's1',
    slug: 'one-linux-tool-browse-web-without-chrome',
    title: 'The One Linux Tool That Lets You Browse the Web Without Opening Chrome',
    excerpt: 'A full web page, loaded and readable, without a single graphical window opening',
    coverUrl: cover('linux-browser'),
    author: { id: 'a1', name: 'Sabit', avatarUrl: null },
    category: null,
    publishedAt: '2026-10-02T00:00:00Z',
    readingMinutes: 5,
    claps: 102,
    comments: 0,
    relativeDate: 'Oct 2',
  },
  {
    id: 's2',
    slug: 'moving-mainstream-internet-to-cosy-indie-web',
    title: "Why I'm Moving From Mainstream Internet To The Cosy/ Indie web",
    excerpt: 'Reclaiming my artistic sovereignty, sanity, and sense of peace online',
    coverUrl: cover('indie-web'),
    author: { id: 'a2', name: 'Words By Egypt', avatarUrl: null },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 4,
    claps: 531,
    comments: 15,
    relativeDate: '4d ago',
  },
  {
    id: 's3',
    slug: 'jpeg-older-than-modern-web',
    title: "JPEG Is Older Than the Modern Web. Here's Why Nothing Has Killed It.",
    excerpt:
      'A 30-year-old file format still powers every selfie, meme, and product photo you see online â€” and the reasons why is moreâ€¦',
    coverUrl: cover('jpeg'),
    author: { id: 'a3', name: 'Bharat Chandra', avatarUrl: null },
    category: null,
    publishedAt: '2026-10-02T00:00:00Z',
    readingMinutes: 8,
    claps: 316,
    comments: 7,
    relativeDate: 'Oct 2',
  },
  {
    id: 's4',
    slug: 'all-my-websites-one-cloudflare-bill',
    title: 'I Run All My Websites on One $5 Cloudflare Bill',
    excerpt:
      'Every website I run sits on the same Cloudflare stack. Not one Cloudflare account per website. Not one database bill per project.',
    coverUrl: cover('cloudflare'),
    author: { id: 'a4', name: 'Civil Learning', avatarUrl: null },
    category: null,
    publishedAt: '2026-10-02T00:00:00Z',
    readingMinutes: 6,
    claps: 45,
    comments: 0,
    relativeDate: 'Oct 2',
  },
  {
    id: 's5',
    slug: 'go-web-frameworks-2026',
    title: 'Go Web Frameworks in 2026: net/http, chi, Gin, Echo, Fiber Compared',
    excerpt:
      'Go ships a production-capable HTTP server in its standard library, and since Go 1.22 the built-in router handles methodâ€¦',
    coverUrl: cover('go-frameworks'),
    author: { id: 'a5', name: 'Rost Glukhov', avatarUrl: null },
    category: null,
    publishedAt: '2026-10-05T00:00:00Z',
    readingMinutes: 9,
    claps: 10,
    comments: 0,
    relativeDate: '5d ago',
  },
  {
    id: 's6',
    slug: 'proving-rust-web-correctness-with-lean-4',
    title: 'Proving Rust Web Application Correctness with Lean 4',
    excerpt:
      'AI coding agents such as Claude Code and Codex are making it possible to build surprisingly large web applications in a veryâ€¦',
    coverUrl: cover('rust-lean'),
    author: { id: 'a6', name: 'Hidesaki Takahashi', avatarUrl: avatar(65) },
    category: null,
    publishedAt: '2026-10-07T00:00:00Z',
    readingMinutes: 11,
    claps: 51,
    comments: 1,
    relativeDate: '3d ago',
  },
  {
    id: 's7',
    slug: '31-percent-web-written-by-ai',
    title: '31% of the Web Is Now Written by AI. Your Company Wiki Is Next.',
    excerpt:
      'A study of 800 models found that the damage hides inside averages. The same thing is happening in your RAG pipeline, anâ€¦',
    coverUrl: cover('ai-web'),
    author: { id: 'a7', name: 'Claudio Lupi', avatarUrl: avatar(19) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 12,
    claps: 123,
    comments: 2,
    relativeDate: '4d ago',
  },
  {
    id: 's8',
    slug: 'strabfeld-enigma-louise-kerton',
    title: 'The StraÃŸfeld Enigma: The Vanishing of Louise Kerton and the Web of Silence',
    excerpt:
      'A summer holiday in Germany, a grieving friend, and a family with a dark history collide in one of Europeâ€™s most baffling cold cases.',
    coverUrl: cover('strabfeld'),
    author: { id: 'a8', name: 'Alex Sterling', avatarUrl: avatar(41) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 10,
    claps: 100,
    comments: 0,
    relativeDate: '4d ago',
  },
  {
    id: 's9',
    slug: 'learn-web-development-again-2026',
    title: 'If I Had to Learn Web Development Again in 2026, Iâ€™d Do This',
    excerpt:
      "If I were starting web development from zero today, I wouldn't try to learn everything. I'd focus on the skills that actually help meâ€¦",
    coverUrl: cover('learn-web'),
    author: { id: 'a9', name: 'Tech with Tabish', avatarUrl: avatar(33) },
    category: null,
    publishedAt: '2026-10-04T00:00:00Z',
    readingMinutes: 7,
    claps: 27,
    comments: 0,
    relativeDate: '6d ago',
  },
];
