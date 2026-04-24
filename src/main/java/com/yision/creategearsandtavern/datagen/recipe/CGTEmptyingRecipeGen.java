package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.data.recipe.EmptyingRecipeGen;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class CGTEmptyingRecipeGen extends EmptyingRecipeGen {
    public CGTEmptyingRecipeGen(PackOutput output) {
        super(output, CreateGearsandTavern.MOD_ID);

        for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
            ResourceLocation drinkId = definition.drinkId();
            Item drinkItem = BuiltInRegistries.ITEM.get(drinkId);
            if (drinkItem == Items.AIR) {
                continue;
            }
            if (CGTDrinkCatalog.hasSingleVariant(drinkId)) {
                create(drinkId.getPath() + "_bottle", b -> {
                    b.require(Ingredient.of(drinkItem))
                        .output(CGTFluids.of(drinkId, 250, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL))
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
                    b.require(new BrewLevelIngredient(drinkItem, finalLevel))
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
