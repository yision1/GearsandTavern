package com.yision.creategearsandtavern.datagen.recipe;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.data.recipe.EmptyingRecipeGen;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public class CGTEmptyingRecipeGen extends EmptyingRecipeGen {
    public CGTEmptyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateGearsandTavern.MOD_ID);

        for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
            ResourceLocation drinkId = definition.drinkId();
            Optional<Item> drinkItemOpt = BuiltInRegistries.ITEM.getOptional(drinkId);
            if (drinkItemOpt.isEmpty()) {
                continue;
            }
            Item drinkItem = drinkItemOpt.get();
            if (CGTDrinkCatalog.hasSingleVariant(drinkId)) {
                create(drinkId.getPath() + "_bottle", b -> {
                    b.require(DataComponentIngredient.of(false, new ItemStack(drinkItem)))
                        .output(CGTFluids.of(drinkId, 250, CGTDrinkCatalog.normalizedBrewLevel(drinkId, IBarrel.BREWING_STARTED)))
                        .output(ModItems.EMPTY_BOTTLE.get());
                    for (String requiredMod : definition.requiredMods()) {
                        b.whenModLoaded(requiredMod);
                    }
                    return b;
                });
                continue;
            }
            for (int level = IBarrel.BREWING_STARTED; level <= IBarrel.BREWING_FINISHED; level++) {
                int finalLevel = level;
                create(drinkId.getPath() + "_bottle_quality_" + level, b -> {
                    b.require(DataComponentIngredient.of(false, CGTDrinkRecipeGenHelper.bottleWithBrewLevel(drinkItem, finalLevel)))
                        .output(CGTFluids.of(drinkId, 250, finalLevel))
                        .output(ModItems.EMPTY_BOTTLE.get());
                    for (String requiredMod : definition.requiredMods()) {
                        b.whenModLoaded(requiredMod);
                    }
                    return b;
                });
            }
        }
    }
}
