package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.google.common.collect.Lists;
import com.yision.creategearsandtavern.registry.CGTDataComponents;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class ShakerMixing {
    private static final ThreadLocal<ItemStack> CAPTURED_MIXER_RESULT = new ThreadLocal<>();

    private ShakerMixing() {
    }

    public static boolean isReadyShaker(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() != ModItems.SHAKER.get()) {
            return false;
        }
        if (ShakerItem.hasResult(stack)) {
            return false;
        }
        ItemStackHandler storage = ShakerItem.getStorage(stack);
        int filled = 0;
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack ingredient = storage.getStackInSlot(i);
            if (ingredient.isEmpty()) {
                return false;
            }
            if (!BottleBlockItem.isValidForShaker(ingredient)) {
                return false;
            }
            filled++;
        }
        return filled == storage.getSlots();
    }

    public static boolean canCompute(ItemStack stack) {
        if (!isReadyShaker(stack)) {
            return false;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return false;
        }
        return mix(stack, server.getRecipeManager(), server.registryAccess()) != null;
    }

    public static boolean captureMixerResult(ItemStack stack) {
        CAPTURED_MIXER_RESULT.remove();
        if (!isReadyShaker(stack)) {
            return false;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return false;
        }
        ItemStack result = mix(stack, server.getRecipeManager(), server.registryAccess());
        if (result == null || result.isEmpty()) {
            return false;
        }
        CAPTURED_MIXER_RESULT.set(result);
        return true;
    }

    public static ItemStack consumeCapturedMixerResult() {
        ItemStack result = CAPTURED_MIXER_RESULT.get();
        CAPTURED_MIXER_RESULT.remove();
        return result == null ? ItemStack.EMPTY : result.copy();
    }

    public static ItemStack mix(ItemStack shaker) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        return mix(shaker, server.getRecipeManager(), server.registryAccess());
    }

    public static ItemStack mix(ItemStack shaker, RecipeManager recipeManager, RegistryAccess registryAccess) {
        if (!isReadyShaker(shaker)) {
            return null;
        }
        ItemStack shakerCopy = shaker.copy();
        ItemStackHandler storage = ShakerItem.getStorage(shakerCopy);
        List<ItemStack> ingredients = Lists.newArrayList();
        for (int i = 0; i < storage.getSlots(); i++) {
            ItemStack ingredient = storage.getStackInSlot(i);
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient.copy());
            }
        }
        if (ingredients.size() != storage.getSlots()) {
            return null;
        }

        SimpleInput input = new SimpleInput(ingredients);
        com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.ShakerRecipe matchedRecipe = null;
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(ModRecipes.SHAKER_RECIPE)) {
            if (holder.value() instanceof com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.ShakerRecipe shakerRecipe
                && shakerRecipe.matches(input, null)) {
                matchedRecipe = shakerRecipe;
                break;
            }
        }

        ItemStack result;
        if (matchedRecipe != null) {
            result = matchedRecipe.assemble(input, registryAccess);
        } else {
            result = new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
            CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(storage);
            List<com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData.Entry> merged =
                CocktailEffectHelper.mergeEffects(data.effects());
            int color = ColorUtils.mixColors(data.colors());
            SignatureCocktailBlockItem.setEffects(result, merged);
            SignatureCocktailBlockItem.setColor(result, color);
        }

        if (result.isEmpty()) {
            return null;
        }
        result.set(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT, ShakerIngredientConversions.SLOT_AMOUNT);
        ShakerItem.setResult(shakerCopy, result);
        return shakerCopy;
    }
}
