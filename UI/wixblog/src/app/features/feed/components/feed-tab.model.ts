import { PATHS } from '../../../../../context/paths';

/** One entry in the feed's route-level tab bar. */
export interface FeedTab {
  readonly label: string;
  readonly path: string;
}

/**
 * The feed mounts at "" under MainLayout (see app.routes.ts), so these are the real
 * URLs — "/" and "/following". They used to point at "/feed" and "/feed/following",
 * which resolved to nothing after the route table was rebuilt.
 *
 * `following` has no dedicated PATHS entry, so it is spelled out here.
 */
export const DEFAULT_FEED_TABS: readonly FeedTab[] = [
  { label: 'For you', path: PATHS.home },
  { label: 'Following', path: '/following' },
];