package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.mixology.ShakerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public final class CGTKaleidoscopeShakerFluids {
    private CGTKaleidoscopeShakerFluids() {
    }

    public static IFluidHandlerItem createItemHandler(ItemStack stack) {
        return stack.getItem() == ModItems.SHAKER.get()
            ? new ShakerFluidHandler(new ShakerContentsAccess.ItemShakerContentsAccess(stack), stack)
            : null;
    }

    public static ShakerFluidHandler createBlockHandler(ShakerBlockEntity blockEntity) {
        return new ShakerFluidHandler(new ShakerContentsAccess.BlockShakerContentsAccess(blockEntity),
            new ItemStack(ModItems.SHAKER.get()));
    }
}
