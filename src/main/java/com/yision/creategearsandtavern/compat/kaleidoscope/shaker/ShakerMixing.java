package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.ShakerRecipe;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.google.common.collect.Lists;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class ShakerMixing {
    public static final String RESULT_AMOUNT_TAG = "CreateGearsandTavernShakerAmount";
    private static final int INPUT_SLOTS = 3;
    private static final ThreadLocal<ItemStack> CAPTURED_MIXER_RESULT = new ThreadLocal<>();

    private ShakerMixing() {
    }

    public static ItemStackHandler normalizeStorage(ItemStackHandler storage) {
        if (storage.getSlots() == INPUT_SLOTS) {
            return storage;
        }
        ItemStackHandler resized = new ItemStackHandler(INPUT_SLOTS);
        for (int slot = 0; slot < Math.min(storage.getSlots(), INPUT_SLOTS); slot++) {
            resized.setStackInSlot(slot, storage.getStackInSlot(slot));
        }
        return resized;
    }

    public static boolean isReadyShaker(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() != ModItems.SHAKER.get()
            || ShakerItem.hasResult(stack)) {
            return false;
        }
        ItemStackHandler storage = normalizeStorage(ShakerItem.getStorage(stack));
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack ingredient = storage.getStackInSlot(slot);
            if (ingredient.isEmpty() || !BottleBlockItem.isValidForShaker(ingredient)) {
                return false;
            }
        }
        return true;
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

    public static ItemStack mix(ItemStack shaker, RecipeManager recipeManager, RegistryAccess registryAccess) {
        if (!isReadyShaker(shaker)) {
            return null;
        }
        ItemStackHandler storage = normalizeStorage(ShakerItem.getStorage(shaker.copy()));
        List<ItemStack> ingredients = Lists.newArrayList();
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ingredients.add(storage.getStackInSlot(slot).copyWithCount(1));
        }
        SimpleContainer input = new SimpleContainer(ingredients.toArray(new ItemStack[0]));

        ShakerRecipe matchedRecipe = null;
        for (ShakerRecipe recipe : recipeManager.getAllRecipesFor(ModRecipes.SHAKER_RECIPE)) {
            if (recipe.matches(input, null)) {
                matchedRecipe = recipe;
                break;
            }
        }

        ItemStack result;
        if (matchedRecipe != null) {
            result = matchedRecipe.assemble(input, registryAccess);
        } else {
            result = new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
            CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(storage);
            List<DrinkEffectData.Entry> effects = CocktailEffectHelper.mergeEffects(data.effects());
            SignatureCocktailBlockItem.setEffects(result, effects);
            SignatureCocktailBlockItem.setColor(result, ColorUtils.mixColors(data.colors()));
        }

        if (result.isEmpty()) {
            return null;
        }
        CompoundTags.setShakerAmount(result, ShakerIngredientConversions.SLOT_AMOUNT);
        ItemStack shakerResult = shaker.copy();
        ShakerItem.setResult(shakerResult, result);
        return shakerResult;
    }

    private static final class CompoundTags {
        private static void setShakerAmount(ItemStack stack, int amount) {
            stack.getOrCreateTag().putInt(RESULT_AMOUNT_TAG, amount);
        }
    }
}
