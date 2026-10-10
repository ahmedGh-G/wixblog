/** A topic as rendered in the horizontal explore bar and the recommended row. */
export interface ExploreTopic {
  readonly label: string;
  readonly slug: string;
}

/**
 * The pill bar, left to right. `isActive` marks the current section — "Explore topics"
 * is the page itself, so it is the only active entry here.
 */
export interface ExploreTopicBarItem extends ExploreTopic {
  readonly isActive?: boolean;
}

/**
 * Horizontal pill bar (topics-bar.png). "Explore topics" leads as the active pill,
 * then the browseable topics.
 */
export const EXPLORE_TOPIC_BAR_ITEMS: readonly ExploreTopicBarItem[] = [
  { label: 'Explore topics', slug: '', isActive: true },
  { label: 'Media', slug: 'media' },
  { label: 'Software Development', slug: 'software-development' },
  { label: 'Life', slug: 'life' },
  { label: 'Culture', slug: 'culture' },
  { label: 'Technology', slug: 'technology' },
  { label: 'Society', slug: 'society' },
  { label: 'World', slug: 'world' },
  { label: 'Self Improvement', slug: 'self-improvement' },
  { label: 'Design', slug: 'design' },
  { label: 'Writing', slug: 'writing' },
  { label: 'Business', slug: 'business' },
  { label: 'Politics', slug: 'politics' },
  { label: 'Health', slug: 'health' },
];

/** The "Recommended:" row beneath the search field (exploer-topics-section.png). */
export const RECOMMENDED_TOPICS: readonly ExploreTopic[] = [
  { label: 'Programming', slug: 'programming' },
  { label: 'Self Improvement', slug: 'self-improvement' },
  { label: 'Data Science', slug: 'data-science' },
];

/** Placeholder for the search field. */
export const EXPLORE_TOPICS_SEARCH_PLACEHOLDER = 'Search all topics';
