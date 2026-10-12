/**
 * Every URL in the app, in one place.
 *
 * Components import from here instead of hardcoding '/topic/...' etc, so renaming a URL
 * touches this file only. Mirrors the route table in `src/app/app.routes.ts` — every key
 * below corresponds to a route registered there, and every registered route has a key.
 *
 * Functions return arrays so they bind straight to `routerLink`, e.g.
 * `[routerLink]="PATHS.topic(slug)"`. Keys are functions only where the URL carries a
 * parameter; static destinations are plain strings.
 *
 * Routes that are still commented out in app.routes.ts (list, pub, stats, notifications,
 * staff) deliberately have no entry: they are not reachable, and adding them here would
 * invite links to dead ends. Add the key in the same commit that uncomments the route.
 */
export const PATHS = {
  home: '/',

  // public content
  post: (slug: string) => ['/post', slug],
  topics: '/explore-topics',
  topic: (slug: string) => ['/topic', slug],
  search: '/search',

  /** Profile URLs are served by `profileMatcher`, not a literal path, so these build the string. */
  profile: (username: string) => `/@${username}`,
  profileAbout: (username: string) => `/@${username}/about`,

  // static + system
  about: '/about',
  membership: '/membership',
  terms: '/legal/terms',
  privacy: '/legal/privacy',
  rules: '/legal/rules',
  forbidden: '/forbidden',
  gone: '/gone',

  // member area
  library: '/me/library',
  following: '/following',
  myStories: '/me/stories',
  settings: '/me/settings',

  // editor
  newStory: '/new-story',
  editStory: (id: string | number) => ['/p', String(id), 'edit'],
  previewStory: (id: string | number) => ['/p', String(id), 'preview'],

  // auth
  login: '/auth/login',
  register: '/auth/register',
  forgotPassword: '/auth/forgot-password',
  resetPassword: (token: string) => ['/auth/reset-password', token],
  verifyEmail: '/auth/verify-email',

  // dev only
  postCardsPlayground: '/playground/post-cards',
} as const;
