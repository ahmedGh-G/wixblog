import type { Routes } from '@angular/router';

/**
 * Child routes for the explore feature.
 * Slice layout: pages/ components/ data/ services/ directives/ pipes/
 *
 * Mounted at "" under MainLayout, so these paths are the absolute URLs:
 *   /explore-topics
 */
export const EXPLORE_ROUTES: Routes = [
  {
    path: 'explore-topics',
    pathMatch: 'full',
    loadComponent: () =>
      import('@features/explore/pages/explore-topics.page').then((m) => m.ExploreTopicsPage),
  },
  {
    path: 'search',
    loadComponent: () => import('@features/explore/pages/search.page').then((m) => m.SearchPage),
  },
  // A topic page serves a category, a subcategory or a tag — all three are "topics".
  // `**` rather than `:slug` because subcategory slugs are nested ("life/family"),
  // which a single-segment param would not match. One or more segments both work.
  {
    path: 'topic/**',
    loadComponent: () => import('@features/explore/pages/topic.page').then((m) => m.TopicPage),
  },
];
