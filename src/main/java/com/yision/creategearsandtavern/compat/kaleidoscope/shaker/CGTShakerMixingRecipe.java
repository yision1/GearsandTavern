package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class CGTShakerMixingRecipe extends MixingRecipe {
    public CGTShakerMixingRecipe(ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CGTRecipeSerializers.SHAKER_MIXING.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.SHAKER.get());
    }

    @Override
    public List<ItemStack> rollResults() {
        ItemStack result = ShakerMixing.consumeCapturedMixerResult();
        return result.isEmpty() ? List.of() : List.of(result);
    }
}
