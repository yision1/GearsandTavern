package com.yision.creategearsandtavern.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.registry.CGTFluids;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CGTFillingRecipeGen extends FillingRecipeGen {
    public CGTFillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateGearsandTavern.MOD_ID);

        for (KaleidoscopeDrinkType drinkType : KaleidoscopeDrinkType.values()) {
            Item drinkItem = BuiltInRegistries.ITEM.get(drinkType.id());
            for (int level = IBarrel.BREWING_STARTED; level <= IBarrel.BREWING_FINISHED; level++) {
                int finalLevel = level;
                create(drinkType.id().getPath() + "_bottle_quality_" + level, b -> b.require(ModItems.EMPTY_BOTTLE.get())
                    .require(new SizedFluidIngredient(DataComponentFluidIngredient.of(false, CGTFluids.of(drinkType, 250, finalLevel)), 250))
                    .output(CGTDrinkRecipeGenHelper.bottleWithBrewLevel(drinkItem, finalLevel)));
            }
        }
    }
}
