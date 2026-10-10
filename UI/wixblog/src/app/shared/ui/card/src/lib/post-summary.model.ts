/** What post cards need. The UI model, NOT the Spring DTO (see features/blog/data/post.mapper.ts). */
export interface PostSummary {
  id: string;
  slug: string;
  title: string;
  excerpt: string;
  coverUrl: string | null;
  author: { id: string; name: string; avatarUrl: string | null };
  category: { slug: string; name: string } | null;
  publishedAt: string; // ISO 8601
  readingMinutes: number;
}
