package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class CocktailItemFluidHandlers {
    private CocktailItemFluidHandlers() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
            (stack, ctx) -> new SignatureCocktailItemFluidHandler(stack),
            ModItems.SIGNATURE_COCKTAIL.get());
    }

    public static final class SignatureCocktailItemFluidHandler implements IFluidHandlerItem {
        private static final int CAPACITY = CGTDrinkCatalog.COCKTAIL_AMOUNT;
        private ItemStack container;

        public SignatureCocktailItemFluidHandler(ItemStack container) {
            this.container = container;
        }

        @Override
        public ItemStack getContainer() {
            return container;
        }

        private FluidStack heldFluid() {
            return CocktailFluidConversions.fromResult(container);
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return tank == 0 ? heldFluid() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? CAPACITY : 0;
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
            if (resource == null || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FluidStack held = heldFluid();
            if (held.isEmpty() || !FluidStack.isSameFluidSameComponents(held, resource)) {
                return FluidStack.EMPTY;
            }
            return drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack held = heldFluid();
            if (held.isEmpty() || maxDrain < CAPACITY) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                container = new ItemStack(ModItems.EMPTY_GLASSWARE.get());
            }
            return held.copyWithAmount(CAPACITY);
        }
    }

}
