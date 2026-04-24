package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CGTDrinkRecipeGenHelper {
    private CGTDrinkRecipeGenHelper() {
    }

    public static ItemStack bottleWithBrewLevel(Item item, int brewLevel) {
        ItemStack stack = new ItemStack(item);
        BottleBlockItem.setBrewLevel(stack, brewLevel);
        return stack;
    }
}
