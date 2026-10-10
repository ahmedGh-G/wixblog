import type { Routes } from '@angular/router';

import { BlogApi } from '../src/app/shared/ui/card/src/lib/blog.api';
import { BlogApiMock } from '../src/app/shared/ui/card/src/lib/blog.api.mock';
import { BlogFeedStore } from '../src/app/shared/ui/card/src/lib/blog-feed.store';
import { environment } from '../src/app/shared/ui/card/src/lib/environment';

/**
 * Lazy child routes for the home feed page.
 *
 * Mounted at `/home-feed` from `src/app/app.routes.ts`. The providers mirror
 * `BLOG_ROUTES`: the store injects `BlogApi` and never learns which implementation it
 * received, so flipping `environment.useMocks` off swaps in the real HTTP API without
 * touching a component.
 */
export const HOME_FEED_ROUTES: Routes = [
  {
    path: '',
    providers: [
      environment.useMocks ? { provide: BlogApi, useClass: BlogApiMock } : BlogApi,
      BlogFeedStore,
    ],
    loadComponent: () => import('./home-feed.page').then((m) => m.HomeFeedPage),
  },
];