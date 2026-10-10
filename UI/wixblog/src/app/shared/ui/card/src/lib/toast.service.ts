import { Injectable } from '@angular/core';

/** STUB. Replace the bodies with Spartan's sonner (toast) when you generate it. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  success(message: string) { console.info('[toast:success]', message); }
  error(message: string)   { console.error('[toast:error]', message); }
}
