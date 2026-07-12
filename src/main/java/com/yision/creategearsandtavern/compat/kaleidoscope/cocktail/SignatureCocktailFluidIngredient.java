package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTFluids;
import com.yision.creategearsandtavern.registry.CGTIngredientTypes;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

public final class SignatureCocktailFluidIngredient extends FluidIngredient {
    public static final SignatureCocktailFluidIngredient INSTANCE = new SignatureCocktailFluidIngredient();
    public static final MapCodec<SignatureCocktailFluidIngredient> CODEC = MapCodec.unit(INSTANCE);

    private static final ThreadLocal<FluidStack> LAST_MATCHED = new ThreadLocal<>();

    private SignatureCocktailFluidIngredient() {
    }

    public static FluidStack consumeMatchedFluid() {
        FluidStack fluid = LAST_MATCHED.get();
        LAST_MATCHED.remove();
        return fluid == null ? FluidStack.EMPTY : fluid;
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        LAST_MATCHED.remove();
        if (fluidStack == null || fluidStack.isEmpty()) {
            return false;
        }
        if (!(fluidStack.getFluid() instanceof KaleidoscopeDrinkFluid)) {
            return false;
        }
        if (!CGTDrinkCatalog.isSignatureCocktail(KaleidoscopeDrinkFluid.variant(fluidStack).drinkId())) {
            return false;
        }
        LAST_MATCHED.set(fluidStack.copy());
        return true;
    }

    @Override
    protected Stream<FluidStack> generateStacks() {
        return Stream.of(CGTFluids.signatureCocktailStack(
            CGTDrinkCatalog.COCKTAIL_AMOUNT,
            List.of(),
            CGTDrinkCatalog.SIGNATURE_COCKTAIL_DEFAULT_COLOR));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public FluidIngredientType<?> getType() {
        return CGTIngredientTypes.SIGNATURE_COCKTAIL_FLUID.get();
    }

    @Override
    public int hashCode() {
        return SignatureCocktailFluidIngredient.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SignatureCocktailFluidIngredient;
    }
}
