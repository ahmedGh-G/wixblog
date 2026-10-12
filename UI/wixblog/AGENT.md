# OpenCode Engineering Conventions

These rules apply in **both Plan mode and Build mode**. Follow them exactly. If a rule conflicts with your own judgment, follow the rule and ask the user (see section 6).

## 1. Architectural Mandates

- **Component Architecture:** Every single Angular component generated must be 100% standalone.
- **Inline Templates & Styles:** Do NOT generate external `.html` or `.scss` files for components. Everything must use inline strings:
  - Use `template: \`...\`` for templates.
  - Use `styles: [\`...\`]` for component-specific styles when necessary, heavily leaning on Tailwind CSS.
- **Change Detection:** Enforce `changeDetection: ChangeDetectionStrategy.OnPush` across all generated components.
- **Code Comments (crucial):** Comment **only the important code blocks**. Do not add comments to blocks that do not need them. A comment must explain **why**, never restate **what** the code already says.
  - **Comment these:**
    - non-obvious business rules or invariants (for example, "only PUBLISHED posts accept comments"),
    - workarounds, hacks, or browser or library quirks, with the reason,
    - performance, security, or race-condition decisions,
    - complex algorithms, regular expressions, or non-trivial computed logic,
    - the purpose of a shared component's `input()` or `output()` only when it is not obvious from the name.
  - **Never comment these:**
    - imports, decorators, or standard component boilerplate,
    - simple getters, setters, assignments, or one-line methods,
    - template markup, Tailwind classes, or section dividers,
    - code that is already self-explanatory from clear naming,
    - closing braces, change logs, or commented-out code.
  - **Style:** one short line is preferred. No JSDoc blocks on every function. Do not add or remove comments in code you are not otherwise changing.
  - The visible placeholder labels and `data-section` attributes from the page pipeline (section 4) are **not** comments and must still be added.

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

- **No Heavy Scans:** Do not execute automated recursive directory scans (`Get-ChildItem` or deep folder loops) as it causes terminal runtime crashes. List one folder level at a time, or open the exact file you need.
- **Strict Adherence:** Treat commented out pathways inside `app.routes.ts` and `paths.ts` as forbidden zones. Do not invent or scaffold them.

## 4. New Page Pipeline (mandatory for every new page)

Every new page is built in **three phases, in this order**. Never skip ahead.

```
Phase 1  Layout skeleton  ->  STOP: user checks it visually  ->  Phase 2  Stacked sections  ->  Phase 3  Content + components
```

### Placeholder visibility rule (applies to Phase 1 AND Phase 2, every page)

The whole point of the placeholder phases is that the user can **visually verify the layout**
before real content exists. Therefore, for **every** page, without exception:

- **Every placeholder section must be visibly tinted.** Never leave a section transparent or
  unstyled. The user must be able to see exactly where each block begins and ends.
- **Every column gets its own distinct tint.** The main column and the sidebar column never
  share a background, so the column split and the divider position are obvious.
- **Stacked sections alternate tints** so vertically adjacent blocks are distinguishable from
  each other, not just from their column neighbours.
- **Tint palette:** cycle through `bg-muted` → `bg-secondary` → `bg-card`, in that order,
  wrapping around. Use a distinct tint for column 1 vs column 2 first, then continue the cycle
  for the sections inside each column.
- **Every placeholder section also keeps** a dashed `border border-dashed border-border`, its
  name as visible text, and its `data-section="<name>"` attribute.

Never ask whether to apply this. Apply it automatically on every page.

### Phase 1: Main layout skeleton (columns only)

1. **Choose the column structure** from the reference screenshots:

- **1 column (reading layout):** `docs/reference-layouts/story-details-fullpage.png`
  One centered, narrow column (about 680px wide) with generous empty space on both sides. Used for story reading, editor, settings forms.
- **2 columns (list + sidebar layout):** `docs/reference-layouts/search-results-page.png`
  A wide main column on the left plus a narrower sidebar column on the right (about one third of the width), separated by a thin vertical divider. Used for search, feed, topic and tag pages.
- If the page does not clearly fit one of these, **ask the user** which one to use (section 6). Do not guess.

2. **Build only the empty structure.** In this phase the page contains:

- the page component (`pages/<name>.page.ts`, standalone, inline template, OnPush),
- one wrapper container,
- one `<section>` per column.
  Do **not** add real content, nested components, stores, services or HTTP calls yet.

3. **Make every section visible and named.** Each section must:

- have a **light background color** using a semantic token (`bg-muted`, `bg-secondary` or `bg-card`) plus a dashed border (`border border-dashed border-border`), so the sections can be told apart,
- show its **name as visible text** inside it,
- carry a `data-section="<name>"` attribute.

Use these exact names:

- 1 column: `Main section`
- 2 columns: `Column 1 section (main)` and `Column 2 section (sidebar)`

