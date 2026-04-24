package com.yision.creategearsandtavern.content.fluids.drink;

import com.simibubi.create.AllFluids.TintedFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class KaleidoscopeDrinkFluid extends VirtualFluid {
    public static final String QUALITY_NAME_TRANSLATION_KEY = "fluid.creategearsandtavern.kaleidoscope_drink.with_quality";

    public KaleidoscopeDrinkFluid(ForgeFlowingFluid.Properties properties, boolean source) {
        super(properties, source);
    }

    public static KaleidoscopeDrinkFluid createSource(ForgeFlowingFluid.Properties properties) {
        return new KaleidoscopeDrinkFluid(properties, true);
    }

    public static KaleidoscopeDrinkFluid createFlowing(ForgeFlowingFluid.Properties properties) {
        return new KaleidoscopeDrinkFluid(properties, false);
    }

    public static KaleidoscopeDrinkVariant variant(FluidStack fluidStack) {
        if (fluidStack.hasTag()) {
            var tag = fluidStack.getTag();
            if (tag != null && tag.contains("DrinkVariant")) {
                var vt = tag.getCompound("DrinkVariant");
                if (!vt.isEmpty()) {
                    return KaleidoscopeDrinkVariant.fromTag(vt);
                }
            }
        }
        return new KaleidoscopeDrinkVariant(resolveDrinkId(fluidStack), 0);
    }

    public static ResourceLocation resolveDrinkId(FluidStack stack) {
        String path = ForgeRegistries.FLUIDS.getKey(stack.getFluid()).getPath();
        if (path.startsWith("flowing_")) {
            path = path.substring("flowing_".length());
        }
        return CGTDrinkCatalog.byPath(path).drinkId();
    }

    public static int tintColor(KaleidoscopeDrinkVariant variant) {
        int baseColor = variant.definition().color();
        if (variant.brewLevel() <= 0) {
            return 0xff000000 | baseColor;
        }
        float multiplier = 0.78f + Math.min(variant.brewLevel(), 7) * 0.05f;
        int red = Math.min(255, Math.round(((baseColor >> 16) & 0xff) * multiplier));
        int green = Math.min(255, Math.round(((baseColor >> 8) & 0xff) * multiplier));
        int blue = Math.min(255, Math.round((baseColor & 0xff) * multiplier));
        return 0xff000000 | (red << 16) | (green << 8) | blue;
    }

    public static FluidStack setVariant(FluidStack fluidStack, KaleidoscopeDrinkVariant dv) {
        fluidStack.getOrCreateTag().put("DrinkVariant", dv.toTag());
        return fluidStack;
    }

    public static class KaleidoscopeDrinkFluidType extends TintedFluidType {
        private static final ResourceLocation STILL = new ResourceLocation("create", "fluid/potion_still");
        private static final ResourceLocation FLOWING = new ResourceLocation("create", "fluid/potion_flow");

        public KaleidoscopeDrinkFluidType(FluidType.Properties properties) {
            super(properties, STILL, FLOWING);
        }

        @Override
        public Component getDescription(FluidStack stack) {
            KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(stack);
            Component drinkName = Component.translatable(variant.definition().translationKey());
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
