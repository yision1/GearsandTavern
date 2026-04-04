package com.yision.creategearsandtavern.content.fluids.drink;

import com.simibubi.create.AllFluids.TintedFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.yision.creategearsandtavern.registry.CGTDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;

public class KaleidoscopeDrinkFluid extends VirtualFluid {
    public static final String QUALITY_NAME_TRANSLATION_KEY = "fluid.creategearsandtavern.kaleidoscope_drink.with_quality";

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
        KaleidoscopeDrinkVariant stored = stack.get(CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT);
        if (stored != null) {
            return stored;
        }
        return new KaleidoscopeDrinkVariant(resolveDrinkId(stack), 0);
    }

    public static ResourceLocation resolveDrinkId(FluidStack stack) {
        String path = BuiltInRegistries.FLUID.getKey(stack.getFluid()).getPath();
        if (path.startsWith("flowing_")) {
            path = path.substring("flowing_".length());
        }
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeDrinkType.NAMESPACE, path);
    }

    public static int tintColor(KaleidoscopeDrinkVariant variant) {
        int baseColor = variant.drinkType().color();
        if (variant.brewLevel() <= 0) {
            return 0xff000000 | baseColor;
        }
        float multiplier = 0.78f + Math.min(variant.brewLevel(), 7) * 0.05f;
        int red = Math.min(255, Math.round(((baseColor >> 16) & 0xff) * multiplier));
        int green = Math.min(255, Math.round(((baseColor >> 8) & 0xff) * multiplier));
        int blue = Math.min(255, Math.round((baseColor & 0xff) * multiplier));
        return 0xff000000 | (red << 16) | (green << 8) | blue;
    }

    public static class KaleidoscopeDrinkFluidType extends TintedFluidType {
        public KaleidoscopeDrinkFluidType(net.neoforged.neoforge.fluids.FluidType.Properties properties, ResourceLocation stillTexture,
                                          ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        public Component getDescription(FluidStack stack) {
            KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(stack);
            Component drinkName = Component.translatable(variant.drinkType().translationKey());
            if (variant.brewLevel() <= 0) {
                return drinkName;
            }
            return Component.translatable(QUALITY_NAME_TRANSLATION_KEY, Component.translatable(variant.qualityTranslationKey()), drinkName);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return KaleidoscopeDrinkFluid.tintColor(KaleidoscopeDrinkFluid.variant(stack));
        }

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return NO_TINT;
        }
    }
}
