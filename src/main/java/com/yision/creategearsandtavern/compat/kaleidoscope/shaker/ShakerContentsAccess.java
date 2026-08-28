package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.mixology.ShakerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public interface ShakerContentsAccess {
    ItemStackHandler getStorage();
    ItemStack getResult();
    void setStorage(ItemStackHandler handler);
    void setResult(ItemStack result);
    void clear();
    void markChanged();

    final class ItemShakerContentsAccess implements ShakerContentsAccess {
        private final ItemStack stack;

        public ItemShakerContentsAccess(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStackHandler getStorage() {
            return ShakerMixing.normalizeStorage(ShakerItem.getStorage(stack));
        }

        @Override
        public ItemStack getResult() {
            return ShakerItem.getResult(stack);
        }

        @Override
        public void setStorage(ItemStackHandler handler) {
            ShakerItem.setStorage(stack, ShakerMixing.normalizeStorage(handler));
        }

        @Override
        public void setResult(ItemStack result) {
            ShakerItem.setResult(stack, result);
        }

        @Override
        public void clear() {
            ShakerItem.removeAll(stack);
        }

        @Override
        public void markChanged() {
        }
    }

    final class BlockShakerContentsAccess implements ShakerContentsAccess {
        private final ShakerBlockEntity blockEntity;

        public BlockShakerContentsAccess(ShakerBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public ItemStackHandler getStorage() {
            return blockEntity.getStorage();
        }

        @Override
        public ItemStack getResult() {
            return blockEntity.getResult();
        }

        @Override
        public void setStorage(ItemStackHandler handler) {
            blockEntity.setStorage(handler);
        }

        @Override
        public void setResult(ItemStack result) {
            blockEntity.setResult(result);
        }

        @Override
        public void clear() {
            setStorage(new ItemStackHandler(ShakerIngredientConversions.INPUT_SLOTS));
            setResult(ItemStack.EMPTY);
            markChanged();
        }

        @Override
        public void markChanged() {
            blockEntity.setChanged();
            if (blockEntity.getLevel() != null) {
                blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(),
                    blockEntity.getBlockState(), net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
            }
        }
    }
}
