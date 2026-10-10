// ── Models ───────────────────────────────────────────────────────────────────────
export * from './components/feed-tab.model';
export * from './data/feed-ui.data';

// ── Chrome ───────────────────────────────────────────────────────────────────────
export * from './components/feed-promo-banner.component';
export * from './components/feed-tabs.component';

// ── Feed content ─────────────────────────────────────────────────────────────────
export * from './components/feed-list.component';
export * from './components/feed-post-row.component';
export * from './components/feed-post-byline.component';
export * from './components/feed-post-toolbar.component';

// ── Discovery sidebar ────────────────────────────────────────────────────────────
export * from './components/feed-discover-sidebar.component';
export * from './components/reading-list-promo.component';
export * from './components/sections/feed-discover-heading.component';
export * from './components/sections/feed-staff-picks.component';
export * from './components/sections/feed-trending.component';
export * from './components/sections/feed-topics.component';
export * from './components/sections/feed-who-to-follow.component';

// NOTE: pages are deliberately NOT re-exported here.
// `feed.routes.ts` lazy-loads them by direct path, and a page importing this barrel
// while the barrel exports that same page closes a cycle:
//     index.ts -> pages/feed.page.ts -> index.ts
// which surfaces at runtime as NG0919 "Cannot read @Component metadata" — the build
// succeeds and only the dev server fails. Components may import each other via direct
// relative paths for the same reason.