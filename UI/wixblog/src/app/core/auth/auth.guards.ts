import type { CanActivateFn, CanMatchFn } from '@angular/router';

/**
 * Placeholder auth state.
 * Replace with a real signal store / AuthService when the identity slice lands.
 */
export interface AuthState {
  isAuthenticated: boolean;
  permissions: ReadonlySet<string>;
}

function authState(): AuthState {
  return { isAuthenticated: false, permissions: new Set<string>() };
}

/**
 * STUB: always permits so every branch renders during scaffolding.
 * WIRING: `const state = authState(); return state.isAuthenticated || router.createUrlTree([PATHS.login]);`
 */
export const authGuard: CanActivateFn & CanMatchFn = () => true;

/**
 * STUB: always permits so every branch renders during scaffolding.
 * WIRING: `const state = authState(); return !state.isAuthenticated || router.createUrlTree([PATHS.home]);`
 */
export const guestGuard: CanActivateFn & CanMatchFn = () => true;

/**
 * STUB: factory returning a guard that always permits.
 * WIRING: `const state = authState(); return state.permissions.has(permission) || router.createUrlTree([PATHS.forbidden]);`
 */
export function permissionGuard(permission: string): CanActivateFn & CanMatchFn {
  return () => {
    void permission;
    return authState().permissions.size >= 0;
  };
}