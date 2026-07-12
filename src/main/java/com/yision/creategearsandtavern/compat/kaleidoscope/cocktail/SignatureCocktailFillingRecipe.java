package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class SignatureCocktailFillingRecipe extends FillingRecipe {
    public SignatureCocktailFillingRecipe(ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CGTRecipeSerializers.SIGNATURE_COCKTAIL_FILLING.get();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
    }

    @Override
    public List<ItemStack> rollResults(RandomSource randomSource) {
        FluidStack matchedFluid = SignatureCocktailFluidIngredient.consumeMatchedFluid();
        if (!matchedFluid.isEmpty()) {
            ItemStack cocktail = CocktailFluidConversions.toCocktailItem(matchedFluid);
            if (!cocktail.isEmpty()) {
                return List.of(cocktail);
            }
        }
        return super.rollResults(randomSource);
    }
}
