package com.yision.creategearsandtavern.compat.kaleidoscope.cabinet;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BottleBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarCabinetBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.CellarCabinetBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.init.tag.TagMod;
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
        return line().positions().size() * slotsPerCabinet();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (!canAccess()) {
            return ItemStack.EMPTY;
        }
        int slotsPerCabinet = slotsPerCabinet();
        BlockEntity cabinet = cabinetForSlot(slot, slotsPerCabinet);
        if (cabinet instanceof BarCabinetBlockEntity barCabinet) {
            return isLeftSlot(slot) ? barCabinet.getLeftItem() : barCabinet.getRightItem();
        }
        if (cabinet instanceof CellarCabinetBlockEntity cellarCabinet) {
            return cellarCabinet.getItems().getStackInSlot(slot % slotsPerCabinet);
        }
        return ItemStack.EMPTY;
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

        int slotsPerCabinet = slotsPerCabinet();
        BlockEntity cabinet = cabinetForSlot(slot, slotsPerCabinet);
        if (cabinet instanceof CellarCabinetBlockEntity cellarCabinet) {
            if (stack.is(TagMod.CELLAR_CABINET_BLOCKLIST)) {
                return stack;
            }
            ItemStack remainder = cellarCabinet.getItems().insertItem(slot % slotsPerCabinet, stack, simulate);
            if (!simulate && remainder.getCount() != stack.getCount()) {
                cellarCabinet.refresh();
            }
            return remainder;
        }
        if (!(cabinet instanceof BarCabinetBlockEntity barCabinet)
            || !canInsertInto(barCabinet, slot, bottle)) {
            return stack;
        }

        ItemStack inserted = stack.copyWithCount(1);
        ItemStack remainder = stack.copy();
        remainder.shrink(1);

        if (!simulate) {
            if (isLeftSlot(slot)) {
                barCabinet.setLeftItem(inserted);
            } else {
                barCabinet.setRightItem(inserted);
            }
            barCabinet.setSingle(bottle.irregular());
            barCabinet.refresh();
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

        int slotsPerCabinet = slotsPerCabinet();
        BlockEntity cabinet = cabinetForSlot(slot, slotsPerCabinet);
        if (cabinet instanceof CellarCabinetBlockEntity cellarCabinet) {
            ItemStack extracted = cellarCabinet.getItems().extractItem(slot % slotsPerCabinet, amount, simulate);
            if (!simulate && !extracted.isEmpty()) {
                cellarCabinet.refresh();
            }
            return extracted;
        }
        if (!(cabinet instanceof BarCabinetBlockEntity barCabinet)) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = isLeftSlot(slot) ? barCabinet.getLeftItem() : barCabinet.getRightItem();
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int extractedCount = Math.min(amount, Math.min(existing.getCount(), getSlotLimit(slot)));
        ItemStack extracted = existing.copyWithCount(extractedCount);
        if (!simulate) {
            existing.shrink(extractedCount);
            if (existing.isEmpty() && isLeftSlot(slot)) {
                barCabinet.setLeftItem(ItemStack.EMPTY);
            } else if (existing.isEmpty()) {
                barCabinet.setRightItem(ItemStack.EMPTY);
            }
            if (barCabinet.getLeftItem().isEmpty() && barCabinet.getRightItem().isEmpty()) {
                barCabinet.setSingle(false);
            }
            barCabinet.refresh();
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
        int slotsPerCabinet = slotsPerCabinet();
        BlockEntity cabinet = cabinetForSlot(slot, slotsPerCabinet);
        if (cabinet instanceof CellarCabinetBlockEntity) {
            return !stack.is(TagMod.CELLAR_CABINET_BLOCKLIST);
        }
        return cabinet instanceof BarCabinetBlockEntity barCabinet
            && canInsertInto(barCabinet, slot, bottle);
    }

    private BarCabinetLineCache.LineView line() {
        return BarCabinetLineCache.get(context.getLevel(), context.getBlockPos(), context.getBlockState());
    }

    private BlockEntity cabinetForSlot(int slot, int slotsPerCabinet) {
        if (slot < 0 || slotsPerCabinet == 0) {
            return null;
        }
        BlockEntity be = line().blockEntityAt(context.getLevel(), slot / slotsPerCabinet);
        if (be == null || be.isRemoved()) {
            return null;
        }
        return be;
    }

    private int slotsPerCabinet() {
        if (context instanceof BarCabinetBlockEntity) {
            return 2;
        }
        if (context instanceof CellarCabinetBlockEntity) {
            return 9;
        }
        return 0;
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
