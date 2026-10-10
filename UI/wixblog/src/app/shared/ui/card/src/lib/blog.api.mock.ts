import { Injectable } from '@angular/core';
import { delay, map, type Observable, of, switchMap, throwError, timer } from 'rxjs';

import { MOCK_POST_DTOS } from './mock-posts';
import { toPostSummary } from './post.mapper';
import { BlogApi } from './blog.api';
import { PostSummary } from './post-summary.model';

const byId = (ids: number[]) =>
  ids.map((id) => MOCK_POST_DTOS.find((p) => p.id === id)!).map(toPostSummary);

/** Same public shape as BlogApi, so the store can't tell the difference. No HttpClient needed. */
@Injectable()
export class BlogApiMock implements Pick<
  BlogApi,
  'feed' | 'recommended' | 'trending' | 'setBookmark'
> {
  feed(page = 0, size = 8): Observable<PostSummary[]> {
    return of(MOCK_POST_DTOS).pipe(
      delay(600), // long enough to see the loading skeleton
      map((l) => l.slice(page * size, (page + 1) * size).map(toPostSummary)),
    );
  }

  recommended(): Observable<PostSummary[]> {
    return of(byId([9, 10, 12])).pipe(delay(700));
  }

  trending(): Observable<PostSummary[]> {
    return of(byId([2, 4, 8, 1, 10])).pipe(delay(500));
  }

  setBookmark(postId: string, _on: boolean): Observable<void> {
    return postId === '7'
      ? timer(300).pipe(switchMap(() => throwError(() => new Error('mock: bookmark failed'))))
      : of(void 0).pipe(delay(200));
  }
}
