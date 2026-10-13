package com.yision.creategearsandtavern.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid.KaleidoscopeDrinkFluidType;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.callback.AddCallback;

public final class CGTFluids {
	private static final Map<ResourceLocation, DrinkFluids> ENTRIES = new LinkedHashMap<>();
	private static final Map<Fluid, CGTDrinkDefinition> BY_FLUID = new IdentityHashMap<>();
	private static boolean installed;
	private static boolean complete;

	private CGTFluids() {
	}

	public static void register(IEventBus modEventBus) {
		if (installed) {
			return;
		}
		installed = true;
		BuiltInRegistries.ITEM
			.addCallback((AddCallback<Item>) (registry, rawId, key, item) -> discover(key.location(), item));
		BuiltInRegistries.ITEM.entrySet().forEach(entry -> discover(entry.getKey().location(), entry.getValue()));
		modEventBus.addListener(CGTFluids::finishRegistration);
	}

	private static void discover(ResourceLocation itemId, Item item) {
		if (complete) {
			return;
		}
		CGTDrinkDefinition definition = CGTDrinkCatalog.discover(itemId, item);
		if (definition == null) {
			return;
		}
		DrinkFluids fluids = new DrinkFluids(definition);
		ENTRIES.put(definition.drinkId(), fluids);
		BY_FLUID.put(fluids.source(), definition);
		BY_FLUID.put(fluids.flowing(), definition);
	}

	private static void finishRegistration(BlockEntityTypeAddBlocksEvent event) {
		if (complete) {
			return;
		}
		CGTDrinkCatalog.complete();
		Map<CGTDrinkDefinition, String> paths = new LinkedHashMap<>();
		CGTDrinkCatalog.allDefinitions().forEach(definition -> paths.put(definition, definition.itemId().getPath()));
		Set<CGTDrinkDefinition> qualified = new HashSet<>();
		boolean changed;
		do {
			changed = false;
			Map<String, CGTDrinkDefinition> owners = new HashMap<>();
			Set<CGTDrinkDefinition> collisions = new HashSet<>();
			for (var entry : paths.entrySet()) {
				for (String path : List.of(entry.getValue(), "flowing_" + entry.getValue())) {
					CGTDrinkDefinition previous = owners.putIfAbsent(path, entry.getKey());
					if (previous != null) {
						collisions.add(previous);
						collisions.add(entry.getKey());
					}
					if (BuiltInRegistries.FLUID.containsKey(id(path))) {
						collisions.add(entry.getKey());
					}
				}
				if (NeoForgeRegistries.FLUID_TYPES.containsKey(id(entry.getValue()))) {
					collisions.add(entry.getKey());
				}
			}
			for (CGTDrinkDefinition definition : collisions) {
				if (definition.itemId().getNamespace().equals("kaleidoscope_tavern")) {
					continue;
				}
				if (qualified.add(definition)) {
					paths.put(definition, definition.itemId().getNamespace() + "/" + definition.itemId().getPath());
					changed = true;
				}
			}
			if (!collisions.isEmpty() && !changed) {
				throw new IllegalStateException("Conflicting drink fluid ids after namespace qualification: "
					+ collisions.stream().map(CGTDrinkDefinition::itemId).sorted().toList());
			}
		} while (changed);
		for (var entry : paths.entrySet()) {
			DrinkFluids fluids = ENTRIES.get(entry.getKey().drinkId());
			String path = entry.getValue();
			Registry.register(NeoForgeRegistries.FLUID_TYPES, id(path), fluids.type());
			Registry.register(BuiltInRegistries.FLUID, id(path), fluids.source());
			Registry.register(BuiltInRegistries.FLUID, id("flowing_" + path), fluids.flowing());
			if (qualified.contains(entry.getKey())) {
				CreateGearsandTavern.LOGGER.warn("Drink {} uses {} because its fluid path conflicts",
					entry.getKey().itemId(), id(path));
			}
		}
		complete = true;
		CreateGearsandTavern.LOGGER.info("Registered {} automatically discovered drinks", ENTRIES.size());
	}

	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID, path);
	}

	public static CGTDrinkDefinition definition(Fluid fluid) {
		return BY_FLUID.get(fluid);
	}

	public static DrinkFluids getEntry(ResourceLocation drinkId) {
		DrinkFluids fluids = ENTRIES.get(drinkId);
		if (fluids == null) {
			throw new IllegalArgumentException("No fluid entry for drink id: " + drinkId);
		}
		return fluids;
	}

	public static FluidStack of(ResourceLocation drinkId, int amount, int brewLevel) {
		FluidStack stack = new FluidStack(getEntry(drinkId).source(), amount);
		stack.set(CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT,
			new KaleidoscopeDrinkVariant(drinkId, CGTDrinkCatalog.normalizedBrewLevel(drinkId, brewLevel)));
		return stack;
	}

	public static FluidStack signatureCocktailStack(int amount, List<DrinkEffectData.Entry> effects, int color) {
		FluidStack stack = of(CGTDrinkCatalog.SIGNATURE_COCKTAIL_ID, amount, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL);
		stack.set(ModDataComponents.SIGNATURE_COCKTAIL_EFFECTS.get(), List.copyOf(effects));
		stack.set(ModDataComponents.SIGNATURE_COCKTAIL_COLOR.get(), color);
		return stack;
	}

	public static final class DrinkFluids {
		private final FluidType type;
		private final KaleidoscopeDrinkFluid source;
		private final KaleidoscopeDrinkFluid flowing;

		private DrinkFluids(CGTDrinkDefinition definition) {
			type = new KaleidoscopeDrinkFluidType(FluidType.Properties.create().density(1000).viscosity(1000),
				definition);
			BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(this::type, this::source,
				this::flowing);
			source = KaleidoscopeDrinkFluid.createSource(properties);
			flowing = KaleidoscopeDrinkFluid.createFlowing(properties);
		}

		public FluidType type() {
			return type;
		}

		public KaleidoscopeDrinkFluid source() {
			return source;
		}

		public KaleidoscopeDrinkFluid flowing() {
			return flowing;
		}
	}
}
