package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public record CGTDrinkDefinition(
	ResourceLocation drinkId,
	String translationKey,
	int color,
	Set<String> requiredMods
) {
	public boolean isEnabled(Set<String> loadedMods) {
		return loadedMods.containsAll(requiredMods);
	}
}
