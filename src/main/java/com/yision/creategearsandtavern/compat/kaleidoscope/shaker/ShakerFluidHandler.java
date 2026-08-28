package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.CocktailFluidConversions;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;

public final class ShakerFluidHandler implements IFluidHandlerItem {
    private final ShakerContentsAccess access;
    private final ItemStack container;

    public ShakerFluidHandler(ShakerContentsAccess access, ItemStack container) {
        this.access = access;
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    private boolean hasResult() {
        return !access.getResult().isEmpty();
    }

    @Override
    public int getTanks() {
        return hasResult() ? 1 : ShakerIngredientConversions.INPUT_SLOTS;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank < 0) {
            return FluidStack.EMPTY;
        }
        if (hasResult()) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }
            FluidStack result = CocktailFluidConversions.fromShakerResult(access.getResult(), access.getStorage());
            int amount = result.isEmpty() ? 0 : resultAmount();
            return amount <= 0 ? FluidStack.EMPTY : copyWithAmount(result, amount);
        }
        if (tank >= ShakerIngredientConversions.INPUT_SLOTS) {
            return FluidStack.EMPTY;
        }
        return ShakerIngredientConversions.toSourceFluid(access.getStorage().getStackInSlot(tank));
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank < 0) {
            return 0;
        }
        if (hasResult()) {
            return tank == 0 ? ShakerIngredientConversions.SLOT_AMOUNT : 0;
        }
        if (tank >= ShakerIngredientConversions.INPUT_SLOTS) {
            return 0;
        }
        ItemStack ingredient = access.getStorage().getStackInSlot(tank);
        return ingredient.isEmpty() || ShakerIngredientConversions.isRestorableIngredient(ingredient)
            ? ShakerIngredientConversions.SLOT_AMOUNT : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (hasResult() || resource == null || resource.getAmount() < ShakerIngredientConversions.SLOT_AMOUNT) {
            return 0;
        }
        ItemStack ingredient = ShakerIngredientConversions.toIngredientStack(
            copyWithAmount(resource, ShakerIngredientConversions.SLOT_AMOUNT)).orElse(ItemStack.EMPTY);
        if (ingredient.isEmpty() || !BottleBlockItem.isValidForShaker(ingredient)) {
            return 0;
        }
        ItemStackHandler storage = access.getStorage();
        int slot = firstEmptySlot(storage);
        if (slot < 0) {
            return 0;
        }
        if (action.execute()) {
            storage.setStackInSlot(slot, ingredient.copyWithCount(1));
            access.setStorage(storage);
            access.markChanged();
        }
        return ShakerIngredientConversions.SLOT_AMOUNT;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource == null || resource.isEmpty() || resource.getAmount() < ShakerIngredientConversions.SLOT_AMOUNT) {
            return FluidStack.EMPTY;
        }
        if (hasResult()) {
            return drainResult(resource, action);
        }
        ItemStackHandler storage = access.getStorage();
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            FluidStack source = ShakerIngredientConversions.toSourceFluid(storage.getStackInSlot(slot));
            if (!source.isEmpty() && sameFluidAndTags(source, resource)) {
                if (action.execute()) {
                    storage.setStackInSlot(slot, ItemStack.EMPTY);
                    access.setStorage(storage);
                    access.markChanged();
                }
                return copyWithAmount(source, ShakerIngredientConversions.SLOT_AMOUNT);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain < ShakerIngredientConversions.SLOT_AMOUNT) {
            return FluidStack.EMPTY;
        }
        if (hasResult()) {
            FluidStack result = CocktailFluidConversions.fromShakerResult(access.getResult(), access.getStorage());
            return result.isEmpty() ? FluidStack.EMPTY : drainResult(result, action);
        }
        ItemStackHandler storage = access.getStorage();
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            FluidStack source = ShakerIngredientConversions.toSourceFluid(storage.getStackInSlot(slot));
            if (!source.isEmpty()) {
                if (action.execute()) {
                    storage.setStackInSlot(slot, ItemStack.EMPTY);
                    access.setStorage(storage);
                    access.markChanged();
                }
                return copyWithAmount(source, ShakerIngredientConversions.SLOT_AMOUNT);
            }
        }
        return FluidStack.EMPTY;
    }

    private FluidStack drainResult(FluidStack requested, FluidAction action) {
        FluidStack result = CocktailFluidConversions.fromShakerResult(access.getResult(), access.getStorage());
        if (result.isEmpty() || !sameFluidAndTags(result, requested)) {
            return FluidStack.EMPTY;
        }
        int amount = Math.min(resultAmount(), ShakerIngredientConversions.SLOT_AMOUNT);
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }
        if (action.execute()) {
            int remaining = resultAmount() - amount;
            if (remaining <= 0) {
                access.clear();
            } else {
                ItemStack updated = access.getResult().copy();
                updated.getOrCreateTag().putInt(ShakerMixing.RESULT_AMOUNT_TAG, remaining);
                access.setResult(updated);
                access.markChanged();
            }
        }
        return copyWithAmount(result, amount);
    }

    private int resultAmount() {
        ItemStack result = access.getResult();
        if (result.getTag() == null || !result.getTag().contains(ShakerMixing.RESULT_AMOUNT_TAG)) {
            return ShakerIngredientConversions.SLOT_AMOUNT;
        }
        return Math.min(ShakerIngredientConversions.SLOT_AMOUNT,
            Math.max(0, result.getTag().getInt(ShakerMixing.RESULT_AMOUNT_TAG)));
    }

    private static int firstEmptySlot(ItemStackHandler storage) {
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            if (storage.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private static FluidStack copyWithAmount(FluidStack source, int amount) {
        FluidStack copy = source.copy();
        copy.setAmount(amount);
        return copy;
    }

    private static boolean sameFluidAndTags(FluidStack first, FluidStack second) {
        return first.getFluid().isSame(second.getFluid())
            && FluidStack.areFluidStackTagsEqual(first, second);
    }
}
