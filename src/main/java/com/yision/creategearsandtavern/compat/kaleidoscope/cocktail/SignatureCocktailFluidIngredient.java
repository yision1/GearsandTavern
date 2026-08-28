package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class SignatureCocktailFluidIngredient extends FluidIngredient {
    private static final ThreadLocal<FluidStack> LAST_MATCHED = new ThreadLocal<>();

    public SignatureCocktailFluidIngredient() {
        amountRequired = CGTDrinkCatalog.COCKTAIL_AMOUNT;
    }

    public static FluidStack consumeMatchedFluid() {
        FluidStack fluid = LAST_MATCHED.get();
        LAST_MATCHED.remove();
        return fluid == null ? FluidStack.EMPTY : fluid;
    }

    @Override
    protected boolean testInternal(FluidStack fluidStack) {
        LAST_MATCHED.remove();
        if (fluidStack == null || fluidStack.isEmpty() || fluidStack.getAmount() < amountRequired
            || !(fluidStack.getFluid() instanceof KaleidoscopeDrinkFluid)
            || !CGTDrinkCatalog.isSignatureCocktail(KaleidoscopeDrinkFluid.variant(fluidStack).drinkId())) {
            return false;
        }
        LAST_MATCHED.set(fluidStack.copy());
        return true;
    }

    @Override
    protected void readInternal(FriendlyByteBuf buffer) {
        buffer.readRegistryId();
        buffer.readNbt();
    }

    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {
        buffer.writeRegistryId(ForgeRegistries.FLUIDS,
            CGTFluids.getEntry(CGTDrinkCatalog.SIGNATURE_COCKTAIL_ID).get().getSource());
        buffer.writeNbt(new CompoundTag());
    }

    @Override
    protected void readInternal(JsonObject json) {
    }

    @Override
    protected void writeInternal(JsonObject json) {
        json.addProperty("fluid", new ResourceLocation("creategearsandtavern", "signature_cocktail").toString());
        json.add("nbt", new com.google.gson.JsonObject());
    }

    @Override
    protected List<FluidStack> determineMatchingFluidStacks() {
        return List.of(CGTFluids.signatureCocktailStack(
            CGTDrinkCatalog.COCKTAIL_AMOUNT, List.of(), CGTDrinkCatalog.SIGNATURE_COCKTAIL_DEFAULT_COLOR));
    }
}
