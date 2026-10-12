// ── Data ─────────────────────────────────────────────────────────────────────────
export * from './data/explore-topics.data';
export * from './data/explore-taxonomy.data';
export * from './data/explore-taxonomy.filter';
export * from './data/explore-footer.data';
export * from './data/topic-detail.data';
export * from './data/search.data';

// ── Components ───────────────────────────────────────────────────────────────────
export * from './components/explore-topics-bar.component';
export * from './components/explore-topics-search.component';
export * from './components/explore-taxonomy-grid.component';
export * from './components/explore-category-cell.component';
export * from './components/explore-subcategory.component';
export * from './components/explore-suggest-edit.component';
export * from './components/explore-footer.component';
export * from './components/topic-header.component';
export * from './components/topic-story-row.component';
export * from './components/topic-featured-stories.component';
export * from './components/topic-explorer-section.component';
export * from './components/search-main-section.component';
export * from './components/search-filter-bar.component';
export * from './components/search-discover-section.component';

// NOTE: pages are deliberately NOT re-exported here.
// `explore.routes.ts` lazy-loads the page by direct path, and a page importing this
// barrel while the barrel exports that same page closes a cycle:
//     index.ts -> pages/explore-topics.page.ts -> index.ts
// which surfaces at runtime as NG0919 "Cannot read @Component metadata" — the build
// succeeds and only the dev server fails. Components may import each other via direct
// relative paths for the same reason.
