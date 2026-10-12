import { PATHS } from '@shared/paths';

/** One entry in the feed's route-level tab bar. */
export interface FeedTab {
  readonly label: string;
  readonly path: string;
}

/**
 * The feed mounts at "" under MainLayout and owns `following` itself (see FEED_ROUTES),
 * so the tab points at /following, not /me/following.
 */
export const DEFAULT_FEED_TABS: readonly FeedTab[] = [
  { label: 'For you', path: PATHS.home },
  { label: 'Following', path: PATHS.following },
];
