import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { HlmSkeletonImports } from '@spartan-ng/helm/skeleton';

import { StoryAuthorCard } from '../components/story-author-card.component';
import { StoryAuthorLine } from '../components/story-author-line.component';
import { StoryBody } from '../components/story-body.component';
import { StoryHeadline } from '../components/story-headline.component';
import { StoryRecommendations } from '../components/story-recommendations.component';
import { StorySocialBar } from '../components/story-social-bar.component';
import { StoryTagsBar } from '../components/story-tags-bar.component';
import { ResponsesSection } from '../components/responses-section.component';
import { StoryStore } from '../data/story.store';

@Component({
  selector: 'app-story-details-page',
  imports: [
    ResponsesSection,
    StoryAuthorCard,
    StoryAuthorLine,
    StoryBody,
    StoryHeadline,
    StoryRecommendations,
    StorySocialBar,
    StoryTagsBar,
    HlmSkeletonImports,
  ],
  providers: [StoryStore],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto w-full max-w-[42.5rem] px-4 py-8">
      @if (store.isLoading()) {
        <div class="flex flex-col gap-6">
          <div hlmSkeleton class="h-5 w-48"></div>
          <div hlmSkeleton class="h-12 w-full"></div>
          <div hlmSkeleton class="h-12 w-3/4"></div>
          <div hlmSkeleton class="h-8 w-64"></div>
          <div hlmSkeleton class="h-96 w-full"></div>
        </div>
      } @else if (store.isEmpty()) {
        <div class="flex flex-col items-center gap-4 py-24 text-center">
          <h1 class="text-2xl font-bold tracking-tight">This story does not exist</h1>
          <p class="text-muted-foreground text-sm">
            It may have been unpublished, or the link could be wrong.
          </p>
        </div>
      } @else if (story(); as s) {
        <div class="flex flex-col gap-8">
          <section data-section="Member badge + Tags bar" class="flex flex-col gap-6">
            <app-story-tags-bar [tags]="s.tags" [membersOnly]="s.membersOnly" />
          </section>

          <section data-section="Headline" class="flex flex-col gap-6">
            <app-story-headline [title]="s.title" [subtitle]="s.subtitle" />
          </section>

          <section data-section="Author line">
            <app-story-author-line
              [author]="s.author"
              [readingMinutes]="s.readingMinutes"
              [publishedAt]="s.publishedAt"
              [following]="store.following()"
              (followToggle)="store.toggleFollow()"
            />
          </section>

          <section data-section="Social bar (top)">
            <app-story-social-bar
              [claps]="s.engagement.claps"
              [clapped]="s.engagement.clapped"
              [responses]="store.responsesCount()"
              [saved]="s.engagement.saved"
              (clapToggle)="store.toggleClap()"
              (saveToggle)="store.toggleSave()"
            />
          </section>

          <section data-section="Article body">
            <app-story-body [blocks]="s.body" [paywall]="s.paywall ?? null" />
          </section>

          <section data-section="Social bar (bottom)">
            <app-story-social-bar
              [claps]="s.engagement.claps"
              [clapped]="s.engagement.clapped"
              [responses]="store.responsesCount()"
              [saved]="s.engagement.saved"
              (clapToggle)="store.toggleClap()"
              (saveToggle)="store.toggleSave()"
            />
          </section>

          <section data-section="Author bio card">
            <app-story-author-card
              [author]="s.author"
              [following]="store.following()"
              (followToggle)="store.toggleFollow()"
            />
          </section>

          <app-responses-section
            data-section="Responses"
            [count]="store.responsesCount()"
            [comments]="store.comments()"
            [user]="store.currentUser"
            [draft]="store.draft()"
            [loading]="store.isLoading()"
            (draftChange)="store.setDraft($event)"
            (submit)="store.submitComment()"
          />

          <app-story-recommendations
            data-section="More from Author"
            title="More from the author"
            [posts]="store.moreFromAuthor()"
            ctaLabel="See all from this author"
            emptyLabel="This author has not published anything else yet."
          />

          <app-story-recommendations
            data-section="Similar stories"
            title="Similar stories"
            [posts]="store.similarStories()"
            ctaLabel="See more recommendations"
          />
        </div>
      }
    </div>
  `,
})
export class StoryDetailsPage {
  protected readonly store = inject(StoryStore);
  private readonly route = inject(ActivatedRoute);

  readonly slug = input<string>('', { alias: 'slug' });

  protected readonly story = computed(() => this.store.story());

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.store.load(params.get('slug') ?? '');
    });
  }
}
