package com.yision.creategearsandtavern.compat.kaleidoscope.cabinet;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BottleBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarCabinetBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

public class BarCabinetLineItemHandler implements IItemHandler {
    private final BlockEntity context;
    private final Direction side;

    private BarCabinetLineItemHandler(BlockEntity context, Direction side) {
        this.context = context;
        this.side = side;
    }

    public static IItemHandler create(BlockEntity context, Direction side) {
        if (context == null || context.getLevel() == null) {
            return null;
        }
        return new BarCabinetLineItemHandler(context, side);
    }

    private boolean canAccess() {
        return side == null || side == Direction.UP || side == Direction.DOWN || side.getAxis().isHorizontal();
    }

    @Override
    public int getSlots() {
        if (!canAccess()) {
            return 0;
        }
        return line().positions().size() * 2;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (!canAccess()) {
            return ItemStack.EMPTY;
        }
        BarCabinetBlockEntity cabinet = cabinetForSlot(slot);
        if (cabinet == null) {
            return ItemStack.EMPTY;
        }
        return isLeftSlot(slot) ? cabinet.getLeftItem() : cabinet.getRightItem();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!canAccess()) {
            return stack;
        }
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        BottleBlock bottle = bottleBlock(stack);
        if (bottle == null) {
            return stack;
        }

        BarCabinetBlockEntity cabinet = cabinetForSlot(slot);
        if (cabinet == null || !canInsertInto(cabinet, slot, bottle)) {
            return stack;
        }

        ItemStack inserted = stack.copyWithCount(1);
        ItemStack remainder = stack.copy();
        remainder.shrink(1);

        if (!simulate) {
            if (isLeftSlot(slot)) {
                cabinet.setLeftItem(inserted);
            } else {
                cabinet.setRightItem(inserted);
            }
            cabinet.setSingle(bottle.irregular());
            cabinet.refresh();
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canAccess()) {
            return ItemStack.EMPTY;
        }
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        BarCabinetBlockEntity cabinet = cabinetForSlot(slot);
        if (cabinet == null) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = isLeftSlot(slot) ? cabinet.getLeftItem() : cabinet.getRightItem();
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int extractedCount = Math.min(amount, Math.min(existing.getCount(), getSlotLimit(slot)));
        ItemStack extracted = existing.copyWithCount(extractedCount);
        if (!simulate) {
            existing.shrink(extractedCount);
            if (existing.isEmpty() && isLeftSlot(slot)) {
                cabinet.setLeftItem(ItemStack.EMPTY);
            } else if (existing.isEmpty()) {
                cabinet.setRightItem(ItemStack.EMPTY);
            }
            if (cabinet.getLeftItem().isEmpty() && cabinet.getRightItem().isEmpty()) {
                cabinet.setSingle(false);
            }
            cabinet.refresh();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!canAccess()) {
            return false;
        }
        BottleBlock bottle = bottleBlock(stack);
        if (bottle == null) {
            return false;
        }
        BarCabinetBlockEntity cabinet = cabinetForSlot(slot);
        return cabinet != null && canInsertInto(cabinet, slot, bottle);
    }

    private BarCabinetLineCache.LineView line() {
        return BarCabinetLineCache.get(context.getLevel(), context.getBlockPos(), context.getBlockState());
    }

    private BarCabinetBlockEntity cabinetForSlot(int slot) {
        if (slot < 0) {
            return null;
        }
        BlockEntity be = line().blockEntityAt(context.getLevel(), slot / 2);
        if (be == null || be.isRemoved()) {
            return null;
        }
        return be instanceof BarCabinetBlockEntity cabinet ? cabinet : null;
    }

    private boolean canInsertInto(BarCabinetBlockEntity cabinet, int slot, BottleBlock bottle) {
        boolean left = isLeftSlot(slot);
        if (bottle.irregular()) {
            return left && cabinet.getLeftItem().isEmpty() && cabinet.getRightItem().isEmpty();
        }
        if (cabinet.isSingle()) {
            return false;
        }
        return left ? cabinet.getLeftItem().isEmpty() : cabinet.getRightItem().isEmpty();
    }

    private static boolean isLeftSlot(int slot) {
        return slot % 2 == 0;
    }

    private static BottleBlock bottleBlock(ItemStack stack) {
        if (stack.getItem() instanceof BottleBlockItem item && item.getBlock() instanceof BottleBlock bottleBlock) {
            return bottleBlock;
        }
        return null;
    }
}
