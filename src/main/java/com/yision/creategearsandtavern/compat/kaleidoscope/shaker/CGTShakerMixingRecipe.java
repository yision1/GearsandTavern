package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CGTShakerMixingRecipe extends MixingRecipe {
    public CGTShakerMixingRecipe(ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CGTRecipeSerializers.SHAKER_MIXING.get();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(ModItems.SHAKER.get());
    }

    @Override
    public List<ItemStack> rollResults(RandomSource randomSource) {
        ItemStack result = ShakerMixing.consumeCapturedMixerResult();
        return result.isEmpty() ? List.of() : List.of(result);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(RecipeInput input) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        return remainders;
    }
}
