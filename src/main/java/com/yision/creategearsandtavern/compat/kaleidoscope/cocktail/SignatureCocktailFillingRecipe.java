package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class SignatureCocktailFillingRecipe extends FillingRecipe {
    public SignatureCocktailFillingRecipe(ProcessingRecipeParams params) {
        super(params);
        this.fluidIngredients = NonNullList.of(FluidIngredient.EMPTY, new SignatureCocktailFluidIngredient());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CGTRecipeSerializers.SIGNATURE_COCKTAIL_FILLING.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
    }

    @Override
    public List<ItemStack> rollResults() {
        ItemStack result = CocktailFluidConversions.toCocktailItem(
            SignatureCocktailFluidIngredient.consumeMatchedFluid());
        return result.isEmpty() ? List.of() : List.of(result);
    }
}
