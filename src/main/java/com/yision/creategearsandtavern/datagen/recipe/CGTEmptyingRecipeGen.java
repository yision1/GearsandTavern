package com.yision.creategearsandtavern.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.data.recipe.EmptyingRecipeGen;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public class CGTEmptyingRecipeGen extends EmptyingRecipeGen {
    public CGTEmptyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateGearsandTavern.MOD_ID);

        for (KaleidoscopeDrinkType drinkType : KaleidoscopeDrinkType.values()) {
            Item drinkItem = BuiltInRegistries.ITEM.get(drinkType.id());
            for (int level = IBarrel.BREWING_STARTED; level <= IBarrel.BREWING_FINISHED; level++) {
                int finalLevel = level;
                create(drinkType.id().getPath() + "_bottle_quality_" + level, b -> b.require(DataComponentIngredient.of(false, CGTDrinkRecipeGenHelper.bottleWithBrewLevel(drinkItem, finalLevel)))
                    .output(CGTFluids.of(drinkType, 250, finalLevel))
                    .output(ModItems.EMPTY_BOTTLE.get()));
            }
        }
    }
}
