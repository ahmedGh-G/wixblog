import { RenderMode, ServerRoute } from '@angular/ssr';

/**
 * Render strategy per URL pattern.
 *
 * `**` is RenderMode.Server, NOT Prerender. Prerendering a wildcard is only
 * possible when Angular can enumerate every URL in the config, and `app.routes.ts`
 * registers `profileMatcher` (the /@username branch). Matchers are opaque to the
 * prerenderer, so it refuses the wildcard with:
 *
 *   The route '**' is set for prerendering but has a defined matcher.
 *
 * Server mode renders per request instead, which has no such restriction.
 */
export const serverRoutes: ServerRoute[] = [
  {
    // The feed root ("/"). Server-rendered, not prerendered: the rows are populated by
    // BlogFeedStore during bootstrap, so they only exist once the request has actually
    // run. Prerendering emits at build time, gets an empty shell, and fails with
    // "content returned was empty". Client mode would also build, but would ship a
    // blank page to crawlers and to anyone with JS disabled.
    //
    // NB: this was previously 'home-feed', which no longer matches any route now that
    // the feed sits at "" under MainLayout.
    path: '',
    renderMode: RenderMode.Server,
  },
  {
    path: '**',
    renderMode: RenderMode.Server,
  },
];
