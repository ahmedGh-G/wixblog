import { UrlSegment, type UrlMatchResult } from '@angular/router';

/**
 * Matches the public profile namespace:
 *   /@username
 *   /@username/about
 *
 * Pops the `@` prefix and republishes `username` in `posParams` so
 * @features/profile child routes can read it from the param map.
 */
export function profileMatcher(segments: UrlSegment[]): UrlMatchResult | null {
  const [head] = segments;

  if (!head || !head.path.startsWith('@') || head.path.length < 2) {
    return null;
  }

  return {
    consumed: segments,
    posParams: {
      username: new UrlSegment(head.path.slice(1), head.parameters),
    },
  };
}
