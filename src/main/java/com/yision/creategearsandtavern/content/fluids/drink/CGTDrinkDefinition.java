package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.List;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public record CGTDrinkDefinition(
	ResourceLocation drinkId,
	String translationKey,
	int color,
	List<String> requiredMods
) {
	public boolean isEnabled(Set<String> loadedMods) {
		return loadedMods.containsAll(requiredMods);
	}
}
