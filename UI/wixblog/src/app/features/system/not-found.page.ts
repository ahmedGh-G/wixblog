import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto flex w-full max-w-3xl flex-col items-center gap-4 py-20 text-center">
      <p class="text-5xl font-semibold tracking-tight">404</p>
      <h1 class="text-xl font-medium">Page not found</h1>
      <p class="max-w-md text-sm text-muted-foreground">
        The page you are looking for does not exist or has been moved.
      </p>
      <a routerLink="/" class="text-sm font-medium underline underline-offset-4"> Back to home </a>
    </section>
  `,
  styles: [],
})
export class NotFoundPage {}
