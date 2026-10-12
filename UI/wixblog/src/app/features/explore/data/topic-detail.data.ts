import type { PostSummary } from '@shared/ui/card/src/lib/post-summary.model';

const cover = (seed: string) => `https://picsum.photos/seed/${seed}/800/450`;
const avatar = (n: number) => `https://i.pravatar.cc/80?img=${n}`;

/**
 * A story on the topic page. Wraps PostSummary — the shape <app-post-card> consumes —
 * and adds the engagement counts and AI summary the reference shows alongside it.
 */
export interface TopicStory extends PostSummary {
  readonly claps: number;
  readonly comments: number;
  readonly reposts: number;
  readonly publication: string | null;
  readonly relativeDate: string;
  /** Only present in the "Explore the Topic" section. */
  readonly aiSummary?: string;
}

export interface TopicSubtopic {
  readonly label: string;
  readonly storyCount: number;
  readonly description: string;
  readonly stories: readonly TopicStory[];
  /** The reference renders grey skeleton blocks for these until content loads. */
  readonly pending?: boolean;
}

export interface TopicHeaderData {
  readonly title: string;
  readonly coverUrl: string;
  readonly followers: string;
  readonly stories: string;
  readonly writers: string;
}

export const TOPIC_HEADER: TopicHeaderData = {
  title: 'Self Improvement',
  coverUrl: cover('self-improvement-cover'),
  followers: '13.4M',
  stories: '921K',
  writers: '238K',
};

/**
 * `selected(featured)-topic-stories.png`.
 * Left column first, then the right column, top to bottom as they appear.
 */
