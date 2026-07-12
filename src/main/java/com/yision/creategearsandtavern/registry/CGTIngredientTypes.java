package com.yision.creategearsandtavern.registry;

import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.SignatureCocktailFluidIngredient;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.ReadyShakerIngredient;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CGTIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, CreateGearsandTavern.MOD_ID);
    public static final DeferredRegister<FluidIngredientType<?>> FLUID_INGREDIENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_INGREDIENT_TYPES, CreateGearsandTavern.MOD_ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<ReadyShakerIngredient>> READY_SHAKER =
        INGREDIENT_TYPES.register("ready_shaker", () -> new IngredientType<>(ReadyShakerIngredient.CODEC));
    public static final DeferredHolder<FluidIngredientType<?>, FluidIngredientType<SignatureCocktailFluidIngredient>> SIGNATURE_COCKTAIL_FLUID =
        FLUID_INGREDIENT_TYPES.register("signature_cocktail_fluid",
            () -> new FluidIngredientType<>(SignatureCocktailFluidIngredient.CODEC));
}
