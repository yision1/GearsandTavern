package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.item.CocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.DrinkBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.MolotovBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition.Kind;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class CGTDrinkCatalog {
	public static final ResourceLocation SIGNATURE_COCKTAIL_ID = ResourceLocation
		.fromNamespaceAndPath("creategearsandtavern", "signature_cocktail");
	public static final int LEVELLESS_BREW_LEVEL = 0;
	public static final int SERVING_AMOUNT = 250;
	public static final int COCKTAIL_AMOUNT = SERVING_AMOUNT;

	private static final Map<ResourceLocation, CGTDrinkDefinition> BY_ID = new LinkedHashMap<>();
	private static final Map<Item, CGTDrinkDefinition> BY_ITEM = new IdentityHashMap<>();
	private static List<CGTDrinkDefinition> definitions = List.of();
	private static boolean complete;

	private CGTDrinkCatalog() {
	}

	public static CGTDrinkDefinition discover(ResourceLocation itemId, Item item) {
		if (complete || BY_ITEM.containsKey(item)) {
			return null;
		}
		Kind kind;
		if (item instanceof SignatureCocktailBlockItem) {
			kind = Kind.SIGNATURE_COCKTAIL;
		} else if (item instanceof CocktailBlockItem) {
			kind = Kind.COCKTAIL;
		} else if (item instanceof DrinkBlockItem) {
			kind = Kind.DRINK;
		} else if (item instanceof MolotovBlockItem) {
			kind = Kind.MOLOTOV;
		} else {
			return null;
		}
		ResourceLocation drinkId = itemId;
		if (itemId.equals(ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "signature_cocktail"))) {
			drinkId = SIGNATURE_COCKTAIL_ID;
		}
		CGTDrinkDefinition definition = new CGTDrinkDefinition(itemId, drinkId, item, kind);
		if (BY_ID.putIfAbsent(drinkId, definition) != null) {
			throw new IllegalStateException("Duplicate drink identity: " + drinkId + " for " + itemId);
		}
		BY_ITEM.put(item, definition);
		return definition;
	}

	public static void complete() {
		if (complete) {
			return;
		}
		definitions = BY_ID.values().stream()
			.sorted(Comparator.comparing(definition -> definition.itemId().toString()))
			.toList();
		for (CGTDrinkDefinition definition : definitions) {
			if (definition.containerItem() == null || definition.containerItem() == Items.AIR) {
				throw new IllegalStateException("Missing drink container: " + definition.itemId());
			}
		}
		complete = true;
	}

	public static List<CGTDrinkDefinition> allDefinitions() {
		return complete ? definitions : List.copyOf(BY_ID.values());
	}

	public static CGTDrinkDefinition byItem(Item item) {
		return BY_ITEM.get(item);
	}

	public static CGTDrinkDefinition byDrinkId(ResourceLocation id) {
		CGTDrinkDefinition definition = BY_ID.get(id);
		if (definition == null) {
			throw new IllegalArgumentException("Unknown drink id: " + id);
		}
		return definition;
	}

	public static boolean hasDrinkId(ResourceLocation id) {
		return BY_ID.containsKey(id);
	}

	public static int normalizedBrewLevel(ResourceLocation id, int brewLevel) {
		return byDrinkId(id).qualityAware()
			? Math.clamp(brewLevel, IBarrel.BREWING_NOT_STARTED, IBarrel.BREWING_FINISHED)
			: LEVELLESS_BREW_LEVEL;
	}

	public static boolean isSignatureCocktail(ResourceLocation id) {
		CGTDrinkDefinition definition = BY_ID.get(id);
		return definition != null && definition.componentAware();
	}
}
