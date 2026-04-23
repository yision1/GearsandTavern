package com.yision.creategearsandtavern.registry;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;

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
                ResourceLocation drinkId = drinkIdOf(stack);
                return drinkId == null ? null : new KaleidoscopeDrinkBottleFluidHandler(stack, drinkId);
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
            ModItems.VINEGAR.get(),
            ModItems.MOLOTOV.get());
    }

    public static void registerCapabilitiesForKdw(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (!itemId.getNamespace().equals("kaleidoscope_dim_wine")) {
                continue;
            }
            if (!CGTDrinkCatalog.hasDrinkId(itemId)) {
                continue;
            }
            event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, context) -> {
                    ResourceLocation drinkId = drinkIdOf(stack);
                    return drinkId == null ? null : new KaleidoscopeDrinkBottleFluidHandler(stack, drinkId);
                },
                item);
        }
    }

    public static void register(IEventBus modEventBus) {
    }

    private static ResourceLocation drinkIdOf(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) {
            return null;
        }
        if (CGTDrinkCatalog.hasDrinkId(itemId)) {
            return itemId;
        }
        return null;
    }

    private static int fluidBrewLevelOf(ResourceLocation drinkId, ItemStack stack) {
        Integer brewLevel = stack.get(ModDataComponents.BREW_LEVEL.get());
        if (brewLevel == null) {
            return CGTDrinkCatalog.normalizedBrewLevel(drinkId, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL);
        }
        int level = brewLevel;
        return CGTDrinkCatalog.normalizedBrewLevel(drinkId,
            Math.max(IBarrel.BREWING_STARTED, Math.min(IBarrel.BREWING_FINISHED, level)));
    }

    private static class KaleidoscopeDrinkBottleFluidHandler implements IFluidHandlerItem {
        private static final int BOTTLE_AMOUNT = 250;
        private final ItemStack container;
        private final FluidStack fluid;

        private KaleidoscopeDrinkBottleFluidHandler(ItemStack container, ResourceLocation drinkId) {
            this.container = container;
            this.fluid = CGTFluids.of(drinkId, BOTTLE_AMOUNT, fluidBrewLevelOf(drinkId, container));
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
