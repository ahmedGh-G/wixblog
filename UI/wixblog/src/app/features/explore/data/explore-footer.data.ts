import { PATHS } from '@shared/paths';

/**
 * Footer link list at the bottom of /explore-topics
 * (`prototyping/explore/explore-topics-fullpage.png`).
 *
 * Paths match app.routes.ts. The Medium-internal destinations (Help, Status, Careers, …)
 * have no counterpart in this app yet and point at "/" until they do.
 */
export interface ExploreFooterLink {
  readonly label: string;
  readonly path: string;
}

export const EXPLORE_FOOTER_LINKS: readonly ExploreFooterLink[] = [
  { label: 'Help', path: PATHS.home },
  { label: 'Status', path: PATHS.home },
  { label: 'About', path: PATHS.about },
  { label: 'Careers', path: PATHS.home },
  { label: 'Press', path: PATHS.home },
  { label: 'Blog', path: PATHS.home },
  { label: 'Store', path: PATHS.home },
  { label: 'Privacy', path: PATHS.privacy },
  { label: 'Rules', path: PATHS.rules },
  { label: 'Terms', path: PATHS.terms },
  { label: 'Text to speech', path: PATHS.home },
];
