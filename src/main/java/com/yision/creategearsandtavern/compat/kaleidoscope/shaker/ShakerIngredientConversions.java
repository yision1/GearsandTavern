package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.Optional;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

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
            KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(fluid);
            ResourceLocation drinkId = variant.drinkId();
            if (!CGTDrinkCatalog.hasDrinkId(drinkId) || CGTDrinkCatalog.isSignatureCocktail(drinkId)
                || variant.brewLevel() < BottleBlockItem.MIN_BREW_LEVEL_FOR_SHAKER) {
                return Optional.empty();
            }
            Item drinkItem = BuiltInRegistries.ITEM.get(drinkId);
            if (drinkItem == Items.AIR) {
                return Optional.empty();
            }
            ItemStack stack = new ItemStack(drinkItem);
            BottleBlockItem.setBrewLevel(stack, variant.brewLevel());
            return Optional.of(stack);
        }
        if (fluid.getFluid().isSame(Fluids.WATER)) {
            return Optional.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
        }
        if (fluid.getFluid() instanceof PotionFluid) {
            ItemStack bottle = PotionFluidHandler.fillBottle(ItemStack.EMPTY, fluid);
            if (bottle.isEmpty() || bottle.getItem() == Items.SPLASH_POTION
                || bottle.getItem() == Items.LINGERING_POTION) {
                return Optional.empty();
            }
            return Optional.of(bottle);
        }
        return Optional.empty();
    }

    public static FluidStack toSourceFluid(ItemStack ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (ingredient.getItem() instanceof BottleBlockItem) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(ingredient.getItem());
            if (!CGTDrinkCatalog.hasDrinkId(itemId) || CGTDrinkCatalog.isSignatureCocktail(itemId)) {
                return FluidStack.EMPTY;
            }
            int brewLevel = Math.max(IBarrel.BREWING_STARTED,
                Math.min(IBarrel.BREWING_FINISHED, BottleBlockItem.getBrewLevel(ingredient)));
            return CGTFluids.of(itemId, SLOT_AMOUNT, brewLevel);
        }
        if (ingredient.getItem() instanceof PotionItem) {
            if (PotionUtils.getPotion(ingredient) == Potions.WATER
                && PotionUtils.getCustomEffects(ingredient).isEmpty()) {
                return new FluidStack(Fluids.WATER, SLOT_AMOUNT);
            }
            FluidStack fluid = PotionFluidHandler.getFluidFromPotionItem(ingredient);
            fluid.setAmount(SLOT_AMOUNT);
            return fluid;
        }
        return FluidStack.EMPTY;
    }

    public static boolean isRestorableIngredient(ItemStack ingredient) {
        return !toSourceFluid(ingredient).isEmpty();
    }
}
