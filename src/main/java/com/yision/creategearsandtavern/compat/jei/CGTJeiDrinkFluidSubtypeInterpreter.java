package com.yision.creategearsandtavern.compat.jei;

import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;

public class CGTJeiDrinkFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {
    @Override
    public String apply(FluidStack ingredient, UidContext context) {
        ResourceLocation drinkId;
        try {
            drinkId = KaleidoscopeDrinkFluid.resolveDrinkId(ingredient);
        } catch (Exception ignored) {
            drinkId = null;
        }
        return uidOf(drinkId, ingredient.getTag());
    }

    static String uidOf(ResourceLocation drinkId, CompoundTag tag) {
        if (drinkId == null) {
            return NONE;
        }
        if (tag == null || !tag.contains("DrinkVariant", Tag.TAG_COMPOUND)) {
            return drinkId.toString();
        }
        KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkVariant.fromTag(tag.getCompound("DrinkVariant"));
        return variant.drinkId().toString();
    }
}
