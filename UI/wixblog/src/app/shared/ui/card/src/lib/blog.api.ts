import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, type Observable } from 'rxjs';

import type { PostSummaryDto, SpringPage } from './post.dto';
import { toPostSummary } from './post.mapper';
import { PostSummary } from './post-summary.model';

/** HTTP only. No state, no UI logic. Provided on the blog route, not root. */
@Injectable()
export class BlogApi {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080';

  feed(page = 0, size = 10): Observable<PostSummary[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<SpringPage<PostSummaryDto>>(`${this.base}/posts`, { params })
      .pipe(map((p) => p.content.map(toPostSummary)));
  }

  recommended(): Observable<PostSummary[]> {
    return this.http
      .get<PostSummaryDto[]>(`${this.base}/posts/recommended`)
      .pipe(map((l) => l.map(toPostSummary)));
  }

  trending(): Observable<PostSummary[]> {
    return this.http
      .get<PostSummaryDto[]>(`${this.base}/posts/trending`)
      .pipe(map((l) => l.map(toPostSummary)));
  }

  setBookmark(postId: string, on: boolean): Observable<void> {
    const url = `${this.base}/posts/${postId}/bookmark`;
    return on ? this.http.put<void>(url, {}) : this.http.delete<void>(url);
  }
}
