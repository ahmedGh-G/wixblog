import type { Routes } from '@angular/router';
import { BlogApi } from './blog.api';
import { BlogApiMock } from './blog.api.mock';
import { environment } from './environment';
import { BlogFeedStore } from './blog-feed.store';

export const BLOG_ROUTES: Routes = [
  {
    path: '',
    providers: [
      // DI swap: the store injects BlogApi and never knows which one it got.
      environment.useMocks ? { provide: BlogApi, useClass: BlogApiMock } : BlogApi,
      BlogFeedStore,
    ],
    children: [{ path: '', loadComponent: () => import('./home.page').then((m) => m.HomePage) }],
  },
];
