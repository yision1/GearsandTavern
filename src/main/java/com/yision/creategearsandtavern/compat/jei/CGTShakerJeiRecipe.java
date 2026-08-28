package com.yision.creategearsandtavern.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

public record CGTShakerJeiRecipe(List<ItemStack> inputs, List<ItemStack> outputs) {
    public CGTShakerJeiRecipe {
        inputs = copyStacks(inputs);
        outputs = copyStacks(outputs);
        if (inputs.isEmpty() || inputs.size() != outputs.size()) {
            throw new IllegalArgumentException("Shaker JEI inputs and outputs must be paired and non-empty");
        }
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copies = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                throw new IllegalArgumentException("Shaker JEI stacks must be non-empty");
            }
            copies.add(stack.copy());
        }
        return List.copyOf(copies);
    }
}
