package com.yision.creategearsandtavern.content.fluids.drink;

import com.simibubi.create.content.fluids.VirtualFluid;
import com.yision.creategearsandtavern.registry.CGTDataComponents;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class KaleidoscopeDrinkFluid extends VirtualFluid {
	public static final String QUALITY_NAME_TRANSLATION_KEY =
		"fluid.creategearsandtavern.kaleidoscope_drink.with_quality";

	public static KaleidoscopeDrinkFluid createSource(Properties properties) {
		return new KaleidoscopeDrinkFluid(properties, true);
	}

	public static KaleidoscopeDrinkFluid createFlowing(Properties properties) {
		return new KaleidoscopeDrinkFluid(properties, false);
	}

	public KaleidoscopeDrinkFluid(Properties properties, boolean source) {
		super(properties, source);
	}

	public static KaleidoscopeDrinkVariant variant(FluidStack stack) {
		ResourceLocation drinkId = resolveDrinkId(stack);
		KaleidoscopeDrinkVariant stored = stack.get(CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT);
		int level = stored != null && stored.drinkId().equals(drinkId) ? stored.brewLevel() : 0;
		return new KaleidoscopeDrinkVariant(drinkId, CGTDrinkCatalog.normalizedBrewLevel(drinkId, level));
	}

	public static boolean hasValidIdentity(FluidStack stack) {
		CGTDrinkDefinition definition = CGTFluids.definition(stack.getFluid());
		if (definition == null) {
			return false;
		}
		KaleidoscopeDrinkVariant stored = stack.get(CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT);
		return stored == null || stored.drinkId().equals(definition.drinkId());
	}

	public static ResourceLocation resolveDrinkId(FluidStack stack) {
		CGTDrinkDefinition definition = CGTFluids.definition(stack.getFluid());
		if (definition == null) {
			throw new IllegalArgumentException("Unknown drink fluid: " + stack.getFluid());
		}
		return definition.drinkId();
	}

	public static class KaleidoscopeDrinkFluidType extends FluidType {
		private final CGTDrinkDefinition definition;

		public KaleidoscopeDrinkFluidType(Properties properties, CGTDrinkDefinition definition) {
			super(properties);
			this.definition = definition;
		}

		@Override
		public Component getDescription() {
			return definition.item().getName(definition.item().getDefaultInstance());
		}

		@Override
		public Component getDescription(FluidStack stack) {
			KaleidoscopeDrinkVariant variant = variant(stack);
			Component name = getDescription();
			if (!definition.qualityAware() || variant.brewLevel() <= 0) {
				return name;
			}
			return Component.translatable(QUALITY_NAME_TRANSLATION_KEY,
				Component.translatable(variant.qualityTranslationKey()), name);
		}
	}
}
