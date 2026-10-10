/*
 * INKWELL customization of the Spartan helm card, generated from style "maia".
 * Only the lines marked // INKWELL: differ from the generated file.
 * If you generated another style, apply the same 4 edits to YOUR file.
 */
import { Directive, InjectionToken, inject, input, type ValueProvider } from '@angular/core';
import { classes } from '@spartan-ng/helm/utils';

@Directive({ selector: '[hlmCardAction]', host: { 'data-slot': 'card-action' } })
export class HlmCardAction {
	constructor() {
		classes(() => 'col-start-2 row-span-2 row-start-1 self-start justify-self-end');
	}
}

@Directive({ selector: '[hlmCardContent]', host: { 'data-slot': 'card-content' } })
export class HlmCardContent {
	constructor() {
		classes(() => 'px-(--card-spacing)');
	}
}

@Directive({ selector: '[hlmCardDescription]', host: { 'data-slot': 'card-description' } })
export class HlmCardDescription {
	constructor() {
		classes(() => 'text-muted-foreground text-sm');
	}
}

@Directive({ selector: '[hlmCardFooter],hlm-card-footer', host: { 'data-slot': 'card-footer' } })
export class HlmCardFooter {
	constructor() {
		// INKWELL (1/4): rounded-b-xl -> rounded-b-lg
		classes(() => 'rounded-b-lg px-(--card-spacing) [.border-t]:pt-(--card-spacing) flex items-center');
	}
}

@Directive({ selector: '[hlmCardHeader],hlm-card-header', host: { 'data-slot': 'card-header' } })
export class HlmCardHeader {
	constructor() {
		classes(
			() =>
				// INKWELL (2/4): rounded-t-xl -> rounded-t-lg
				'gap-2 rounded-t-lg px-(--card-spacing) [.border-b]:pb-(--card-spacing) group/card-header @container/card-header grid auto-rows-min items-start has-data-[slot=card-action]:grid-cols-[1fr_auto] has-data-[slot=card-description]:grid-rows-[auto_auto]',
		);
	}
}

@Directive({ selector: '[hlmCardTitle]', host: { 'data-slot': 'card-title' } })
export class HlmCardTitle {
	constructor() {
		// INKWELL (3/4): editorial headline weight
		classes(() => 'text-base font-semibold leading-snug tracking-tight');
	}
}

export type HlmCardConfig = { size: 'sm' | 'default' };

const defaultConfig: HlmCardConfig = { size: 'default' };
const HlmCardConfigToken = new InjectionToken<HlmCardConfig>('HlmCardConfig');

export function provideHlmCardConfig(config: Partial<HlmCardConfig>): ValueProvider {
	return { provide: HlmCardConfigToken, useValue: { ...defaultConfig, ...config } };
}

export function injectHlmCardConfig(): HlmCardConfig {
	return inject(HlmCardConfigToken, { optional: true }) ?? defaultConfig;
}

@Directive({
	selector: '[hlmCard],hlm-card',
	host: { 'data-slot': 'card', '[attr.data-size]': 'size()' },
})
export class HlmCard {
	private readonly _defaultConfig = injectHlmCardConfig();
	public readonly size = input<HlmCardConfig['size']>(this._defaultConfig.size);

	constructor() {
		// INKWELL (4/4): ring-foreground/10 -> ring-border (our token), rounded-2xl -> rounded-lg,
		// image corners rounded-t-xl/b-xl -> rounded-t-lg/b-lg. Flat: no shadow.
		classes(
			() =>
				'ring-border bg-card text-card-foreground gap-(--card-spacing) overflow-hidden rounded-lg py-(--card-spacing) text-sm ring-1 [--card-spacing:--spacing(6)] has-[>img:first-child]:pt-0 data-[size=sm]:[--card-spacing:--spacing(4)] *:[img:first-child]:rounded-t-lg *:[img:last-child]:rounded-b-lg group/card flex flex-col',
		);
	}
}

export const HlmCardImports = [
	HlmCard,
	HlmCardAction,
	HlmCardContent,
	HlmCardDescription,
	HlmCardFooter,
	HlmCardHeader,
	HlmCardTitle,
] as const;
