import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  afterNextRender,
  computed,
  effect,
  inject,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideChevronLeft, lucideChevronRight, lucideCompass, lucidePlus } from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';

import { EXPLORE_TOPIC_BAR_ITEMS, type ExploreTopicBarItem } from '../data/explore-topics.data';

/**
 * The horizontal topic pill bar (`prototyping/explore/topics-bar.png`).
 *
 * Sizing follows the spec measured off the full-page screenshot: a 27.986px-tall row
 * sitting at the top of the same 1496.458px centered column as the rest of the page.
 * That is tighter than the pills' natural height, so padding is collapsed and the label
 * drops to 13px to fit.
 *
 * ## Symmetrical paging
 * The two arrows scroll by whole viewports rather than a fixed pixel delta, and every
 * landing position is snapped to a multiple of the viewport width:
 *
 *     target = round(scrollLeft / viewportWidth) * viewportWidth   (Â± 1 viewport)
 *
 * Snapping is what makes the round trip exact. Without it, scrolling right then left
 * subtracts the same width from an off-boundary offset and lands a few pixels away from
 * where it started â€” visibly asymmetric once the bar has a partial page at its end.
 */
import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-explore-topics-bar',
  imports: [RouterLink, NgIcon],
  providers: [provideIcons({ lucideCompass, lucidePlus, lucideChevronLeft, lucideChevronRight })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="relative flex h-[27.986px] items-center">
      @if (canScrollBack()) {
        <button
          type="button"
          class="text-muted-foreground hover:text-foreground hover:bg-muted absolute start-0 z-10 grid size-7 shrink-0 place-items-center rounded-full transition-colors"
          aria-label="Scroll topics back"
          (click)="page(-1)"
        >
          <ng-icon name="lucideChevronLeft" class="size-5" aria-hidden="true" />
        </button>
      }

      <div
        #scroller
        class="flex flex-1 items-center gap-1.5 overflow-x-auto scroll-smooth [scrollbar-width:none] [&::-webkit-scrollbar]:hidden"
        role="navigation"
        aria-label="Topic categories"
        (scroll)="onScroll()"
      >
        @for (item of items(); track item.slug) {
          @if (!$first) {
            <ng-icon
              name="lucidePlus"
              class="text-muted-foreground/60 size-3.5 shrink-0"
              aria-hidden="true"
            />
          }

          @if (item.isActive) {
            <span
              class="bg-primary text-primary-foreground inline-flex h-[27.986px] shrink-0 items-center gap-1.5 rounded-full py-0 pr-3.5 pl-1 text-[13px] font-medium whitespace-nowrap"
              aria-current="page"
            >
              <span
                class="border-primary-foreground/40 flex size-[22px] shrink-0 items-center justify-center rounded-full border"
              >
                <ng-icon name="lucideCompass" class="size-3" aria-hidden="true" />
              </span>
              {{ item.label }}
            </span>
          } @else {
            <a
              [routerLink]="topicPath(item.slug)"
              class="bg-background hover:bg-muted inline-flex h-[27.986px] shrink-0 items-center rounded-full border px-3.5 text-[13px] font-medium whitespace-nowrap transition-colors"
            >
              {{ item.label }}
            </a>
          }
        }
      </div>

      @if (canScrollForward()) {
        <button
          type="button"
          class="text-muted-foreground hover:text-foreground hover:bg-muted absolute end-0 z-10 grid size-7 shrink-0 place-items-center rounded-full transition-colors"
          aria-label="Scroll topics forward"
          (click)="page(1)"
        >
          <ng-icon name="lucideChevronRight" class="size-5" aria-hidden="true" />
        </button>
      }
    </div>
  `,
  styles: [],
})
export class ExploreTopicsBar {
  readonly items = input<readonly ExploreTopicBarItem[]>(EXPLORE_TOPIC_BAR_ITEMS);

  private readonly host = inject(ElementRef<HTMLElement>);

  private readonly scroller = viewChild.required<ElementRef<HTMLElement>>('scroller');

  private readonly scrollLeft = signal(0);
  private readonly viewport = signal(0);
  private readonly scrollWidth = signal(0);

  protected readonly canScrollBack = computed(() => this.scrollLeft() > 1);
  protected readonly canScrollForward = computed(
    () => this.scrollLeft() + this.viewport() < this.scrollWidth() - 1,
  );

  constructor() {
    afterNextRender(() => this.observe());
    // Pill lists can change length (items input), so re-measure when they do.
    effect(() => {
      this.items();
      this.measure();
    });
  }

  /** Topic detail lives at PATHS.topic(slug). */
  protected topicPath = (slug: string) => PATHS.topic(slug);

  protected onScroll = () => this.scrollLeft.set(this.scroller().nativeElement.scrollLeft);

  /** `direction` is -1 for back and +1 for forward; both move exactly one viewport. */
  protected page = (direction: -1 | 1) => {
    const el = this.scroller().nativeElement;
    const width = el.clientWidth;
    if (!width) return;

    const bounds = this.pageBounds(el, width);
    if (bounds.length < 2) return;

    // Walk the boundary list rather than doing arithmetic on scrollLeft. When the final
    // page is partial, `maxScroll` is not a multiple of the viewport width, and naive
    // `round(scrollLeft / width)` arithmetic makes the step back from that clamped end
    // skip the page before it. Indexing the list keeps both directions exactly mirrored.
    const current = nearestIndex(bounds, el.scrollLeft);
    const next = current + direction;

    if (next < 0 || next >= bounds.length) return;
    el.scrollTo({ left: bounds[next], behavior: 'smooth' });
  };

  /**
   * Every legal resting offset: whole viewports, plus `maxScroll` when the tail is
   * partial. Sorted and de-duplicated so the forward and backward walks are mirror images.
   */
  private pageBounds(el: HTMLElement, width: number): number[] {
    const maxScroll = Math.max(0, el.scrollWidth - width);
    const bounds = new Set<number>([0]);

    for (let x = width; x < maxScroll; x += width) bounds.add(x);
    if (maxScroll > 0) bounds.add(maxScroll);

    return [...bounds].sort((a, b) => a - b);
  }

  private measure() {
    const el = this.scroller()?.nativeElement;
    if (!el) return;
    this.viewport.set(el.clientWidth);
    this.scrollWidth.set(el.scrollWidth);
    this.scrollLeft.set(el.scrollLeft);
  }

  private observe() {
    const el = this.scroller().nativeElement;
    this.measure();

    const observer = new ResizeObserver(() => this.measure());
    observer.observe(el);
    // Pills can wrap/reflow without the scroller box itself resizing.
    observer.observe(this.host.nativeElement);

    this.host.nativeElement.getRootNode().addEventListener('resize', () => this.measure());

    // Clean up when the view is torn down.
    queueMicrotask(() => {
      if (!this.host.nativeElement.isConnected) observer.disconnect();
    });
  }
}

/** Index of the boundary closest to `offset`. */
function nearestIndex(bounds: readonly number[], offset: number): number {
  let best = 0;
  let bestDistance = Math.abs(bounds[0] - offset);

  for (let i = 1; i < bounds.length; i++) {
    const distance = Math.abs(bounds[i] - offset);
    if (distance < bestDistance) {
      best = i;
      bestDistance = distance;
    }
  }
  return best;
}
