import type {Routes} from '@angular/router';
import {authGuard, guestGuard, permissionGuard} from '@core/auth/auth.guards';
import {profileMatcher} from '@core/routing/profile.matcher';
import {unsavedChangesGuard} from '@core/routing/unsaved-changes.guard';

/**
 * Skeleton generated from docs/SITEMAP.md.
 * Feature route files do not exist yet: create each one as you build that slice,
 * and keep unbuilt lines commented out so the app compiles.
 * don't create the commented blocks components
 */
export const routes: Routes = [
  // ───────────────────────────── ① MainLayout: reading + member shell
  {
    path: '',
    loadComponent: () => import('./layouts/main.layout').then((m) => m.MainLayout),
    children: [
      // "/" is two different pages depending on who asks

      {
        path: '',
        loadChildren: () => import('@features/feed/feed.routes').then((m) => m.FEED_ROUTES),
      },

      // public content (SSR)
      {
        path: 'post/:slug',
        loadChildren: () => import('@features/story/story.routes').then((m) => m.STORY_ROUTES),
      },
      {
        path: '',
        loadChildren: () =>
          import('@features/explore/explore.routes').then((m) => m.EXPLORE_ROUTES),
      }, // topics, topic/:slug, tag/:slug, search
      {
        matcher: profileMatcher,
        loadChildren: () =>
          import('@features/profile/profile.routes').then((m) => m.PROFILE_ROUTES),
      }, // @:username [1.0]
      // { path: 'list/:id', loadChildren: ... LISTS_ROUTES },                                  // [1.2]
      // { path: 'pub/:slug', loadChildren: ... PUBLICATION_ROUTES },                           // [2.0]
      {
        path: '',
        loadChildren: () => import('@features/static/static.routes').then((m) => m.STATIC_ROUTES),
      }, // about, membership, legal/*

      // member area (/me/*)
      {
        path: 'me',
        canMatch: [authGuard],
        children: [
          {
            path: 'library',
            loadChildren: () =>
              import('@features/library/library.routes').then((m) => m.LIBRARY_ROUTES),
          }, // [1.1]
          {
            path: 'following',
            loadChildren: () =>
              import('@features/following/following.routes').then((m) => m.FOLLOWING_ROUTES),
          }, // [1.1]
          {
            path: 'stories',
            canMatch: [permissionGuard('post:write')],
            loadChildren: () =>
              import('@features/my-stories/my-stories.routes').then((m) => m.MY_STORIES_ROUTES),
          }, // [1.0]
          // { path: 'stats', ... STATS_ROUTES },                                               // [1.2]
          // { path: 'notifications', ... NOTIFICATIONS_ROUTES },                               // [1.2]
          {
            path: 'settings',
            loadChildren: () =>
              import('@features/settings/settings.routes').then((m) => m.SETTINGS_ROUTES),
          },
          { path: '', pathMatch: 'full', redirectTo: 'stories' },
        ],
      },

      // system pages
      {
        path: 'forbidden',
        loadComponent: () => import('@features/system/forbidden.page').then((m) => m.ForbiddenPage),
      },
      {
        path: 'gone',
        loadComponent: () => import('@features/system/gone.page').then((m) => m.GonePage),
      },
    ],
  },

  // ───────────────────────────── ② EditorLayout: distraction-free writing
  {
    path: '',
    canMatch: [authGuard, permissionGuard('post:write')],
    loadComponent: () => import('./layouts/editor.layout').then((m) => m.EditorLayout),
    children: [
      {
        path: 'new-story',
        loadComponent: () =>
          import('@features/editor/pages/new-story.page').then((m) => m.NewStoryPage),
      },
      {
        path: 'p/:id/edit',
        canDeactivate: [unsavedChangesGuard],
        loadComponent: () =>
          import('@features/editor/pages/story-editor.page').then((m) => m.StoryEditorPage),
      },
      {
        path: 'p/:id/preview',
        loadComponent: () =>
          import('@features/editor/pages/story-preview.page').then((m) => m.StoryPreviewPage),
      },
    ],
  },

  // ───────────────────────────── ③ AuthLayout: centered card
  {
    path: 'auth',
    canMatch: [guestGuard],
    loadComponent: () => import('./layouts/auth.layout').then((m) => m.AuthLayout),
    loadChildren: () => import('@features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },

  // ───────────────────────────── ④ StaffLayout: back office, permission per page
  // {
  //   path: 'staff',
  //   canMatch: [authGuard],
  //   loadComponent: () => import('./layouts/staff.layout').then(m => m.StaffLayout),
  //   children: [
  //     { path: 'queue',     canMatch: [permissionGuard('post:review')],
  //                          loadChildren: () => import('@features/moderation/moderation.routes').then(m => m.MODERATION_ROUTES) },  // queue, queue/:postId, published, reports
  //     { path: 'topics',    canMatch: [permissionGuard('taxonomy:manage')],
  //                          loadChildren: () => import('@features/taxonomy/taxonomy.routes').then(m => m.TAXONOMY_ROUTES) },       // [1.1]
  //     // { path: 'users',     canMatch: [permissionGuard('user:manage')], ... USERS_ROUTES },                                     // [1.1]
  //     // { path: 'dashboard', canMatch: [permissionGuard('dashboard:view')], ... DASHBOARD_ROUTES },                              // [1.2]
  //     // { path: 'audit-log', canMatch: [permissionGuard('user:manage')], ... AUDIT_ROUTES },                                     // [1.1]
  //     { path: '', pathMatch: 'full', redirectTo: 'queue' },   // TODO: redirect to the first page the user may open
  //   ],
  // },

  {
    path: '**',
    loadComponent: () => import('@features/system/not-found.page').then((m) => m.NotFoundPage),
  },

  {
    path: 'playground/post-cards',
    loadComponent: () =>
      import('./shared/ui/card/src/lib/post-cards.gallery').then((m) => m.PostCardsGallery),
  },
];
