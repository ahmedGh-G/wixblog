import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { HlmSidebarImports } from '@spartan-ng/helm/sidebar';

import { AppNavSidebar } from './app-nav-sidebar.component';
import { AppTopbar } from './app-topbar.component';

@Component({
  selector: 'app-shell',
  imports: [HlmSidebarImports, AppNavSidebar, AppTopbar],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div hlmSidebarWrapper class="flex-col">
      <app-topbar [avatarUrl]="avatarUrl()">
        <ng-content select="[appTopbarEnd]" />
      </app-topbar>

      <div class="flex w-full flex-1">
        <hlm-sidebar
          collapsible="offcanvas"
          variant="sidebar"
          side="left"
          [sidebarContainerClass]="sidebarContainerClass"
        >
          <app-nav-sidebar />
        </hlm-sidebar>

        <main hlmSidebarInset>
          <ng-content />
        </main>
      </div>
    </div>
  `,
  styles: `
    :host {
      --shell-topbar-h: 3.5rem;
    }
  `,
})
export class AppShell {
  readonly avatarUrl = input<string | null>(null);

  protected readonly sidebarContainerClass =
    'top-[var(--shell-topbar-h)] h-[calc(100dvh-var(--shell-topbar-h))]';
}
