package com.yision.creategearsandtavern.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

public record CGTShakerJeiRecipe(List<ItemStack> inputs, List<ItemStack> outputs) {
    public CGTShakerJeiRecipe {
        inputs = copyStacks(inputs, "inputs");
        outputs = copyStacks(outputs, "outputs");
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("Shaker JEI recipe must have at least one input and output");
        }
        if (inputs.size() != outputs.size()) {
            throw new IllegalArgumentException("Shaker JEI inputs and outputs must be paired");
        }
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks, String name) {
        if (stacks == null) {
            throw new IllegalArgumentException("Shaker JEI " + name + " must be non-null");
        }
        List<ItemStack> copies = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                throw new IllegalArgumentException("Shaker JEI " + name + " must be non-empty");
            }
            copies.add(stack.copy());
        }
        return List.copyOf(copies);
    }
}
