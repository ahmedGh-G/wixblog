import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  HostListener,
  inject,
  input,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBell,
  lucideLayoutGrid,
  lucideMenu,
  lucidePenLine,
  lucideSearch,
} from '@ng-icons/lucide';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmDropdownMenuImports } from '@spartan-ng/helm/dropdown-menu';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmTooltipImports } from '@spartan-ng/helm/tooltip';
import { HlmSidebarService } from '@spartan-ng/helm/sidebar';

import { PATHS } from '@shared/paths';

import { TopbarExplorePopup } from './topbar-explore-popup.component';
import { UserProfileMenu } from './user-profile-menu.component';

@Component({
  selector: 'app-topbar',
  imports: [
    RouterLink,
    HlmButtonImports,
    HlmInputImports,
    HlmAvatarImports,
    HlmTooltipImports,
    NgIcon,
    TopbarExplorePopup,
    UserProfileMenu,
    HlmDropdownMenuImports,
  ],
  providers: [
    provideIcons({
      lucideMenu,
      lucideSearch,
      lucideBell,
      lucidePenLine,
      lucideLayoutGrid,
    }),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: `
    :host {
      display: contents;
    }
  `,
  template: `
    <header
      class="bg-background/95 sticky top-0 z-40 flex h-14 shrink-0 items-center gap-4 border-b border-border px-4 backdrop-blur lg:px-6"
    >
      <button
        hlmBtn
        variant="ghost"
        size="icon"
        type="button"
        class="-ms-2 text-muted-foreground"
        [attr.aria-expanded]="sidebar.open()"
        aria-controls="app-nav-sidebar"
        aria-label="Toggle navigation sidebar"
        (click)="sidebar.toggleSidebar()"
      >
        <ng-icon name="lucideMenu" class="size-5" />
      </button>

      <a routerLink="/" class="text-xl font-bold tracking-tight">WixBlog</a>

      <div #searchAnchor class="relative ms-2 hidden w-64 md:block">
        <ng-icon
          name="lucideSearch"
          class="pointer-events-none absolute start-4 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"
        />
        <input
          hlmInput
          type="search"
          placeholder="Search"
          aria-label="Search WixBlog"
          aria-haspopup="dialog"
          [attr.aria-expanded]="searchOpen()"
          [attr.aria-controls]="searchOpen() ? 'topbar-search-popup' : null"
          class="rounded-4xl bg-muted ps-10 text-sm placeholder:text-muted-foreground"
          (click)="toggleSearch($event)"
          (input)="onQuery($event)"
          (keydown.enter)="submitSearch()"
        />

        @if (searchOpen()) {
          <div id="topbar-search-popup">
            <app-topbar-explore-popup (dismissed)="closeSearch()" />
          </div>
        }
      </div>

      <ng-content select="[appTopbarEnd]" />

      <div class="ms-auto flex items-center gap-1">
        <button hlmBtn variant="default" size="sm" type="button" class="gap-1.5 px-4">
          <ng-icon name="lucideLayoutGrid" class="size-4" />
          PlayGames
        </button>

        <a
          hlmBtn
          variant="ghost"
          size="sm"
          [routerLink]="PATHS.newStory"
          class="gap-1.5 text-muted-foreground"
        >
          <ng-icon name="lucidePenLine" class="size-4" />
          Write
        </a>

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

        <button
          type="button"
          hlmDropdownMenuTrigger
          [hlmDropdownMenuTrigger]="menu"
          align="end"
          side="bottom"
          sideOffset="8"
          class="ms-1 cursor-pointer rounded-full"
          aria-label="Open user menu"
        >
          <hlm-avatar size="default">
            @if (avatarUrl(); as src) {
              <img hlmAvatarImage [src]="src" alt="" />
            } @else {
              <span hlmAvatarFallback>A</span>
            }
          </hlm-avatar>
        </button>

        <ng-template #menu>
          <app-user-profile-menu [avatarUrl]="avatarUrl()" />
        </ng-template>
      </div>
    </header>
  `,
})
export class AppTopbar {
  readonly avatarUrl = input<string | null>(null);

  readonly queryChange = output<string>();

  protected readonly sidebar = inject(HlmSidebarService);
  protected readonly searchOpen = signal(false);
  protected readonly query = signal('');
  protected readonly PATHS = PATHS;
  private readonly router = inject(Router);
  private readonly searchAnchor = viewChild.required<ElementRef<HTMLElement>>('searchAnchor');

  protected toggleSearch = (event: MouseEvent) => {
    event.stopPropagation();
    this.searchOpen.update((open) => !open);
  };

  protected closeSearch = () => this.searchOpen.set(false);

  protected onQuery = (event: Event) => {
    const value = (event.target as HTMLInputElement).value;
    this.query.set(value);
    this.queryChange.emit(value);
  };

  /** Enter runs the search: /search?q=<query>. */
  protected submitSearch = () => {
    const q = this.query().trim();
    if (!q) return;

    this.searchOpen.set(false);
    this.router.navigate([PATHS.search], { queryParams: { q } });
  };

  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent) {
    if (!this.searchOpen()) return;
    if (this.searchAnchor().nativeElement.contains(event.target as Node)) return;
    this.searchOpen.set(false);
  }

  @HostListener('document:keydown.escape')
  protected onEscape() {
    this.searchOpen.set(false);
  }
}
