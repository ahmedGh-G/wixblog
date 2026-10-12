import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

import { EXPLORE_TAXONOMY } from '../data/explore-taxonomy.data';
import { TOPIC_HEADER } from '../data/topic-detail.data';
import { TopicExplorerSection } from '../components/topic-explorer-section.component';
import { TopicFeaturedStories } from '../components/topic-featured-stories.component';
import { TopicHeader } from '../components/topic-header.component';

/**
 * `/topic/**` â€” i.e. ['/topic', slug].
 *
 * A topic is the root of all three taxonomy levels, so the same page serves a
 * category ("life"), a subcategory ("life/family") and a tag ("adoption"). The slug
 * decides which: it is matched against EXPLORE_TAXONOMY to find the nearest node, and
 * falls back to the generic header when nothing matches.
 *
 * Composes `prototyping/explore/topic/` top to bottom:
 *   1. TopicHeader            (topic-header-section.png)
 *   2. TopicFeaturedStories   (selected(featured)-topic-stories.png)
 *   3. TopicExplorerSection   (topic-exploer-section.png)
 *
 * One centred column with 24px gutters â€” the main content is centred in the page.
 */
@Component({
  selector: 'app-topic-page',
  imports: [TopicHeader, TopicFeaturedStories, TopicExplorerSection],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto w-full max-w-[1496.458px] px-6 py-8">
      <app-topic-header [header]="header()" />

      <app-topic-featured-stories class="mt-16 block" />

      <app-topic-explorer-section class="mt-20 block" />
    </div>
  `,
  styles: [],
})
export class TopicPage {
  private readonly route = inject(ActivatedRoute);

  /**
   * The topic slug, derived from the URL rather than a route param: the wildcard
   * segment is not reliably exposed on `paramMap` for a `**` path, but the resolved
   * URL always carries it. Everything after "topic/" is the slug, which may be nested
   * ("life/family").
   */
  readonly slug = toSignal(
    this.route.url.pipe(
      map((segments) =>
        segments
          .map((s) => s.path)
          .filter((p) => p !== 'topic')
          .join('/'),
      ),
    ),
    { initialValue: '' },
  );

  protected readonly title = computed(() => {
    const slug = this.slug();
    if (!slug) return TOPIC_HEADER.title;
    return this.resolveLabel(slug) ?? humanize(slug.split('/').pop() ?? slug);
  });

  protected readonly header = computed(() => ({ ...TOPIC_HEADER, title: this.title() }));

  /** Exact taxonomy match first, then the nearest ancestor node. */
  private resolveLabel(slug: string): string | null {
    const segments = slug.split('/');

    for (const category of EXPLORE_TAXONOMY) {
      if (category.slug === slug) return category.label;

      for (const sub of category.subcategories) {
        if (sub.slug === slug) return sub.label;

        const tag = [...sub.tags, ...sub.hiddenTags].find((t) => t.slug === slug);
        if (tag) return tag.label;
      }
    }

    // A parent of the requested slug, e.g. "life" for "life/does-not-exist".
    const parent = EXPLORE_TAXONOMY.find((c) => segments[0] === c.slug);
    return parent ? parent.label : null;
  }
}

function humanize(value: string): string {
  return value.replace(/-/g, ' ').replace(/\b\w/g, (c) => c.toUpperCase());
}
