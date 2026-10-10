# OpenCode Engineering Conventions

## 1. Architectural Mandates

- **Component Architecture:** Every single Angular component generated must be 100% standalone.
- **Inline Templates & Styles:** Do NOT generate external `.html` or `.scss` files for components. Everything must use inline strings:
  - Use `template: \`...\`` for templates.
  - Use `styles: [\`...\`]` for component-specific styles when necessary, heavily leaning on Tailwind CSS.
- **Change Detection:** Enforce `changeDetection: ChangeDetectionStrategy.OnPush` across all generated components.

## 2. Directory Layout Architecture

When implementing any feature under `src/app/features/`, you must enforce the following sub-folder isolation matrix:

- `[feature].routes.ts` (Feature specific routing file)
- `pages/` (High-level route-bound page views)
- `components/` (Feature-isolated presentation views)
- `data/` (Signals stores, state managers, custom models, and DTO templates)
- `services/` (HTTP Client consumer wrappers for downstream API operations)
- `directives/` (Feature-scoped attribute or structural directives)
- `pipes/` (Feature-scoped data transform elements)

## 3. Scope Restriction Guardrails

- **No Heavy Scans:** Do not execute automated recursive directory scans (`Get-ChildItem` or deep folder loops) as it causes terminal runtime crashes.
- **Strict Adherence:** Treat commented out pathways inside `app.routes.ts` and `paths.ts` as forbidden zones. Do not invent or scaffold them.
