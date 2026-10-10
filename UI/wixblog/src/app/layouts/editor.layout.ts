import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/**
 * Distraction-free writing surface (routes branch ②).
 * Guarded by authGuard + permissionGuard('post:write') at the parent route.
 */
@Component({
  selector: 'app-editor-layout',
  imports: [RouterOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex min-h-dvh flex-col bg-background text-foreground">
      <header class="flex h-14 shrink-0 items-center justify-between border-b px-4">
        <span class="text-sm font-medium text-muted-foreground">Editor</span>
        <!-- EDITOR_TOOLBAR_SLOT -->
      </header>

      <main class="flex-1">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [],
})
export class EditorLayout {}