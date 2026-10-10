/** Shape returned by Spring Boot. Never used in templates. */
export interface PostSummaryDto {
  id: number;
  slug: string;
  title: string;
  excerpt: string;
  coverImageUrl: string | null;
  authorId: number;
  authorName: string;
  authorAvatarUrl: string | null;
  categorySlug: string | null;
  categoryName: string | null;
  publishedAt: string;
  readingTimeMinutes: number;
}

/** Spring Data Page<T> (subset) */
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  number: number;
  size: number;
}
