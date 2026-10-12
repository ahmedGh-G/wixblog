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
import { HlmSidebarImports } from '@spartan-ng/helm/sidebar';

@Component({
  selector: 'app-nav-sidebar',
  imports: [HlmSidebarImports, RouterLink, NgIcon],
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
    <div id="app-nav-sidebar" class="flex h-full flex-col" hlmSidebarContent>
      <nav hlmSidebarGroup aria-label="Main">
        <ul hlmSidebarMenu>
          @for (item of navItems; track item.label) {
            <li hlmSidebarMenuItem>
              <a
                hlmSidebarMenuButton
                routerLink="/"
                [isActive]="item.active"
                [attr.aria-current]="item.active ? 'page' : null"
              >
                <ng-icon [name]="item.icon" class="size-5" />
                <span>{{ item.label }}</span>
              </a>
            </li>
          }
        </ul>
      </nav>

      <div hlmSidebarSeparator class="my-2 border-t border-border"></div>

      <div hlmSidebarGroup class="px-4 pb-6">
        <h2 hlmSidebarGroupLabel class="h-auto px-0 text-[13px] font-normal text-muted-foreground">
          Following
        </h2>

        <a
          hlmSidebarMenuButton
          routerLink="/"
          class="mt-2 h-auto justify-start gap-3 px-0 py-1.5 text-[15px] font-normal text-muted-foreground hover:bg-transparent hover:text-foreground"
        >
          <ng-icon name="lucideUsers" class="size-5 shrink-0" />
          <span>Following</span>
        </a>

        <a
          hlmSidebarMenuButton
          routerLink="/"
          class="h-auto justify-start gap-3 px-0 py-1.5 text-[15px] font-normal text-muted-foreground hover:bg-transparent hover:text-foreground"
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
    </div>
  `,
})
export class AppNavSidebar {
  protected readonly navItems = [
    { label: 'Home', icon: 'lucideHouse', active: true },
    { label: 'Library', icon: 'lucideLibrary', active: false },
    { label: 'Profile', icon: 'lucideUser', active: false },
    { label: 'Stories', icon: 'lucideFileText', active: false },
    { label: 'Stats', icon: 'lucideChartColumn', active: false },
  ] as const;
}
