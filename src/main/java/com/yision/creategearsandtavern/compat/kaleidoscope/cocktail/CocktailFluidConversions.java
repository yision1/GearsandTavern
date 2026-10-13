package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition.ServingKind;
import com.yision.creategearsandtavern.content.fluids.drink.DrinkFluidConversions;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CocktailFluidConversions {
	private CocktailFluidConversions() {
	}

	public static FluidStack fromResult(ItemStack result) {
		CGTDrinkDefinition definition = CGTDrinkCatalog.byItem(result.getItem());
		if (definition == null || definition.servingKind() != ServingKind.COCKTAIL_GLASS) {
			return FluidStack.EMPTY;
		}
		return DrinkFluidConversions.fromItem(result);
	}

	public static FluidStack fromShakerResult(ItemStack result, ItemStackHandler storage) {
		if (result.getItem() instanceof SignatureCocktailBlockItem
			&& !SignatureCocktailBlockItem.hasEffects(result) && storage != null) {
			CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(storage);
			ItemStack completed = result.copy();
			SignatureCocktailBlockItem.setEffects(completed, CocktailEffectHelper.mergeEffects(data.effects()));
			SignatureCocktailBlockItem.setColor(completed, ColorUtils.mixColors(data.colors()));
			return fromResult(completed);
		}
		return fromResult(result);
	}

	public static ItemStack toCocktailItem(FluidStack fluid) {
		CGTDrinkDefinition definition = CGTFluids.definition(fluid.getFluid());
		if (definition == null || definition.servingKind() != ServingKind.COCKTAIL_GLASS) {
			return ItemStack.EMPTY;
		}
		return DrinkFluidConversions.toItem(fluid);
	}
}
