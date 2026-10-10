/**
 * Public entry point for `@spartan-ng/helm/card`.
 *
 * Everything is re-exported from `./lib/hlm-card`, which is the single source of truth
 * for the card directives and carries the project's INKWELL customisations.
 *
 * This barrel deliberately does NOT declare its own `HlmCardImports`. An explicit
 * `export const` here would shadow the complete array exported by `./lib/hlm-card`
 * (a local export always wins over `export *`), silently reducing the import bundle to
 * just `HlmCard`. Consumers that listed `HlmCardImports` alongside separately imported
 * `HlmCardHeader`/`HlmCardFooter`/`HlmCardContent`/`HlmCardTitle` were then mixing the
 * customised card with stale duplicate definitions of the same directives.
 */
export * from './lib/hlm-card';