package com.yision.creategearsandtavern.compat.kaleidoscope;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class CGTItems {
    private static final String BREW_LEVEL_KEY = "BrewLevel";

    public static IFluidHandlerItem createFluidHandler(ItemStack stack) {
        ResourceLocation drinkId = drinkIdOf(stack);
        if (drinkId == null || !shouldExposeFluidHandler(drinkId, stack)) {
            return null;
        }
        return new KaleidoscopeDrinkBottleFluidHandler(stack, drinkId);
    }

    private static ResourceLocation drinkIdOf(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            return null;
        }
        return CGTDrinkCatalog.hasDrinkId(itemId) ? itemId : null;
    }

    private static class KaleidoscopeDrinkBottleFluidHandler implements IFluidHandlerItem {
        private static final int BOTTLE_AMOUNT = 250;
        private final ItemStack container;
        private final FluidStack fluid;
        private boolean emptied;

        private KaleidoscopeDrinkBottleFluidHandler(ItemStack container, ResourceLocation drinkId) {
            this.container = container;
            this.fluid = CGTFluids.of(drinkId, BOTTLE_AMOUNT, fluidBrewLevelOf(drinkId, container));
            this.emptied = false;
        }

        @Override
        public ItemStack getContainer() {
            return emptied ? new ItemStack(ModItems.EMPTY_BOTTLE.get()) : container;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? fluid.copy() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return BOTTLE_AMOUNT;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || emptied || !sameFluidAndVariant(resource)) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = copyFluid(Math.min(resource.getAmount(), fluid.getAmount()));
            if (action.execute() && drained.getAmount() == fluid.getAmount()) {
                emptied = true;
            }
            return drained;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain <= 0 || emptied) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = copyFluid(Math.min(maxDrain, fluid.getAmount()));
            if (action.execute() && drained.getAmount() == fluid.getAmount()) {
                emptied = true;
            }
            return drained;
        }

        private boolean sameFluidAndVariant(FluidStack resource) {
            return resource.getFluid().isSame(fluid.getFluid())
                && Objects.equals(resource.getTag(), fluid.getTag());
        }

        private FluidStack copyFluid(int amount) {
            FluidStack drained = fluid.copy();
            drained.setAmount(amount);
            return drained;
        }
    }

    private static int fluidBrewLevelOf(ResourceLocation drinkId, ItemStack stack) {
        return normalizeFluidBrewLevel(drinkId, BottleBlockItem.getBrewLevel(stack));
    }

    static boolean shouldExposeFluidHandler(ResourceLocation drinkId, ItemStack stack) {
        return shouldExposeFluidHandler(drinkId,
            stack.getTag() != null && stack.getTag().contains(BREW_LEVEL_KEY));
    }

    static boolean shouldExposeFluidHandler(ResourceLocation drinkId, boolean hasExplicitBrewLevel) {
        return hasExplicitBrewLevel;
    }

    static int normalizeFluidBrewLevel(ResourceLocation drinkId, int brewLevel) {
        if (brewLevel <= 0) {
            return CGTDrinkCatalog.normalizedBrewLevel(drinkId, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL);
        }
        return CGTDrinkCatalog.normalizedBrewLevel(drinkId,
            Math.max(IBarrel.BREWING_STARTED, Math.min(IBarrel.BREWING_FINISHED, brewLevel)));
    }
}
