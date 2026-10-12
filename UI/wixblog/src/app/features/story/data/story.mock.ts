import { MOCK_POST_DTOS } from '@shared/ui/card/src/lib/mock-posts';

import type { StoryBlock, StoryComment, StoryDetail } from './story.model';

const img = (seed: string, w = 800, h = 450) => `https://picsum.photos/seed/${seed}/${w}/${h}`;
const avatar = (n: number) => `https://i.pravatar.cc/80?img=${n}`;

const TAGS = [
  { id: 't-ai', label: 'Artificial Intelligence', slug: 'artificial-intelligence' },
  { id: 't-ai2', label: 'AI', slug: 'ai' },
  { id: 't-tech', label: 'Technology', slug: 'technology' },
  { id: 't-tech2', label: 'Tech', slug: 'tech' },
  { id: 't-future', label: 'Future', slug: 'future' },
];

const BODY: StoryBlock[] = [
  {
    kind: 'paragraph',
    text: 'The AI industry loves to defy nature — like when they go and try to build an artificial tool — and they often get that wrong. The US government to splinter America with a bunch of ugly, noisy buildings in what were otherwise beautiful landscapes — but there is one battle the industry will always, inevitably lose: nature cannot stand exponentials.',
  },
  {
    kind: 'paragraph',
    text: 'This is a fact of life: I grew twice my size in the first year after being born and then doubled again by 6 or 7 and now I am biologically stuck at 180 cm. But I am Spanish and I know for a fact that things work differently in California. The Silicon Valley invented it — I am not talking about agents — has developed a mild exponential disorder: the number of transistors inside chips, GPU, AI benchmark scores, the size of the models, intelligence, and the amount of attention they can steal from us before we rise up in arms — all those things are expected to point northeast indefinitely.',
  },
  {
    kind: 'paragraph',
    text: 'Exponentials are, to them, like morning caffeine and weird parties — a constant.',
  },
  {
    kind: 'chart',
    src: img('global-gdp'),
    caption: 'Global GDP over the long run',
    source: 'Our World in Data',
  },
  {
    kind: 'paragraph',
    text: 'The scale of artificial-intelligence neural networks is growing exponentially, as measured by the medians parameters (roughly, the number of connections between their neurons).',
  },
  {
    kind: 'chart',
    src: img('bigger-models'),
    caption: 'The drive to bigger AI models',
    source: 'Generated from open datasets',
  },
];

const PAYWALL = {
  eyebrow: 'Exclusive Offer',
  headline: 'Access to everything. Now 30% off. Unlock every single story.',
  benefits: [
    'Read member-only stories',
    'Support writers you read most',
    'Listen to audio narrations',
    'Read offline with the Medium app',
    'Access our Mastodon community',
    'Comment on your custom domain',
    'Create your own publications',
  ],
  ctaLabel: 'Upgrade now',
};

type Bio = {
  headline: string;
  bio: string;
  followers: number;
  following: number;
  link?: { label: string; url: string };
};

const AUTHOR_BIO: Record<string, Bio> = {
  'Jane Doe': {
    headline: 'AI & Tech | Weekly AI Newsletter',
    bio: 'Writing about AI, systems and the tools that quietly change how we build.',
    followers: 59000,
    following: 134,
    link: {
      label: 'thealgorithmicbridge.substack.com',
      url: 'https://thealgorithmicbridge.substack.com',
    },
  },
  'Omar Ben Ali': {
    headline: 'Staff engineer. Angular, signals, distributed systems.',
    bio: 'I write about the parts of software that only show up once you have to maintain them.',
    followers: 12400,
    following: 302,
  },
  'Sofia Martin': {
    headline: 'Product design, mostly interfaces.',
    bio: 'Design notes on making software feel quiet.',
    followers: 8300,
    following: 415,
  },
};

const DEFAULT_BIO: Bio = {
  headline: 'Writer at WixBlog',
  bio: 'Writing about software, systems and the craft around them.',
  followers: 1200,
  following: 86,
};

const COMMENTS: StoryComment[] = [
  {
    id: 'c-1',
    author: { name: 'Felix Weth', avatarUrl: avatar(14) },
    createdAt: '2026-10-08T09:12:00Z',
    edited: false,
    quote: 'OpenAI is winning.',
    body: 'This is a very fragile claim. The numbers you present can at least partly be explained by OpenAI catching up with coding, taking over market shares from Anthropic. Once that is through, their revenue might stagnate as well.',
    claps: 112,
    replies: 3,
  },
  {
    id: 'c-2',
    author: { name: 'Gianni', avatarUrl: avatar(52) },
    createdAt: '2026-10-07T18:40:00Z',
    edited: false,
    body: 'Excellent analysis that provides a counterpoint to both Boomer and Doomer arguments. Finally a voice of sanity in this incredibly noisy AI hyperbole. Well done!',
    claps: 27,
    replies: 0,
  },
  {
    id: 'c-3',
    author: { name: 'Sierra Sunset', avatarUrl: avatar(31) },
    createdAt: '2026-10-08T11:05:00Z',
    edited: true,
    quote: 'the US government',
    body: 'No. BILLIONAIRES. The US government just dutifully follows along like the lapdog of the Epstein Class that it is.',
    claps: 139,
    replies: 0,
  },
];

const FEATURED_SLUG = 'tokens-not-hex';

export function buildStoryDetail(slug: string): StoryDetail | null {
  const dto = MOCK_POST_DTOS.find((p) => p.slug === slug);
  if (!dto) return null;

  const bio = AUTHOR_BIO[dto.authorName] ?? DEFAULT_BIO;
  const isFeatured = dto.slug === FEATURED_SLUG;

  const others = MOCK_POST_DTOS.filter((p) => p.slug !== slug);
  const sameAuthor = others.filter((p) => p.authorId === dto.authorId);
  const moreFromAuthor = [...sameAuthor, ...others.filter((p) => p.authorId !== dto.authorId)]
    .slice(0, 4)
    .map((p) => p.slug);
  const similarStories = others
    .filter((p) => !moreFromAuthor.includes(p.slug))
    .slice(0, 4)
    .map((p) => p.slug);

  return {
    id: String(dto.id),
    slug: dto.slug,
    title: dto.title,
    subtitle: isFeatured ? 'Nothing lasts' : 'A working note on how this actually plays out',
    coverUrl: dto.coverImageUrl,
    author: {
      id: String(dto.authorId),
      name: dto.authorName,
      username: dto.authorName.toLowerCase().replace(/\s+/g, '-'),
      avatarUrl: dto.authorAvatarUrl,
      followersCount: bio.followers,
      followingCount: bio.following,
      headline: bio.headline,
      bio: bio.bio,
      bioLink: bio.link,
    },
    publishedAt: dto.publishedAt,
    readingMinutes: dto.readingTimeMinutes,
    membersOnly: isFeatured,
    tags: TAGS,
    body: BODY,
    paywall: PAYWALL,
    engagement: {
      claps: 2000,
      clapped: false,
      responses: COMMENTS.length,
      saved: false,
    },
    comments: COMMENTS,
    moreFromAuthor,
    similarStories,
  };
}