4. **Responsive behavior:** two columns collapse into one below the `lg` breakpoint (sidebar stacks below or hides, as the user prefers; ask if unsure).
5. **Never use hardcoded hex colors.** Use the design tokens only (`bg-*`, `text-*`, `border-*` semantic utilities).
6. **Placeholder tinting is not optional and is never up for discussion.** Every section must be
   visibly tinted per the "Placeholder visibility rule" above, so the user can preview and verify
   the layout. Do not ask whether to apply it.
7. **STOP HERE.** Tell the user:

- which file(s) you created,
- the URL to open (only if its route is already active, see section 3),
- the list of section names you created.

Then **wait for the user's explicit approval** (or change requests). Do not start Phase 2 in the same turn.

Reference skeleton for the 2-column case (adapt names and widths as needed):

```ts
@Component({
  selector: 'app-search-page',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="mx-auto grid max-w-6xl gap-0 px-4 lg:grid-cols-[minmax(0,1fr)_22rem]">
      <section data-section="Column 1 section (main)"
               class="min-h-96 border border-dashed border-border bg-muted p-6 lg:pe-10">
        Column 1 section (main)
      </section>
      <section data-section="Column 2 section (sidebar)"
               class="min-h-96 border border-dashed border-border bg-secondary p-6 lg:border-s">
        Column 2 section (sidebar)
      </section>
    </div>
  `,
})
export class SearchPage {}
```

### Phase 2: Stacked sections

After the user approves the layout:

1. **Dissect each column into stacked sections**, vertically (one under another) or horizontally (side by side inside a row). Think in blocks: for example a title section, a tabs section, a results list section, a "show more" section in the main column, and a "topics", "people" and "publications" section in the sidebar.
2. **Give every section the same treatment as in Phase 1:** a light background token color, a dashed border, its name as visible text, and a `data-section` attribute. Use slightly different light tints so neighbors are distinguishable.
3. **Post the section tree in your reply** (column, then its sections, in order). Continue to Phase 3 unless something is uncertain. If it is, ask first (section 6).

### Phase 3: Content and nested components

For each section, from top to bottom:

1. **List what the section needs** (buttons, inputs, tabs, cards, avatars, badges, menus, dialogs, tables, skeletons) before writing any markup.
2. **Check for existing reusable components, in this order. This is crucial:**
1. **`@shared/ui`**: the Spartan UI primitives we own (button, card, input, label, dropdown-menu, dialog, table, badge, tabs and so on). List `src/app/shared/ui` one level deep, or read its index file.
2. **`shared/components`**: domain composites (for example `PostCard`, `PostMeta`, `StatusBadge`, `PageHeader`, `DataTable`, `RowActionsMenu`, `EmptyState`).
3. **The feature's own `components/` folder.**
3. **Always reuse what exists.**

- Never hand-write a button, input, dialog, dropdown, tab, badge or card with raw HTML and ad hoc classes when a shared one exists.
- Never import from `@spartan-ng/brain/*` inside features. Import from `@shared/ui` or `@spartan-ng/helm/*` only.
- Never hardcode colors, radii or fonts. Use the design tokens.

4. **If nothing suitable exists, do not create it silently.** Stop and ask the user where it should live (`shared/ui` through the Spartan CLI, `shared/components`, or the feature's `components/`), and wait for the answer (section 6).
5. **Smart and dumb split:**

- Pages are smart: they inject the store or service and pass data down.
- Nested components are dumb: `input()` and `output()` only, no HTTP, no store injection.

6. **Replace each placeholder section with real content one section at a time.** When a section receives its real content, remove its placeholder label, dashed border and debug tint, but keep the structure and the `data-section` attribute.
7. **Every data-driven section needs loading, empty and error states.**

## 5. Page completion

When all three phases are done:

1. Summarize what was built (files and sections).
2. List the shared components you reused, and any you wanted but did not find.
3. Ask every remaining question (section 6).
4. **Do not start the next page** until the user says so.

## 6. Ask Before Acting (applies in Plan mode AND Build mode)

**Never guess. If a step is uncertain, ask first and wait for the answer before doing the work.**

Always ask when:

- the column structure is unclear (1 column or 2),
- a needed shared component does not exist,
- the route or URL is not active in `app.routes.ts` or `paths.ts`,
- the data shape, API endpoint, or store design is unknown,
- naming is ambiguous,
- something is not covered by this file,
- a change would touch files outside the current feature, or rename or delete anything.

How to ask:

- Ask **numbered, concise questions**. For each, give the options and your **recommended default**.
- **Plan mode:** end the plan with an "Open questions" list. Do not treat the plan as final until they are answered.
- **Build mode:** stop before the uncertain step. Do not edit files in the same turn as the question.
- Do not ask about things this file already decides.
