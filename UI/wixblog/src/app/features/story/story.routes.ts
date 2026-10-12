import type { Routes } from '@angular/router';

/**
 * Child routes for the story feature.
 * Slice layout: pages/ components/ data/ services/ directives/ pipes/
 */
export const STORY_ROUTES: Routes = [
  // The parent route is `post/:slug`, so the slug is already consumed and the child must be
  // empty. A `:slug` child here demanded `/post/:slug/:slug` and rendered a blank page.
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/story-details.page').then((m) => m.StoryDetailsPage),
  },
];