export const SELECTED_STORIES: readonly TopicStory[] = [
  {
    id: 't1',
    slug: 'shoot-your-way-out-of-the-slump',
    title: 'Shoot Your Way Out of the Slump',
    excerpt: 'Assets, liabilities, and getting out of the shame spiral.',
    coverUrl: cover('slump'),
    author: { id: 'a1', name: 'John Gorman', avatarUrl: avatar(11) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 6,
    claps: 476,
    comments: 12,
    reposts: 4,
    publication: null,
    relativeDate: '1d ago',
  },
  {
    id: 't2',
    slug: 'two-dangerous-assumptions-optimization-culture',
    title: 'The Two Dangerous Assumptions at the Heart of Optimization Culture',
    excerpt: 'We know less than we think, and more is not always better.',
    coverUrl: cover('optimization'),
    author: { id: 'a2', name: 'Markham Heid', avatarUrl: avatar(52) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 8,
    claps: 264,
    comments: 3,
    reposts: 0,
    publication: 'In The Escape',
    relativeDate: '2d ago',
  },
  {
    id: 't3',
    slug: 'how-to-get-unstuck',
    title: 'How to Get Unstuck',
    excerpt: "It's time to escape the ghost of your past failures",
    coverUrl: cover('unstuck'),
    author: { id: 'a3', name: 'Jason McBride', avatarUrl: avatar(14) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 5,
    claps: 939,
    comments: 18,
    reposts: 12,
    publication: 'In Zenite',
    relativeDate: '2d ago',
  },
  {
    id: 't4',
    slug: 'how-apps-know-us-better',
    title: 'How apps know us better than we do and influence our decisions',
    excerpt:
      'Explaining the Homo Metrics: someone who understands themselves based on scores in apps',
    coverUrl: cover('homo-metrics'),
    author: { id: 'a4', name: 'Bas Wallet', avatarUrl: avatar(32) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 9,
    claps: 500,
    comments: 5,
    reposts: 3,
    publication: 'In UX Collective',
    relativeDate: '3d ago',
  },
  {
    id: 't5',
    slug: 'ai-skill-atrophy',
    title: 'AI skill atrophy: Are we forgetting how to think?',
    excerpt: 'The hidden cost of outsourcing our thinking to AI.',
    coverUrl: cover('ai-atrophy'),
    author: { id: 'a5', name: 'Slava Polonski, PhD', avatarUrl: avatar(68) },
    category: null,
    publishedAt: '2026-10-06T00:00:00Z',
    readingMinutes: 7,
    claps: 809,
    comments: 22,
    reposts: 6,
    publication: 'In UX Collective',
    relativeDate: '4d ago',
  },
];

export const POPULAR_STORIES: readonly TopicStory[] = [
  {
    id: 'p1',
    slug: 'one-habit-ran-your-life-einstein',
    title: 'The One Habit That Can Ruin Your Life, According to Einstein',
    excerpt: 'And what to do right now.',
    coverUrl: cover('einstein-habit'),
    author: { id: 'p1', name: 'Thomas Oppong', avatarUrl: avatar(15) },
    category: null,
    publishedAt: '2026-10-04T00:00:00Z',
    readingMinutes: 4,
    claps: 1800,
    comments: 42,
    reposts: 9,
    publication: null,
    relativeDate: '6d ago',
  },
  {
    id: 'p2',
    slug: 'want-unshakable-confidence',
    title: 'Want Unshakable Confidence? Stanford Research Says You Need to Master Only 1 Skill',
    excerpt:
      "Most people try to build confidence backward. According to 50 years of research, the secret isn't perfection; it's comfort with...",
    coverUrl: cover('confidence'),
    author: { id: 'p2', name: 'Jessica Stillman', avatarUrl: avatar(44) },
    category: null,
    publishedAt: '2026-09-21T00:00:00Z',
    readingMinutes: 11,
    claps: 2500,
    comments: 50,
    reposts: 6,
    publication: null,
    relativeDate: 'Sep 18',
  },
  {
    id: 'p3',
    slug: 'how-to-work-hard-rest-of-life',
    title: 'How to Work Hard for the Rest of Your Life',
    excerpt: "All it takes is avoiding burn-out. Here's how.",
    coverUrl: cover('burnout'),
    author: { id: 'p3', name: 'Darius Foroux', avatarUrl: avatar(57) },
    category: null,
    publishedAt: '2026-09-21T00:00:00Z',
    readingMinutes: 6,
    claps: 3100,
    comments: 74,
    reposts: 35,
    publication: null,
    relativeDate: 'Sep 22',
  },
  {
    id: 'p4',
    slug: 'detectives-7-rules-hard-conversations',
    title: "A Detective's 7 Rules For Hard Conversations",
    excerpt: 'They work in the kitchen just as well as the interview room',
    coverUrl: cover('detective'),
    author: { id: 'p4', name: 'Joshua Mason', avatarUrl: avatar(26) },
    category: null,
    publishedAt: '2026-09-30T00:00:00Z',
    readingMinutes: 7,
    claps: 3000,
    comments: 44,
    reposts: 10,
    publication: 'In E²—Entertain Enlighten Empower by',
    relativeDate: 'Sep 30',
  },
  {
    id: 'p5',
    slug: 'feynmans-thinking-habit',
    title: "This is Feynman's Thinking Habit. It Made Him a Genius",
    excerpt: 'Think 10x clearer with this simple mental trick.',
    coverUrl: cover('feynman'),
    author: { id: 'p5', name: 'Thomas Oppong', avatarUrl: avatar(15) },
    category: null,
    publishedAt: '2026-06-13T00:00:00Z',
    readingMinutes: 8,
    claps: 16700,
    comments: 369,
    reposts: 263,
    publication: null,
    relativeDate: 'Jun 13',
  },
];

/**
 * The stories behind each subtopic, transcribed from `topic-exploer-section.png`.
 * Declared before TOPIC_SUBTOPICS, which composes them.
 */
const CREATIVE_PRACTICE_STORIES: readonly TopicStory[] = [
  {
    id: 'c1',
    slug: 'treat-your-journal-badly',
    title: 'Treat Your Journal Badly!',
    excerpt: "It'll make you a much better journaler",
    coverUrl: cover('journal-badly'),
    author: { id: 'c1', name: 'Gary Chapin', avatarUrl: avatar(9) },
    category: null,
    publishedAt: '2026-04-15T00:00:00Z',
    readingMinutes: 3,
    claps: 2100,
    comments: 37,
    reposts: 2,
    publication: 'In Contemplate by',
    relativeDate: 'Apr 15, 2024',
    aiSummary:
      'Keeping a journal consistently requires carrying it everywhere, treating it as a disposable tool rather than a precious object, and writing down thoughts immediately without worrying about neatness or systems.',
  },
  {
    id: 'c2',
    slug: 'dont-think-of-it-as-journaling',
    title: "Don't Think of It as Journaling, Think of It as 'Getting It Down'",
    excerpt: "Why there's never been a more important time to record your life",
    coverUrl: cover('getting-it-down'),
    author: { id: 'c2', name: 'Amy Shearn', avatarUrl: avatar(48) },
    category: null,
    publishedAt: '2020-12-04T00:00:00Z',
    readingMinutes: 8,
    claps: 694,
    comments: 4,
    reposts: 0,
    publication: 'In Forge by',
    relativeDate: 'Dec 4, 2020',
    aiSummary:
      'Reframing journaling as simply getting messy thoughts onto paper rather than a formal obligation, the writer argues, reduces anxiety, clarifies values, and captures details of extraordinary times worth remembering.',
  },
  {
    id: 'c3',
    slug: 'unfashionable-power-of-audio-journaling',
    title: 'How to Harness the Untapped Power of Audio Journaling',
    excerpt:
      'A step-by-step guide on how to make the most out of this underrated self-reflection tool',
    coverUrl: cover('audio-journaling'),
    author: { id: 'c3', name: 'David Wang', avatarUrl: avatar(13) },
    category: null,
    publishedAt: '2023-10-22T00:00:00Z',
    readingMinutes: 6,
    claps: 1100,
    comments: 33,
    reposts: 1,
    publication: 'In Better Humans by',
    relativeDate: 'Oct 22, 2023',
    aiSummary:
      'Speaking thoughts aloud clears mental clutter more effectively than writing, the writer argues, and listening back to recordings offers unique self-discovery.',
  },
  {
    id: 'c4',
    slug: 'journaling-improves-mental-physical-health',
    title: 'Journaling Improves Your Mental and Physical Health',
    excerpt:
      'Pouring your heart onto the page unshakes a host of benefits—from shaking off stress to boosting your immune system',
    coverUrl: cover('journaling-health'),
    author: { id: 'c4', name: 'Kathleen Murphy', avatarUrl: avatar(29) },
    category: null,
    publishedAt: '2024-10-25T00:00:00Z',
    readingMinutes: 9,
    claps: 2600,
    comments: 70,
    reposts: 3,
    publication: 'In Wise & Well by',
    relativeDate: 'Oct 25, 2024',
    aiSummary:
      'Expressive writing about difficult experiences reduces stress and anxiety while strengthening immunity; research shows, with benefits emerging even after just four days of journaling.',
  },
  {
    id: 'c5',
    slug: 'journaling-template-no-time',
    title: "This Is the Journaling Template for People Who Don't Have Time to Journal",
    excerpt: '',
    coverUrl: cover('journaling-template'),
    author: { id: 'c5', name: 'Rie Tagolino', avatarUrl: avatar(36) },
    category: null,
    publishedAt: '2021-06-03T00:00:00Z',
    readingMinutes: 7,
    claps: 5800,
    comments: 38,
    reposts: 5,
    publication: 'In Forge by',
    relativeDate: 'Jun 3, 2021',
    aiSummary:
      'Four Corners Journaling divides a single page into gratitude, future goals, and daily wins and losses, making reflection fit busy schedules.',
  },
];

const WRITING_CRAFT_STORIES: readonly TopicStory[] = [
  {
    id: 'w1',
    slug: 'why-most-writing-advice-is-useless',
    title: 'Why Most Writing Advice Is Useless (and What to Do Instead)',
    excerpt: 'Three common rules meant to be broken.',
    coverUrl: cover('writing-advice'),
    author: { id: 'w1', name: 'Felicia C. Sullivan', avatarUrl: avatar(45) },
    category: null,
    publishedAt: '2025-09-08T00:00:00Z',
    readingMinutes: 7,
    claps: 2100,
    comments: 67,
    reposts: 4,
    publication: 'In Feeling Crafty by',
    relativeDate: 'Sep 8, 2025',
    aiSummary:
      'Generic writing rules like "write daily" and "show, don\'t tell" work only when adapted to individual circumstances and writing goals, the author argues.',
  },
  {
    id: 'w2',
    slug: 'writing-advice-thats-stuck',
    title: "Writing Advice That's Stuck",
    excerpt: 'My favourite tips that helped me become a better writer and produce consistently.',
    coverUrl: cover('writing-stuck'),
    author: { id: 'w2', name: 'Emily Rudov', avatarUrl: avatar(25) },
    category: null,
    publishedAt: '2022-11-23T00:00:00Z',
    readingMinutes: 6,
    claps: 931,
    comments: 17,
    reposts: 1,
    publication: 'In Better Humans by',
    relativeDate: 'Nov 23, 2022',
    aiSummary:
      'Consistent daily writing practice matters more than producing perfect text, and writers must find their own process rather than follow universal rules.',
  },
  {
    id: 'w3',
    slug: 'why-do-you-write',
    title: 'Why Do You Write?',
    excerpt: "Tell me it's not about pleasing algorithms and making millions.",
    coverUrl: cover('why-do-you-write'),
    author: { id: 'w3', name: 'Felicia C. Sullivan', avatarUrl: avatar(45) },
    category: null,
    publishedAt: '2024-11-23T00:00:00Z',
    readingMinutes: 5,
    claps: 4800,
    comments: 105,
    reposts: 8,
    publication: 'In Feeling Crafty by',
    relativeDate: 'Nov 23, 2024',
    aiSummary:
      'Writing offers the writer a way to process pain and connect authentically with readers, rather than chase algorithms, money, or fame.',
  },
  {
    id: 'w4',
    slug: 'bring-writing-life-back-from-dead',
    title: 'How To Bring Your Writing Life Back from the Dead',
    excerpt: 'Lessons from my slow return to the page after a long layoff.',
    coverUrl: cover('writing-life-back'),
    author: { id: 'w4', name: 'Jason Schwartzman', avatarUrl: avatar(60) },
    category: null,
    publishedAt: '2021-06-16T00:00:00Z',
    readingMinutes: 8,
    claps: 633,
    comments: 4,
    reposts: 2,
    publication: 'In The Medium Handbook by',
    relativeDate: 'Jun 16, 2021',
    aiSummary:
      'After years away from writing, the writer returned through reading, tiny writing habits, salvaging old work, joining a writers group, and pursuing modest publication goals.',
  },
  {
    id: 'w5',
    slug: 'writing-doesnt-need-to-make-viral',
    title: "Your Writing Doesn't Need to Make $$$ or Go Viral to Be Beautiful",
    excerpt: "Even though the world demands big, it's okay to play small",
    coverUrl: cover('writing-beautiful'),
    author: { id: 'w5', name: 'Felicia C. Sullivan', avatarUrl: avatar(45) },
    category: null,
    publishedAt: '2023-01-07T00:00:00Z',
    readingMinutes: 6,
    claps: 1800,
    comments: 21,
    reposts: 3,
    publication: 'In Feeling Crafty by',
    relativeDate: 'Jan 7, 2023',
    aiSummary:
      'Writing need not be commercially successful or widely shared to have artistic merit; small, intimate work can be as beautiful as ambitious projects.',
  },
];

/**
 * `topic-exploer-section.png`.
 * The first two subtopics render their stories; the last two are still showing the
 * loading skeleton in the reference, which `pending` reproduces.
 */
export const TOPIC_SUBTOPICS: readonly TopicSubtopic[] = [
  {
    label: 'Creative Practice',
    storyCount: 5,
    description:
      'Methods for starting and maintaining journaling routines for reflection and creativity.',
    stories: CREATIVE_PRACTICE_STORIES,
  },
  {
    label: 'Writing Craft',
    storyCount: 5,
    description: 'Advice on becoming a writer, improving craft, and navigating the creative life.',
    stories: WRITING_CRAFT_STORIES,
  },
  {
    label: 'Goals and Achievement',
    storyCount: 5,
    description: 'Frameworks and strategies for defining goals and following through on them.',
    stories: [],
    pending: true,
  },
  {
    label: 'Health and Fitness',
    storyCount: 5,
    description:
      'Evidence-based approaches to physical routines, nutrition, and managing body and mind.',
    stories: [],
    pending: true,
  },
];
