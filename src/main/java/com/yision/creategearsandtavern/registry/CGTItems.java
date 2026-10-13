package com.yision.creategearsandtavern.registry;

import java.util.LinkedHashSet;
import java.util.Set;

import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.DrinkItemFluidHandler;

import net.minecraft.world.item.Item;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class CGTItems {
	private CGTItems() {
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		Set<Item> items = new LinkedHashSet<>();
		for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
			items.add(definition.item());
			items.add(definition.containerItem());
		}
		event.registerItem(Capabilities.FluidHandler.ITEM,
			(stack, context) -> new DrinkItemFluidHandler(stack), items.toArray(Item[]::new));
	}
}
