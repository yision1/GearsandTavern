package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SignatureCocktailFillingRecipeSerializer implements RecipeSerializer<SignatureCocktailFillingRecipe> {
    private final MapCodec<SignatureCocktailFillingRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, SignatureCocktailFillingRecipe> streamCodec;

    public SignatureCocktailFillingRecipeSerializer() {
        this.codec = ProcessingRecipe.<ProcessingRecipeParams, SignatureCocktailFillingRecipe>codec(
            SignatureCocktailFillingRecipe::new, ProcessingRecipeParams.CODEC);
        this.streamCodec = ProcessingRecipe.<ProcessingRecipeParams, SignatureCocktailFillingRecipe>streamCodec(
            SignatureCocktailFillingRecipe::new, ProcessingRecipeParams.STREAM_CODEC);
    }

    @Override
    public MapCodec<SignatureCocktailFillingRecipe> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SignatureCocktailFillingRecipe> streamCodec() {
        return streamCodec;
    }
}
