import type { EditorConfig, ToolConstructable } from '@editorjs/editorjs';

/**
 * `embed`, `link` and `marker` publish types TypeScript cannot resolve: link and marker
 * ship no .d.ts, and embed hides its types behind its package.json "exports" map.
 * Their imports are therefore cast to the constructor shape the tools config expects.
 */
type EditorTool = ToolConstructable;

/**
 * Every Editor.js plugin, loaded on demand.
 *
 * These MUST be dynamic imports. Each plugin touches `window` at module-evaluation
 * time, so a single static import anywhere in this graph throws
 * `ReferenceError: window is not defined` during SSR and takes down every route in
 * the app — not just the editor. `new-story.page.ts` already defers Editor.js itself
 * the same way for exactly this reason.
 */
export async function loadEditorTools(): Promise<EditorConfig['tools']> {
  const [
    header,
    list,
    quote,
    image,
    delimiter,
    embed,
    table,
    link,
    marker,
    inlineCode,
    code,
    warning,
    underline,
  ] = await Promise.all([
    import('@editorjs/header'),
    import('@editorjs/list'),
    import('@editorjs/quote'),
    import('@editorjs/image'),
    import('@editorjs/delimiter'),
    import('@editorjs/embed') as Promise<{ default: EditorTool }>,
    import('@editorjs/table'),
    import('@editorjs/link') as Promise<{ default: EditorTool }>,
    import('@editorjs/marker') as Promise<{ default: EditorTool }>,
    import('@editorjs/inline-code'),
    import('@editorjs/code'),
    import('@editorjs/warning'),
    import('@editorjs/underline'),
  ]);

  // Every plugin is a default export; the namespace object itself is not a BlockTool.
  return {
    header: header.default,
    list: list.default,
    quote: quote.default,
    image: image.default,
    delimiter: delimiter.default,
    embed: embed.default,
    table: table.default,
    link: link.default,
    marker: marker.default,
    inlineCode: inlineCode.default,
    code: code.default,
    warning: warning.default,
    underline: underline.default,
  };
}

/** Block types this story editor allows, in the order the plus-menu lists them. */
export const EDITOR_BLOCK_TYPES = [
  'header',
  'list',
  'quote',
  'image',
  'delimiter',
  'embed',
  'table',
  'code',
  'warning',
] as const;
