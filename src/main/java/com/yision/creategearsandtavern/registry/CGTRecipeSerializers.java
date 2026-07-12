package com.yision.creategearsandtavern.registry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.SignatureCocktailFillingRecipeSerializer;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerMixingRecipe;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerMixingRecipeSerializer;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CGTRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER, CreateGearsandTavern.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, CGTShakerMixingRecipeSerializer> SHAKER_MIXING =
        RECIPE_SERIALIZERS.register("shaker_mixing", CGTShakerMixingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, SignatureCocktailFillingRecipeSerializer> SIGNATURE_COCKTAIL_FILLING =
        RECIPE_SERIALIZERS.register("signature_cocktail_filling", SignatureCocktailFillingRecipeSerializer::new);
}
