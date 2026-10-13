package com.yision.creategearsandtavern.content.fluids.drink;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.IHasContainer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record CGTDrinkDefinition(ResourceLocation itemId, ResourceLocation drinkId, Item item, Kind kind) {

	public boolean qualityAware() {
		return kind == Kind.DRINK;
	}

	public boolean componentAware() {
		return kind == Kind.SIGNATURE_COCKTAIL;
	}

	public ServingKind servingKind() {
		return kind == Kind.COCKTAIL || componentAware() ? ServingKind.COCKTAIL_GLASS : ServingKind.BOTTLE;
	}

	public int servingAmount() {
		return CGTDrinkCatalog.SERVING_AMOUNT;
	}

	public Item containerItem() {
		if (item instanceof IHasContainer container) {
			return container.getContainerItem();
		}
		return ModItems.EMPTY_BOTTLE.get();
	}

	public enum Kind {
		DRINK,
		COCKTAIL,
		SIGNATURE_COCKTAIL,
		MOLOTOV
	}

	public enum ServingKind {
		BOTTLE,
		COCKTAIL_GLASS
	}
}
