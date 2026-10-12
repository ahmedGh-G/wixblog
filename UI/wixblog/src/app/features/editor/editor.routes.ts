import type { Routes } from '@angular/router';

/**
 * Child routes for the editor feature.
 * Slice layout: pages/ components/ data/ services/ directives/ pipes/
 *
 * Mounted at "" under EditorLayout, so these paths are the absolute URLs:
 *   /new-story
 */
export const EDITOR_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('@features/editor/pages/new-story.page').then((m) => m.NewStoryPage),
  },
];
