package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.CocktailFluidConversions;
import com.yision.creategearsandtavern.registry.CGTDataComponents;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ShakerFluidHandler implements IFluidHandlerItem {
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
            FluidStack resultFluid = currentResultFluid();
            if (resultFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            int amount = currentResultAmount();
            if (amount <= 0) {
                return FluidStack.EMPTY;
            }
            return resultFluid.copyWithAmount(amount);
        }
        if (tank >= ShakerIngredientConversions.INPUT_SLOTS) {
            return FluidStack.EMPTY;
        }
        ItemStack ingredient = access.getStorage().getStackInSlot(tank);
        return ShakerIngredientConversions.toSourceFluid(ingredient);
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
        if (ingredient.isEmpty()) {
            return ShakerIngredientConversions.SLOT_AMOUNT;
        }
        return ShakerIngredientConversions.isRestorableIngredient(ingredient) ? ShakerIngredientConversions.SLOT_AMOUNT : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (hasResult() || resource == null || resource.getAmount() < ShakerIngredientConversions.SLOT_AMOUNT) {
            return 0;
        }
        FluidStack sample = resource.copyWithAmount(ShakerIngredientConversions.SLOT_AMOUNT);
        ItemStack ingredient = ShakerIngredientConversions.toIngredientStack(sample)
            .orElse(ItemStack.EMPTY);
        if (ingredient.isEmpty()) {
            return 0;
        }
        int slot = firstEmptyInputSlot();
        if (slot < 0) {
            return 0;
        }
        if (!com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem.isValidForShaker(ingredient)) {
            return 0;
        }
        if (action.execute()) {
            ItemStack toStore = ingredient.copyWithCount(1);
            ItemStackHandler storage = access.getStorage();
            storage.setStackInSlot(slot, toStore);
            access.setStorage(storage);
            access.markChanged();
        }
        return ShakerIngredientConversions.SLOT_AMOUNT;
    }

    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource == null || resource.isEmpty() || resource.getAmount() < ShakerIngredientConversions.SLOT_AMOUNT) {
            return FluidStack.EMPTY;
        }
        if (hasResult()) {
            return drainResult(resource, action);
        }
        return drainInput(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (maxDrain < ShakerIngredientConversions.SLOT_AMOUNT) {
            return FluidStack.EMPTY;
        }
        if (hasResult()) {
            FluidStack resultFluid = currentResultFluid();
            if (resultFluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            return drainResult(resultFluid, action);
        }
        return drainFirstInput(action);
    }

    private FluidStack drainResult(FluidStack requested, IFluidHandler.FluidAction action) {
        FluidStack resultFluid = currentResultFluid();
        if (resultFluid.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (!FluidStack.isSameFluidSameComponents(resultFluid, requested)) {
            return FluidStack.EMPTY;
        }
        int available = currentResultAmount();
        int drained = Math.min(available, ShakerIngredientConversions.SLOT_AMOUNT);
        if (drained <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack output = resultFluid.copyWithAmount(drained);
        if (action.execute()) {
            int remaining = available - drained;
            if (remaining <= 0) {
                access.clear();
            } else {
                setResultAmount(remaining);
            }
            access.markChanged();
        }
        return output;
    }

    private FluidStack currentResultFluid() {
        return CocktailFluidConversions.fromShakerResult(access.getResult(), access.getStorage());
    }

    private FluidStack drainInput(FluidStack requested, IFluidHandler.FluidAction action) {
        ItemStackHandler storage = access.getStorage();
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            ItemStack ingredient = storage.getStackInSlot(slot);
            FluidStack source = ShakerIngredientConversions.toSourceFluid(ingredient);
            if (source.isEmpty()) {
                continue;
            }
            if (!FluidStack.isSameFluidSameComponents(source, requested)) {
                continue;
            }
            if (action.execute()) {
                storage.setStackInSlot(slot, ItemStack.EMPTY);
                access.setStorage(storage);
                access.markChanged();
            }
            return source.copyWithAmount(ShakerIngredientConversions.SLOT_AMOUNT);
        }
        return FluidStack.EMPTY;
    }

    private FluidStack drainFirstInput(IFluidHandler.FluidAction action) {
        ItemStackHandler storage = access.getStorage();
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            ItemStack ingredient = storage.getStackInSlot(slot);
            FluidStack source = ShakerIngredientConversions.toSourceFluid(ingredient);
            if (source.isEmpty()) {
                continue;
            }
            if (action.execute()) {
                storage.setStackInSlot(slot, ItemStack.EMPTY);
                access.setStorage(storage);
                access.markChanged();
            }
            return source.copyWithAmount(ShakerIngredientConversions.SLOT_AMOUNT);
        }
        return FluidStack.EMPTY;
    }

    private int firstEmptyInputSlot() {
        ItemStackHandler storage = access.getStorage();
        for (int slot = 0; slot < storage.getSlots(); slot++) {
            if (storage.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private int currentResultAmount() {
        ItemStack result = access.getResult();
        Integer amount = result.get(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT);
        if (amount == null || amount <= 0) {
            return ShakerIngredientConversions.SLOT_AMOUNT;
        }
        return Math.min(amount, ShakerIngredientConversions.SLOT_AMOUNT);
    }

    private void setResultAmount(int amount) {
        ItemStack result = access.getResult().copy();
        if (amount <= 0) {
            result.remove(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT);
        } else {
            result.set(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT, amount);
        }
        access.setResult(result);
    }
}
