package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public final class CocktailItemFluidHandlers {
    private CocktailItemFluidHandlers() {
    }

    public static IFluidHandlerItem create(ItemStack stack) {
        return stack.getItem() == ModItems.SIGNATURE_COCKTAIL.get()
            ? new SignatureCocktailItemFluidHandler(stack)
            : null;
    }

    public static final class SignatureCocktailItemFluidHandler implements IFluidHandlerItem {
        private ItemStack container;

        private SignatureCocktailItemFluidHandler(ItemStack container) {
            this.container = container;
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
            return tank == 0 ? CocktailFluidConversions.fromResult(container) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank == 0 ? CGTDrinkCatalog.COCKTAIL_AMOUNT : 0;
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
            FluidStack held = getFluidInTank(0);
            if (resource == null || held.isEmpty() || !sameFluidAndTags(held, resource)) {
                return FluidStack.EMPTY;
            }
            return drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack held = getFluidInTank(0);
            if (held.isEmpty() || maxDrain < CGTDrinkCatalog.COCKTAIL_AMOUNT) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                container = new ItemStack(ModItems.EMPTY_GLASSWARE.get());
            }
            return held;
        }

        private static boolean sameFluidAndTags(FluidStack first, FluidStack second) {
            return first.getFluid().isSame(second.getFluid())
                && FluidStack.areFluidStackTagsEqual(first, second);
        }
    }
}
