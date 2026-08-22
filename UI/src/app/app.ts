import {Component, computed, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {LucideCircleCheck, LucideCircleX, LucideDynamicIcon, LucideHouse} from '@lucide/angular';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, LucideDynamicIcon, LucideHouse],
  template: `
    <div class="min-h-screen bg-slate-50 flex items-center justify-center">
      <div class="bg-white p-8 rounded-2xl shadow-xl max-w-md text-center border border-slate-100">
        <h1 class="text-3xl font-extrabold text-indigo-600 tracking-tight">
          WixBlog Engine Live
        </h1>
        <p class="text-slate-500 mt-2 font-medium">
          Angular 20 + Tailwind v4 are successfully configured!
        </p>
        <button class="mt-6 px-6 py-2.5 bg-indigo-600 text-white font-semibold rounded-xl shadow-md hover:bg-indigo-700 transition duration-200">
          Explore Platform
        </button>
      </div>
    </div>


    <svg lucideFileText></svg>
    <svg [lucideIcon]="icon()">heelo</svg>

    <svg lucideHouse [size]="48" color="red" [strokeWidth]="2" title="Home"></svg>

  `,
  styles:``
})
export class App {
  protected readonly title = signal('wixblog');

  protected readonly model = signal<boolean>(true);
  protected readonly icon = computed(() => this.model() ? LucideCircleCheck : LucideCircleX);
}
