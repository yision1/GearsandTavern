package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.data.recipe.FillingRecipeGen;
import com.simibubi.create.foundation.fluid.FluidIngredient;
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

public class CGTFillingRecipeGen extends FillingRecipeGen {
    public CGTFillingRecipeGen(PackOutput output) {
        super(output, CreateGearsandTavern.MOD_ID);

        for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
            ResourceLocation drinkId = definition.drinkId();
            Item drinkItem = BuiltInRegistries.ITEM.get(drinkId);
            if (drinkItem == Items.AIR) {
                continue;
            }
            if (CGTDrinkCatalog.hasSingleVariant(drinkId)) {
                create(drinkId.getPath() + "_bottle", b -> {
                    b.require(ModItems.EMPTY_BOTTLE.get())
                        .require(FluidIngredient.fromFluidStack(CGTFluids.of(drinkId, 250, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL)))
                        .output(new ItemStack(drinkItem));
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
                    b.require(ModItems.EMPTY_BOTTLE.get())
                        .require(FluidIngredient.fromFluidStack(CGTFluids.of(drinkId, 250, finalLevel)))
                        .output(bottleWithBrewLevel(drinkItem, finalLevel));
                    for (String requiredMod : definition.requiredMods()) {
                        b.whenModLoaded(requiredMod);
                    }
                    return b;
                });
            }
        }
    }

    private static ItemStack bottleWithBrewLevel(Item item, int brewLevel) {
        return CGTDrinkRecipeGenHelper.bottleWithBrewLevel(item, brewLevel);
    }
}
