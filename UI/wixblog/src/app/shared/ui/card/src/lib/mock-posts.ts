import { PostSummaryDto } from './post.dto';

const cover = (seed: string) => `https://picsum.photos/seed/${seed}/800/450`;
const avatar = (n: number) => `https://i.pravatar.cc/80?img=${n}`;

const JANE = { authorId: 1, authorName: 'Jane Doe', authorAvatarUrl: null }; // no avatar: tests the initial fallback
const OMAR = { authorId: 2, authorName: 'Omar Ben Ali', authorAvatarUrl: avatar(12) };
const SOFIA = { authorId: 3, authorName: 'Sofia Martin', authorAvatarUrl: avatar(47) };
const LIAM = { authorId: 4, authorName: 'Liam Chen', authorAvatarUrl: avatar(33) };
const AMINA = { authorId: 5, authorName: 'Amina Khelifi', authorAvatarUrl: avatar(5) };

const PROD = { categorySlug: 'productivity', categoryName: 'Productivity' };
const ENG = { categorySlug: 'engineering', categoryName: 'Engineering' };
const DES = { categorySlug: 'design', categoryName: 'Design' };
const WRI = { categorySlug: 'writing', categoryName: 'Writing' };
const NONE = { categorySlug: null, categoryName: null }; // tests "no category"

export const MOCK_POST_DTOS: PostSummaryDto[] = [
  {
    id: 1,
    slug: 'why-i-write-every-morning',
    title: 'Why I write every morning before opening my laptop',
    excerpt:
      'Twenty quiet minutes with a notebook changed how I think, plan and decide. Here is the routine, the mistakes I made, and what I would tell my past self.',
    coverImageUrl: cover('morning'),
    ...JANE,
    ...WRI,
    publishedAt: '2026-10-04T07:30:00Z',
    readingTimeMinutes: 6,
  },

  {
    id: 2,
    slug: 'signals-vs-rxjs',
    title: 'Signals vs RxJS: stop treating them as rivals',
    excerpt:
      'Signals own state, RxJS owns streams. A practical guide to using each where it shines in a modern Angular codebase.',
    coverImageUrl: cover('signals'),
    ...OMAR,
    ...ENG,
    publishedAt: '2026-10-03T16:10:00Z',
    readingTimeMinutes: 9,
  },

  {
    id: 3,
    slug: 'designing-quiet-interfaces',
    title: 'Designing quiet interfaces',
    excerpt:
      'Less chrome, more content. How restraint in color, shadow and motion makes reading products feel calmer and more trustworthy.',
    coverImageUrl: cover('quiet'),
    ...SOFIA,
    ...DES,
    publishedAt: '2026-10-02T09:00:00Z',
    readingTimeMinutes: 5,
  },

  {
    id: 4,
    slug: 'feature-based-angular',
    title:
      'Feature-based architecture: how we cut onboarding time in half and stopped fighting over the shared folder every single sprint',
    excerpt:
      'A very long title on purpose, to check line clamping. Grouping code by business capability keeps teams independent, makes lazy loading obvious and turns dependency rules into something a linter can enforce for you.',
    coverImageUrl: cover('feature'),
    ...LIAM,
    ...ENG,
    publishedAt: '2026-10-01T12:45:00Z',
    readingTimeMinutes: 12,
  },

  {
    id: 5,
    slug: 'no-cover-essay',
    title: 'A post without a cover image',
    excerpt:
      'Not every story has a picture. This one checks that rows and cards still look balanced when there is no thumbnail.',
    coverImageUrl: null,
    ...AMINA,
    ...NONE,
    publishedAt: '2026-09-30T18:20:00Z',
    readingTimeMinutes: 3,
  },

  {
    id: 6,
    slug: 'deep-work-in-open-offices',
    title: 'Deep work in an open office (yes, it is possible)',
    excerpt:
      'Headphones are not a strategy. Calendar blocks, a visible status signal and one hard rule did more than any gadget.',
    coverImageUrl: cover('deepwork'),
    ...JANE,
    ...PROD,
    publishedAt: '2026-09-29T08:05:00Z',
    readingTimeMinutes: 7,
  },

  {
    id: 7,
    slug: 'bookmark-fail-demo',
    title: 'This post fails to bookmark (rollback demo)',
    excerpt:
      'The mock API rejects the bookmark request for this post, so you can watch the optimistic update roll back and the toast fire.',
    coverImageUrl: cover('rollback'),
    ...OMAR,
    ...ENG,
    publishedAt: '2026-09-28T14:00:00Z',
    readingTimeMinutes: 4,
  },

  {
    id: 8,
    slug: 'tokens-not-hex',
    title: 'Design tokens, not hex codes',
    excerpt:
      'How one file of variables lets you rebrand an entire product without touching a single component.',
    coverImageUrl: cover('tokens'),
    ...SOFIA,
    ...DES,
    publishedAt: '2026-09-27T10:30:00Z',
    readingTimeMinutes: 6,
  },

  {
    id: 9,
    slug: 'reading-list-autumn',
    title: 'Five books that shaped how I build software',
    excerpt:
      'None of them are about code. All of them changed how I design systems and work with people.',
    coverImageUrl: cover('books'),
    ...LIAM,
    ...WRI,
    publishedAt: '2026-09-26T19:15:00Z',
    readingTimeMinutes: 8,
  },

  {
    id: 10,
    slug: 'spring-boot-angular-contracts',
    title: 'Keeping Spring Boot and Angular honest with OpenAPI',
    excerpt:
      'Generate types from the contract, map DTOs at the edge, and let the compiler catch the breaking change before your users do.',
    coverImageUrl: cover('contracts'),
    ...AMINA,
    ...ENG,
    publishedAt: '2026-09-25T11:00:00Z',
    readingTimeMinutes: 10,
  },

  {
    id: 11,
    slug: 'small-habits-big-focus',
    title: 'Small habits, big focus',
    excerpt:
      'Three tiny changes to my day that made the biggest difference to how much I actually finish.',
    coverImageUrl: cover('habits'),
    ...JANE,
    ...PROD,
    publishedAt: '2026-09-24T06:50:00Z',
    readingTimeMinutes: 4,
  },

  {
    id: 12,
    slug: 'typography-for-long-reads',
    title: 'Typography for long reads',
    excerpt:
      'Line length, leading and a good serif: the boring details that decide whether people finish your article.',
    coverImageUrl: cover('type'),
    ...SOFIA,
    ...DES,
    publishedAt: '2026-09-23T15:40:00Z',
    readingTimeMinutes: 7,
  },
];
