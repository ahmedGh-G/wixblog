import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideCircleHelp, lucideContrast, lucideSettings } from '@ng-icons/lucide';
import { Router, RouterLink } from '@angular/router';
import { HlmAvatarImports } from '@spartan-ng/helm/avatar';
import { HlmDropdownMenuImports } from '@spartan-ng/helm/dropdown-menu';

import { PATHS } from '@shared/paths';

@Component({
  selector: 'app-user-profile-menu',
  imports: [RouterLink, NgIcon, HlmAvatarImports, HlmDropdownMenuImports],
  providers: [provideIcons({ lucideContrast, lucideSettings, lucideCircleHelp })],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div hlmDropdownMenu class="min-w-[17.5rem] rounded-2xl p-0" align="end" sideOffset="8">
      <div class="flex items-center gap-3 px-4 py-4">
        <hlm-avatar size="lg" class="shrink-0">
          @if (avatarUrl(); as src) {
            <img hlmAvatarImage [src]="src" alt="" />
          } @else {
            <span hlmAvatarFallback class="text-sm">{{ initial() }}</span>
          }
        </hlm-avatar>

        <div class="min-w-0">
          <p class="truncate text-sm font-semibold">{{ name() }}</p>
          <a
            [routerLink]="profilePath()"
            class="text-muted-foreground hover:text-foreground text-sm transition-colors"
          >
            View profile
          </a>
        </div>
      </div>

      <div class="bg-foreground h-[3px] w-full"></div>

      <div class="p-1.5">
        <a hlmDropdownMenuItem [routerLink]="settingsPath()">
          <ng-icon name="lucideContrast" aria-hidden="true" />
          <span class="flex-1">Appearance</span>
          <span
            class="bg-secondary text-secondary-foreground rounded px-1.5 py-0.5 text-[10px] font-semibold tracking-wide uppercase"
          >
            Beta
          </span>
        </a>

        <a hlmDropdownMenuItem [routerLink]="settingsPath()">
          <ng-icon name="lucideSettings" aria-hidden="true" />
          Settings
        </a>

        <button hlmDropdownMenuItem type="button">
          <ng-icon name="lucideCircleHelp" aria-hidden="true" />
          Help
        </button>
      </div>

      <div hlmDropdownMenuSeparator class="bg-border/70 -mx-1 my-1"></div>

      <div class="p-1.5">
        <button hlmDropdownMenuItem type="button" class="h-auto flex-col items-start gap-0 py-3">
          <span class="text-sm font-normal">Sign out</span>
          <span class="text-muted-foreground text-xs">{{ email() }}</span>
        </button>
      </div>

      <div hlmDropdownMenuSeparator class="bg-border/70 -mx-1 my-1"></div>

      <div class="flex flex-wrap gap-x-3 gap-y-1 px-4 py-3">
        @for (link of footerLinks; track link.label) {
          <a
            [routerLink]="link.path"
            class="text-muted-foreground hover:text-foreground text-xs transition-colors"
          >
            {{ link.label }}
          </a>
        }
        <span class="text-muted-foreground text-xs">More</span>
      </div>
    </div>
  `,
  styles: [],
})
export class UserProfileMenu {
  readonly avatarUrl = input<string | null>(null);
  readonly name = input('Ahmed Ghorbel');
  readonly email = input('ahmedghorbel@gmail.com');
  readonly username = input('ahmedghorbel');

  private readonly router = inject(Router);

  protected readonly initial = () => this.name().charAt(0).toUpperCase();

  protected profilePath = () => PATHS.profile(this.username());
  protected settingsPath = () => PATHS.settings;

  protected readonly footerLinks = [
    { label: 'About', path: PATHS.about },
    { label: 'Blog', path: PATHS.home },
    { label: 'Careers', path: PATHS.home },
    { label: 'Privacy', path: PATHS.privacy },
    { label: 'Terms', path: PATHS.terms },
  ];
}
