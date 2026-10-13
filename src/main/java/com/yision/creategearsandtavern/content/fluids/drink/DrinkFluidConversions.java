package com.yision.creategearsandtavern.content.fluids.drink;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.yision.creategearsandtavern.registry.CGTDataComponents;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;

public final class DrinkFluidConversions {
	private DrinkFluidConversions() {
	}

	public static FluidStack fromItem(ItemStack item) {
		return fromItem(item, CGTDrinkCatalog.SERVING_AMOUNT,
			item.getOrDefault(ModDataComponents.BREW_LEVEL.get(), 0));
	}

	public static FluidStack fromItem(ItemStack item, int amount, int brewLevel) {
		if (item.isEmpty() || amount <= 0) {
			return FluidStack.EMPTY;
		}
		CGTDrinkDefinition definition = CGTDrinkCatalog.byItem(item.getItem());
		if (definition == null) {
			return FluidStack.EMPTY;
		}
		DataComponentPatch extra = item.getComponentsPatch().forget(type -> type == ModDataComponents.BREW_LEVEL.get()
			|| type == CGTDataComponents.SHAKER_COCKTAIL_AMOUNT
			|| type == CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT
			|| type == CGTDataComponents.DRINK_ITEM_COMPONENTS
			|| definition.componentAware() && (type == ModDataComponents.SIGNATURE_COCKTAIL_EFFECTS.get()
				|| type == ModDataComponents.SIGNATURE_COCKTAIL_COLOR.get()));
		if (extra.entrySet().stream().anyMatch(entry -> entry.getKey().isTransient())) {
			return FluidStack.EMPTY;
		}
		FluidStack fluid = CGTFluids.of(definition.drinkId(), amount, brewLevel);
		if (!extra.isEmpty()) {
			fluid.set(CGTDataComponents.DRINK_ITEM_COMPONENTS, extra);
		}
		if (definition.componentAware()) {
			fluid.set(ModDataComponents.SIGNATURE_COCKTAIL_EFFECTS.get(), SignatureCocktailBlockItem.getEffects(item));
			fluid.set(ModDataComponents.SIGNATURE_COCKTAIL_COLOR.get(), SignatureCocktailBlockItem.getColor(item));
		}
		return fluid;
	}

	public static ItemStack toItem(FluidStack fluid) {
		if (fluid.isEmpty() || !KaleidoscopeDrinkFluid.hasValidIdentity(fluid)) {
			return ItemStack.EMPTY;
		}
		CGTDrinkDefinition definition = CGTFluids.definition(fluid.getFluid());
		ItemStack item = definition.item().getDefaultInstance();
		item.applyComponents(fluid.getOrDefault(CGTDataComponents.DRINK_ITEM_COMPONENTS, DataComponentPatch.EMPTY));
		if (definition.qualityAware()) {
			item.set(ModDataComponents.BREW_LEVEL.get(), KaleidoscopeDrinkFluid.variant(fluid).brewLevel());
		} else {
			item.remove(ModDataComponents.BREW_LEVEL.get());
		}
		if (definition.componentAware()) {
			var effects = fluid.get(ModDataComponents.SIGNATURE_COCKTAIL_EFFECTS.get());
			if (effects != null) {
				SignatureCocktailBlockItem.setEffects(item, effects);
			}
			Integer color = fluid.get(ModDataComponents.SIGNATURE_COCKTAIL_COLOR.get());
			if (color != null) {
				SignatureCocktailBlockItem.setColor(item, color);
			}
		}
		return item;
	}
}
