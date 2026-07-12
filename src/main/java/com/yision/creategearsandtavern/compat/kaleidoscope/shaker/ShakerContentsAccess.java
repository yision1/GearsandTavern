package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.mixology.ShakerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

public interface ShakerContentsAccess {
    ItemStackHandler getStorage();

    ItemStack getResult();

    void setStorage(ItemStackHandler handler);

    void setResult(ItemStack result);

    void clear();

    void markChanged();

    boolean isBlockForm();

    final class ItemShakerContentsAccess implements ShakerContentsAccess {
        private final ItemStack stack;

        public ItemShakerContentsAccess(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStackHandler getStorage() {
            return ShakerItem.getStorage(stack);
        }

        @Override
        public ItemStack getResult() {
            return ShakerItem.getResult(stack);
        }

        @Override
        public void setStorage(ItemStackHandler handler) {
            ShakerItem.setStorage(stack, handler);
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

        @Override
        public boolean isBlockForm() {
            return false;
        }

        public ItemStack stack() {
            return stack;
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
            ItemStackHandler empty = new ItemStackHandler(ShakerIngredientConversions.INPUT_SLOTS);
            setStorage(empty);
            setResult(ItemStack.EMPTY);
            markChanged();
        }

        @Override
        public void markChanged() {
            blockEntity.setChanged();
            syncToClient();
        }

        private void syncToClient() {
            var level = blockEntity.getLevel();
            if (level == null) {
                return;
            }
            var pos = blockEntity.getBlockPos();
            var state = blockEntity.getBlockState();
            level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }

        @Override
        public boolean isBlockForm() {
            return true;
        }

        public ShakerBlockEntity blockEntity() {
            return blockEntity;
        }
    }
}
