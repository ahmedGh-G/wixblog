import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styles: ``,
  template: `
    <!-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * -->
    <!-- * * * * * * * * * * * The content below * * * * * * * * * * * -->
    <!-- * * * * * * * * * * is only a placeholder * * * * * * * * * * -->
    <!-- * * * * * * * * * * and can be replaced.  * * * * * * * * * * -->
    <!-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * -->
    <!-- * * * * * * * * * Delete the template below * * * * * * * * * -->
    <!-- * * * * * * * to get started with your project! * * * * * * * -->
    <!-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * -->
    <!-- <h1 class="text-3xl font-bold underline">Hello world!</h1>

    <button hlmBtn variant="brand">Button</button>
    <button hlmBtn variant="secondary">Button</button>
    <button hlmBtn variant="outline">Button</button>
    <button hlmBtn variant="default" size="xs">Button</button>
    <button hlmBtn variant="brand" size="sm">Button</button>
    <button hlmBtn variant="brand" size="lg">Button</button>
    <button hlmBtn variant="brand" size="icon-lg">Button</button>-->

    <router-outlet />
  `,
})
export class App {
  protected readonly title = signal('wixblog');
}
