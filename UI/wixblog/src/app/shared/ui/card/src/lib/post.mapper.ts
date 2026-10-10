import type { PostSummaryDto } from './post.dto';
import { PostSummary } from './post-summary.model';

export const toPostSummary = (d: PostSummaryDto): PostSummary => ({
  id: String(d.id),
  slug: d.slug,
  title: d.title,
  excerpt: d.excerpt,
  coverUrl: d.coverImageUrl,
  author: { id: String(d.authorId), name: d.authorName, avatarUrl: d.authorAvatarUrl },
  category:
    d.categorySlug && d.categoryName ? { slug: d.categorySlug, name: d.categoryName } : null,
  publishedAt: d.publishedAt,
  readingMinutes: d.readingTimeMinutes,
});
