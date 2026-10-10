import { ChangeDetectionStrategy, Component } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideChartColumn,
  lucideFileText,
  lucideHouse,
  lucideLibrary,
  lucidePlus,
  lucideSearch,
  lucideUser,
  lucideUsers,
} from '@ng-icons/lucide';
import { RouterLink } from '@angular/router';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmSeparatorImports } from '@spartan-ng/helm/separator';
import { HlmSheetImports } from '@spartan-ng/helm/sheet';

/**
 * Hamburger navigation drawer.
 *
 * Built on `HlmSheet` in `side="left"` mode: the CDK overlay gives the dimmed
 * backdrop and outside-click dismiss for free, which is exactly what the reference
 * drawer does. The alternative — `HlmSidebar` in collapsible mode — models a
 * persistent side rail with a toolbar and an inset main area, so the reference's
 * slide-over panel would have been fought rather than reproduced.
 *
 * Two visual details carried over from the reference:
 *  - the active row is bold and carries a 3px black bar on its left edge, which is a
 *    left border on an absolutely positioned pseudo-element rather than a real border,
 *    so the bar does not shift the label;
 *  - the "WixBlog Staff" row ends with a green status dot, reusing the Inkwell brand
 *    colour for the same "presence" signal.
 */
@Component({
  selector: 'app-nav-drawer',
  imports: [
    HlmSheetImports,
    HlmButtonImports,
    HlmSeparatorImports,
    RouterLink,
    NgIcon,
  ],
  providers: [
    provideIcons({
      lucideHouse,
      lucideLibrary,
      lucideUser,
      lucideFileText,
      lucideChartColumn,
      lucideUsers,
      lucidePlus,
      lucideSearch,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <hlm-sheet>
      <button
        hlmSheetTrigger
        hlmBtn
        variant="ghost"
        size="icon"
        type="button"
        class="-ms-2 text-muted-foreground"
        aria-label="Open navigation menu"
      >
        <ng-icon name="lucideHouse" class="size-5" />
      </button>

      <hlm-sheet-content side="left" class="w-[17.5rem] gap-0 p-0">
        <!-- Drawer header: wordmark + search, matching the reference -->
        <div class="flex items-center gap-3 border-b border-border px-4 py-3">
          <a routerLink="/" class="text-xl font-bold tracking-tight">WixBlog</a>
          <button
            hlmBtn
            variant="ghost"
            size="icon-sm"
            type="button"
            hlmSheetClose
            class="ms-auto text-muted-foreground"
            aria-label="Search"
          >
            <ng-icon name="lucideSearch" class="size-4" />
          </button>
        </div>

        <div hlmSeparator></div>

        <!-- Primary nav. The first row is active by default. -->
        <nav class="flex flex-col py-2" aria-label="Main">
          @for (item of navItems; track item.label) {
            <a
              routerLink="/"
              class="relative flex items-center gap-3 px-4 py-2.5 text-[15px] text-foreground transition-colors hover:bg-muted"
              [class.font-bold]="item.active"
              [attr.aria-current]="item.active ? 'page' : null"
            >
              @if (item.active) {
                <span class="bg-foreground absolute inset-y-0 start-0 w-[3px]"></span>
              }
              <ng-icon [name]="item.icon" class="size-5 text-muted-foreground" />
              {{ item.label }}
            </a>
          }
        </nav>

        <div hlmSeparator class="my-2"></div>

        <!-- Following section -->
        <div class="px-4 py-2">
          <h2 class="text-[13px] text-muted-foreground">Following</h2>

          <a
            routerLink="/"
            class="mt-3 flex items-center gap-3 text-[15px] text-muted-foreground transition-colors hover:text-foreground"
          >
            <ng-icon name="lucideUsers" class="size-5 shrink-0" />
            <span>Following</span>
          </a>

          <a
            routerLink="/"
            class="mt-3 flex items-center gap-3 text-[15px] text-muted-foreground transition-colors hover:text-foreground"
          >
            <span class="grid size-5 shrink-0 place-items-center text-xs font-bold">W</span>
            <span>WixBlog Staff</span>
            <span class="ms-auto size-1.5 rounded-full bg-brand" title="Active"></span>
          </a>

          <div class="mt-4 flex gap-3">
            <ng-icon name="lucidePlus" class="size-5 shrink-0 text-muted-foreground" />
            <p class="text-[15px] leading-relaxed text-muted-foreground">
              Find writers and publications to follow.
              <br />
              <a routerLink="/" class="underline underline-offset-2 hover:text-foreground">
                See suggestions
              </a>
            </p>
          </div>
        </div>
      </hlm-sheet-content>
    </hlm-sheet>
  `,
})
export class NavDrawer {
  protected readonly navItems = [
    { label: 'Home', icon: 'lucideHouse', active: true },
    { label: 'Library', icon: 'lucideLibrary', active: false },
    { label: 'Profile', icon: 'lucideUser', active: false },
    { label: 'Stories', icon: 'lucideFileText', active: false },
    { label: 'Stats', icon: 'lucideChartColumn', active: false },
  ] as const;
}