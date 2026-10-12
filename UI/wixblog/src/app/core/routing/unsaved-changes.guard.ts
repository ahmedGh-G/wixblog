import type { CanDeactivateFn } from '@angular/router';

/**
 * Any component guarded by this must expose a `hasUnsavedChanges()` signal.
 * STUB: always permits navigation.
 */
export interface UnsavedChangesAware {
  hasUnsavedChanges(): boolean;
}

/**
 * STUB: always permits so navigation works during scaffolding.
 * WIRING: `return component.hasUnsavedChanges() ? confirm(...) : true;`
 */
export const unsavedChangesGuard: CanDeactivateFn<UnsavedChangesAware> = () => true;
