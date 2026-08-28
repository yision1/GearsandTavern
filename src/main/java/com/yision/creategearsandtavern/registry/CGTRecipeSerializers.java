package com.yision.creategearsandtavern.registry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.SignatureCocktailFillingRecipe;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerMixingRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CGTRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateGearsandTavern.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CGTShakerMixingRecipe>> SHAKER_MIXING =
        RECIPE_SERIALIZERS.register("shaker_mixing",
            () -> new ProcessingRecipeSerializer<>(CGTShakerMixingRecipe::new));
    public static final RegistryObject<RecipeSerializer<SignatureCocktailFillingRecipe>> SIGNATURE_COCKTAIL_FILLING =
        RECIPE_SERIALIZERS.register("signature_cocktail_filling",
            () -> new ProcessingRecipeSerializer<>(SignatureCocktailFillingRecipe::new));

    private CGTRecipeSerializers() {
    }

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
