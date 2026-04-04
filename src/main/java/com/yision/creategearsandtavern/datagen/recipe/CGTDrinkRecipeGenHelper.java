package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;

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
}
