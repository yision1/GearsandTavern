package com.yision.creategearsandtavern.datagen.recipe;

import java.util.Comparator;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CGTDrinkRecipeGenHelper {
    private CGTDrinkRecipeGenHelper() {
    }

    public static ItemStack bottleWithBrewLevel(Item item, int brewLevel) {
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponents.BREW_LEVEL.get(), brewLevel);
        return stack;
    }

    public static void applyConditions(
        StandardProcessingRecipe.Builder<?> builder,
        CGTDrinkDefinition definition,
        ResourceLocation drinkId) {
        definition.requiredMods().stream()
            .sorted(Comparator
                .comparing((String mod) -> !mod.equals(drinkId.getNamespace()))
                .thenComparing(mod -> mod))
            .forEach(builder::whenModLoaded);
    }
}
