import type { ExploreCategory } from './explore-taxonomy.data';

/**
 * Filters the taxonomy by a free-text query, matching against category, subcategory and
 * tag labels.
 *
 * - A category match keeps the whole category.
 * - A subcategory match keeps that subcategory's full tag list.
 * - A tag match keeps its parent subcategory, narrowed to just the matching tags.
 *
 * Subcategories with no surviving tags are dropped, and a category with no surviving
 * subcategories is dropped entirely.
 */
export function filterTaxonomy(
  categories: readonly ExploreCategory[],
  rawQuery: string,
): readonly ExploreCategory[] {
  const q = rawQuery.trim().toLowerCase();
  if (!q) return categories;

  return categories.reduce<ExploreCategory[]>((acc, category) => {
    if (matches(category.label, q)) {
      acc.push(category);
      return acc;
    }

    const subcategories = category.subcategories.reduce<(typeof category.subcategories)[number][]>(
      (subs, sub) => {
        if (matches(sub.label, q)) {
          subs.push(sub);
          return subs;
        }

        const tags = [...sub.tags, ...sub.hiddenTags].filter((t) => matches(t.label, q));
        if (tags.length) {
          subs.push({ ...sub, tags, hiddenTags: [] });
        }
        return subs;
      },
      [],
    );

    if (subcategories.length) acc.push({ ...category, subcategories });
    return acc;
  }, []);
}

function matches(label: string, query: string): boolean {
  return label.toLowerCase().includes(query);
}