package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.Optional;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public final class ShakerIngredientConversions {
    public static final int SLOT_AMOUNT = 250;
    public static final int INPUT_SLOTS = 3;

    private ShakerIngredientConversions() {
    }

    public static Optional<ItemStack> toIngredientStack(FluidStack fluid) {
        if (fluid == null || fluid.getAmount() < SLOT_AMOUNT) {
            return Optional.empty();
        }
        if (fluid.getFluid() instanceof KaleidoscopeDrinkFluid) {
            return drinkFluidToIngredient(fluid);
        }
        if (fluid.getFluid().isSame(Fluids.WATER)) {
            return Optional.of(PotionContents.createItemStack(Items.POTION, Potions.WATER));
        }
        if (fluid.getFluid() instanceof PotionFluid) {
            return potionFluidToIngredient(fluid);
        }
        return Optional.empty();
    }

    private static Optional<ItemStack> drinkFluidToIngredient(FluidStack fluid) {
        KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(fluid);
        ResourceLocation drinkId = variant.drinkId();
        if (!CGTDrinkCatalog.hasDrinkId(drinkId)) {
            return Optional.empty();
        }
        if (!variant.definition().qualityAware()) {
            return Optional.empty();
        }
        int brewLevel = CGTDrinkCatalog.normalizedBrewLevel(drinkId, variant.brewLevel());
        if (brewLevel < BottleBlockItem.MIN_BREW_LEVEL_FOR_SHAKER) {
            return Optional.empty();
        }
        Item drinkItem = BuiltInRegistries.ITEM.get(drinkId);
        if (drinkItem == Items.AIR) {
            return Optional.empty();
        }
        ItemStack stack = new ItemStack(drinkItem);
        stack.set(ModDataComponents.BREW_LEVEL.get(), brewLevel);
        return Optional.of(stack);
    }

    private static Optional<ItemStack> potionFluidToIngredient(FluidStack fluid) {
        ItemStack bottle = PotionFluidHandler.fillBottle(ItemStack.EMPTY, fluid);
        if (bottle.isEmpty()) {
            return Optional.empty();
        }
        if (bottle.getItem() == Items.SPLASH_POTION || bottle.getItem() == Items.LINGERING_POTION) {
            return Optional.empty();
        }
        return Optional.of(bottle);
    }

    public static FluidStack toSourceFluid(ItemStack ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (ingredient.getItem() instanceof BottleBlockItem) {
            return bottleToSourceFluid(ingredient);
        }
        if (ingredient.getItem() == Items.POTION) {
            PotionContents contents = ingredient.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (contents.is(Potions.WATER) && contents.customEffects().isEmpty()) {
                return new FluidStack(Fluids.WATER, SLOT_AMOUNT);
            }
            return PotionFluid.of(SLOT_AMOUNT, contents, PotionFluid.BottleType.REGULAR);
        }
        return FluidStack.EMPTY;
    }

    private static FluidStack bottleToSourceFluid(ItemStack ingredient) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(ingredient.getItem());
        if (!CGTDrinkCatalog.hasDrinkId(itemId)) {
            return FluidStack.EMPTY;
        }
        if (!CGTDrinkCatalog.byDrinkId(itemId).qualityAware()) {
            return FluidStack.EMPTY;
        }
        int brewLevel = CGTDrinkCatalog.normalizedBrewLevel(itemId,
            Math.max(IBarrel.BREWING_STARTED, Math.min(IBarrel.BREWING_FINISHED, BottleBlockItem.getBrewLevel(ingredient))));
        return CGTFluids.of(itemId, SLOT_AMOUNT, brewLevel);
    }

    public static boolean isRestorableIngredient(ItemStack ingredient) {
        return !toSourceFluid(ingredient).isEmpty();
    }
}
