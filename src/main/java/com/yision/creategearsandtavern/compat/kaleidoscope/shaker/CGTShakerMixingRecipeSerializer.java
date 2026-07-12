package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CGTShakerMixingRecipeSerializer implements RecipeSerializer<CGTShakerMixingRecipe> {
    private final MapCodec<CGTShakerMixingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, CGTShakerMixingRecipe> streamCodec;

    public CGTShakerMixingRecipeSerializer() {
        this.codec = ProcessingRecipe.<ProcessingRecipeParams, CGTShakerMixingRecipe>codec(
            CGTShakerMixingRecipe::new, ProcessingRecipeParams.CODEC);
        this.streamCodec = ProcessingRecipe.<ProcessingRecipeParams, CGTShakerMixingRecipe>streamCodec(
            CGTShakerMixingRecipe::new, ProcessingRecipeParams.STREAM_CODEC);
    }

    @Override
    public MapCodec<CGTShakerMixingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CGTShakerMixingRecipe> streamCodec() {
        return streamCodec;
    }
}
