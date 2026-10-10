import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBell,
  lucideLayoutGrid,
  lucidePenLine,
  lucideSearch,
} from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmTooltipImports } from '@spartan-ng/helm/tooltip';

/**
 * Sticky top bar.
 *
 * Left: menu trigger, wordmark, search. Right: product pill, "Write", notifications,
 * avatar. The bar is `sticky top-0` and carries its own bottom hairline, so it reads as
 * a distinct surface from the scrolling feed beneath it.
 *
 * The menu button is the only interactive element that needs to talk to the page, so it
 * takes the drawer open state as an input rather than owning the sheet itself. That
 * keeps the sheet (and its CDK overlay) mounted in the page, where the rest of the
 * page layout lives.
 */
@Component({
  selector: 'app-feed-navbar',
  imports: [
    RouterLink,
    HlmButtonImports,
    HlmInputImports,
    HlmAvatarImports,
    HlmTooltipImports,
    NgIcon,
  ],
  providers: [
    provideIcons({ lucideSearch, lucideBell, lucidePenLine, lucideLayoutGrid }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header
      class="sticky top-0 z-40 flex h-14 items-center gap-4 border-b border-border bg-background px-4 lg:px-6"
    >
      <!--
        Menu trigger. Projected from the page so the drawer component and the button
        that opens it live together; the navbar only decides where the slot sits.
      -->
      <ng-content select="app-nav-drawer" />

      <!-- Wordmark -->
      <a routerLink="/" class="text-xl font-bold tracking-tight">WixBlog</a>

      <!-- Search pill -->
      <div class="relative ms-2 hidden w-64 md:block">
        <ng-icon
          name="lucideSearch"
          class="pointer-events-none absolute start-4 top-1/2 z-10 size-4 -translate-y-1/2 text-muted-foreground"
        />
        <input
          hlmInput
          type="search"
          placeholder="Search"
          aria-label="Search WixBlog"
          class="rounded-4xl bg-muted ps-10 text-sm placeholder:text-muted-foreground"
        />
      </div>

      <!-- Right cluster -->
      <div class="ms-auto flex items-center gap-1">
        <button hlmBtn variant="default" size="sm" type="button" class="gap-1.5 px-4">
          <ng-icon name="lucideLayoutGrid" class="size-4" />
          PlayGames
        </button>

        <button hlmBtn variant="ghost" size="sm" type="button" class="gap-1.5 text-muted-foreground">
          <ng-icon name="lucidePenLine" class="size-4" />
          Write
        </button>

        <span hlmTooltip="Notifications">
          <button
            hlmBtn
            variant="ghost"
            size="icon-sm"
            type="button"
            class="text-muted-foreground"
            aria-label="Notifications"
          >
            <ng-icon name="lucideBell" class="size-5" />
          </button>
        </span>

        <hlm-avatar size="default" class="ms-1">
          @if (avatarUrl(); as src) {
            <img hlmAvatarImage [src]="src" alt="" />
          } @else {
            <span hlmAvatarFallback>W</span>
          }
        </hlm-avatar>
      </div>
    </header>
  `,
})
export class FeedNavbar {
  readonly avatarUrl = input<string | null>(null);
}