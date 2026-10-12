/**
 * Ambient typings for three Editor.js plugins whose own types TypeScript cannot resolve:
 *
 * - `@editorjs/link` and `@editorjs/marker` publish no .d.ts at all
 * - `@editorjs/embed` publishes types, but its package.json "exports" map hides them
 *
 * Each is an ordinary Editor.js block tool, so they are declared with the real
 * `ToolConstructable` shape rather than `any`. That keeps `blocks.insert()` and the
 * `tools` config type-checked instead of silently untyped.
 *
 * Location matters: tsconfig.app.json's include list does not pick up .d.ts files under
 * `src/app/**`, so this must live outside that tree. `declare module` is only legal in
 * ambient context, so it cannot live inside the loader .ts either.
 */
declare module '@editorjs/link' {
  const tool: import('@editorjs/editorjs').ToolConstructable;
  export default tool;
}

declare module '@editorjs/marker' {
  const tool: import('@editorjs/editorjs').ToolConstructable;
  export default tool;
}

declare module '@editorjs/embed' {
  const tool: import('@editorjs/editorjs').ToolConstructable;
  export default tool;
}