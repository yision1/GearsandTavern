package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public record CGTDrinkDefinition(
	ResourceLocation drinkId,
	String translationKey,
	int color,
	Set<String> requiredMods,
	ServingKind servingKind,
	int servingAmount,
	boolean qualityAware,
	TransferMode transferMode
) {
	public CGTDrinkDefinition(
		ResourceLocation drinkId,
		String translationKey,
		int color,
		Set<String> requiredMods
	) {
		this(drinkId, translationKey, color, requiredMods, ServingKind.BOTTLE, 250, true, TransferMode.STATIC_RECIPE);
	}

	public boolean isEnabled(Set<String> loadedMods) {
		return loadedMods.containsAll(requiredMods);
	}

	public enum ServingKind {
		BOTTLE,
		COCKTAIL_GLASS
	}

	public enum TransferMode {
		STATIC_RECIPE,
		COMPONENT_AWARE
	}
}
