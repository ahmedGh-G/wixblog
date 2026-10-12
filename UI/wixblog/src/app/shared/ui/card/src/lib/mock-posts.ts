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

  {
    id: 13,
    slug: 'the-case-for-boring-interfaces',
    title: 'The case for boring interfaces',
    excerpt:
      'Novelty is a cost, not a feature. Why the products people trust most are the ones that stopped trying to surprise them.',
    coverImageUrl: cover('boring'),
    ...SOFIA,
    ...DES,
    publishedAt: '2026-09-22T13:20:00Z',
    readingTimeMinutes: 8,
  },

  {
    id: 14,
    slug: 'restraint-is-a-feature',
    title: 'Restraint is a feature',
    excerpt:
      'Every element you remove is a decision you no longer have to defend. A short argument for shipping less.',
    coverImageUrl: cover('restraint'),
    ...SOFIA,
    ...DES,
    publishedAt: '2026-09-21T09:45:00Z',
    readingTimeMinutes: 5,
  },

  {
    id: 15,
    slug: 'the-two-hour-writes',
    title: 'The two-hour writes',
    excerpt:
      'Not every morning needs to be productive. Some are for thinking, and thinking rarely fits into a to-do list.',
    coverImageUrl: cover('twohours'),
    ...JANE,
    ...WRI,
    publishedAt: '2026-09-20T07:10:00Z',
    readingTimeMinutes: 6,
  },

  {
    id: 16,
    slug: 'notes-i-actually-keep',
    title: 'The notes I actually keep',
    excerpt:
      'I stopped keeping notes for future me. Here is what survived a year of ruthless deleting.',
    coverImageUrl: cover('notes'),
    ...JANE,
    ...PROD,
    publishedAt: '2026-09-19T08:30:00Z',
    readingTimeMinutes: 5,
  },

  {
    id: 17,
    slug: 'against-the-morning-routine',
    title: 'Against the morning routine',
    excerpt:
      'The five-step sunrise ritual is not a system. Sometimes the first hour of the day belongs to somebody else.',
    coverImageUrl: cover('morningroutine'),
    ...JANE,
    ...WRI,
    publishedAt: '2026-09-18T06:55:00Z',
    readingTimeMinutes: 7,
  },

  {
    id: 18,
    slug: 'signals-where-rxjs-is-not',
    title: 'Signals where RxJS is not (and the reverse)',
    excerpt:
      'A field guide to the overlap: which of the two you reach for when the two both technically work.',
    coverImageUrl: cover('signalswhere'),
    ...OMAR,
    ...ENG,
    publishedAt: '2026-09-17T10:15:00Z',
    readingTimeMinutes: 11,
  },

  {
    id: 19,
    slug: 'the-honest-onboarding-doc',
    title: 'The honest onboarding doc',
    excerpt:
      'Most onboarding docs are marketing. A short template for writing the one a new engineer would actually save.',
    coverImageUrl: cover('onboarding'),
    ...OMAR,
    ...ENG,
    publishedAt: '2026-09-16T09:20:00Z',
    readingTimeMinutes: 8,
  },

  {
    id: 20,
    slug: 'delete-the-abstraction',
    title: 'Delete the abstraction',
    excerpt:
      'Three layers of indirection later, nobody could name the thing it was wrapping. A note on premature design.',
    coverImageUrl: cover('abstraction'),
    ...OMAR,
    ...ENG,
    publishedAt: '2026-09-15T14:40:00Z',
    readingTimeMinutes: 9,
  },

  {
    id: 21,
    slug: 'code-review-is-a-writing-skill',
    title: 'Code review is a writing skill',
    excerpt:
      'The comment you leave is read by a person with a hard day. Clarity there is as valuable as the fix itself.',
    coverImageUrl: cover('codereview'),
    ...LIAM,
    ...ENG,
    publishedAt: '2026-09-14T11:05:00Z',
    readingTimeMinutes: 6,
  },

  {
    id: 22,
    slug: 'feature-flags-forever',
    title: 'Feature flags forever',
    excerpt:
      'We added one to ship safely and kept them for years. What that did to our codebase, and what we would undo.',
    coverImageUrl: cover('flags'),
    ...LIAM,
    ...ENG,
    publishedAt: '2026-09-13T16:30:00Z',
    readingTimeMinutes: 10,
  },

  {
    id: 23,
    slug: 'the-build-that-took-four-hours',
    title: 'The build that took four hours',
    excerpt:
      'It was not one problem. It was a hundred small permissions, each reasonable on its own.',
    coverImageUrl: cover('build'),
    ...LIAM,
    ...ENG,
    publishedAt: '2026-09-12T13:00:00Z',
    readingTimeMinutes: 7,
  },

  {
    id: 24,
    slug: 'notes-from-the-migration',
    title: 'Notes from the migration',
    excerpt:
      'Eighteen months of moving a legacy system, written down so the next person does not have to rediscover it.',
    coverImageUrl: cover('migration'),
    ...AMINA,
    ...ENG,
    publishedAt: '2026-09-11T10:50:00Z',
    readingTimeMinutes: 12,
  },

  {
    id: 25,
    slug: 'the-two-minute-rule-is-wrong',
    title: "The two-minute rule is wrong",
    excerpt:
      'Tasks that take two minutes are rarely two minutes, and the list they land on is never the list you read.',
    coverImageUrl: cover('twominute'),
    ...AMINA,
    ...PROD,
    publishedAt: '2026-09-10T09:25:00Z',
    readingTimeMinutes: 5,
  },

  {
    id: 26,
    slug: 'what-i-stopped-tracking',
    title: 'What I stopped tracking',
    excerpt:
      'Habit apps made me good at recording habits and no better at keeping them. The metrics I dropped and why.',
    coverImageUrl: cover('tracking'),
    ...AMINA,
    ...PROD,
    publishedAt: '2026-09-09T15:45:00Z',
    readingTimeMinutes: 6,
  },
];
