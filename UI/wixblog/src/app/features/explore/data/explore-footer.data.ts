import { PATHS } from '../../../../../context/paths';

/**
 * Footer link list at the bottom of /explore-topics
 * (`protoyping/explore/explore-topics-fullpage.png`).
 *
 * Paths resolve against PATHS where a route exists. The Medium-internal destinations
 * (Help, Status, Careers, …) have no counterpart in this app yet and point at "/"
 * until they do.
 */
export interface ExploreFooterLink {
  readonly label: string;
  readonly path: string;
}

export const EXPLORE_FOOTER_LINKS: readonly ExploreFooterLink[] = [
  { label: 'Help', path: '/' },
  { label: 'Status', path: '/' },
  { label: 'About', path: PATHS.about },
  { label: 'Careers', path: '/' },
  { label: 'Press', path: '/' },
  { label: 'Blog', path: '/' },
  { label: 'Store', path: '/' },
  { label: 'Privacy', path: PATHS.privacy },
  { label: 'Rules', path: PATHS.rules },
  { label: 'Terms', path: PATHS.terms },
  { label: 'Text to speech', path: '/' },
];