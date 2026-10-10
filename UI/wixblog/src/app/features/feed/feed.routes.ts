import type { Routes } from '@angular/router';

import { BlogApi } from '@shared/ui/card/src/lib/blog.api';
import { BlogApiMock } from '@shared/ui/card/src/lib/blog.api.mock';
import { BlogFeedStore } from '@shared/ui/card/src/lib/blog-feed.store';
import { environment } from '@shared/ui/card/src/lib/environment';
import { authGuard, guestGuard } from '@core/auth/auth.guards';

export const FEED_ROUTES: Routes = [
  {
    path: '',
    providers: [
      environment.useMocks ? { provide: BlogApi, useClass: BlogApiMock } : BlogApi,
      BlogFeedStore,
    ],
    children: [
      {
        path: '',
        pathMatch: 'full',
        canMatch: [authGuard],
        loadComponent: () => import('@features/feed/pages/feed.page').then((m) => m.FeedPage),
      },
      {
        path: '',
        pathMatch: 'full',
        canMatch: [guestGuard],
        loadComponent: () => import('@features/feed/pages/landing.page').then((m) => m.LandingPage),
      },
      {
        path: 'following',
        loadComponent: () =>
          import('@features/feed/pages/following.page').then((m) => m.FollowingPage),
      },
    ],
  },
];
