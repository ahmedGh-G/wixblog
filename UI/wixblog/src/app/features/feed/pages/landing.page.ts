import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-landing-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="mx-auto w-full max-w-3xl">
      <h1 class="text-2xl font-semibold tracking-tight">WixBlog</h1>
      <p class="mt-1 text-sm text-muted-foreground">
        Public home for anonymous visitors. Call-to-action lives in
        <code>features/feed/components</code>.
      </p>
    </section>
  `,
  styles: [],
})
export class LandingPage {}