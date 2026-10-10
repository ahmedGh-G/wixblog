import {ChangeDetectionStrategy, Component, OnInit, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AppShell} from './main/app-shell.component';

/**
 * Shell for the reading + member area (routes branch ①).
 * Header / nav chrome is intentionally minimal; wire the shared layout parts here.
 */
@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, AppShell],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-shell [avatarUrl]="currentUserAvatar()">
      <!--<app-feed-promo-banner />-->

      <main aria-label="main-section">
        <router-outlet />
      </main>
    </app-shell>
    <footer class="border-t">
      <div
        class="mx-auto flex h-16 w-full max-w-6xl items-center justify-between px-4 text-sm text-muted-foreground"
      >
        <span>&copy; WixBlog</span>
      </div>
    </footer>
  `,
  styles: [],
})
export class MainLayout implements OnInit {
  protected readonly currentUserAvatar = signal<string | null>(null);

  ngOnInit() {}
}
