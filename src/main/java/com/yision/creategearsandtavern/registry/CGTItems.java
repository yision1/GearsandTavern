package com.yision.creategearsandtavern.registry;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class CGTItems {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
            (stack, context) -> {
                KaleidoscopeDrinkType drinkType = drinkTypeOfKaleidoscopeDrink(stack);
                return drinkType == null ? null : new KaleidoscopeDrinkBottleFluidHandler(stack, drinkType);
            },
            ModItems.WINE.get(),
            ModItems.CHAMPAGNE.get(),
            ModItems.VODKA.get(),
            ModItems.BRANDY.get(),
            ModItems.CARIGNAN.get(),
            ModItems.SAKURA_WINE.get(),
            ModItems.PLUM_WINE.get(),
            ModItems.WHISKEY.get(),
            ModItems.ICE_WINE.get(),
            ModItems.VINEGAR.get());
    }

    public static void register(IEventBus modEventBus) {
    }

    private static KaleidoscopeDrinkType drinkTypeOfKaleidoscopeDrink(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null || !KaleidoscopeDrinkType.NAMESPACE.equals(itemId.getNamespace())) {
            return null;
        }
        try {
            return KaleidoscopeDrinkType.byId(itemId);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static int brewLevelOf(ItemStack stack) {
        Integer brewLevel = stack.get(ModDataComponents.BREW_LEVEL.get());
        int level = brewLevel == null ? IBarrel.BREWING_STARTED : brewLevel;
        return Math.max(IBarrel.BREWING_STARTED, Math.min(IBarrel.BREWING_FINISHED, level));
    }

    private static class KaleidoscopeDrinkBottleFluidHandler implements IFluidHandlerItem {
        private static final int BOTTLE_AMOUNT = 250;
        private final ItemStack container;
        private final FluidStack fluid;

        private KaleidoscopeDrinkBottleFluidHandler(ItemStack container, KaleidoscopeDrinkType drinkType) {
            this.container = container;
            this.fluid = CGTFluids.of(drinkType, BOTTLE_AMOUNT, brewLevelOf(container));
        }

        @Override
        public ItemStack getContainer() {
            return container;
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
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!action.simulate() || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
                return FluidStack.EMPTY;
            }
            return fluid.copyWithAmount(Math.min(resource.getAmount(), fluid.getAmount()));
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (!action.simulate() || maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            return fluid.copyWithAmount(Math.min(maxDrain, fluid.getAmount()));
        }
    }
}
