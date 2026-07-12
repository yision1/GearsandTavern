package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.mixology.ShakerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class CGTKaleidoscopeShakerFluids {
    private static final ResourceLocation SHAKER_ITEM_ID = ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "shaker");
    private static final ResourceLocation SHAKER_BE_ID = ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "shaker");

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        var shakerItem = BuiltInRegistries.ITEM.get(SHAKER_ITEM_ID);
        if (shakerItem != null && shakerItem == ModItems.SHAKER.get()) {
            event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> createItemHandler(stack),
                shakerItem);
        }
        var shakerBeType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(SHAKER_BE_ID);
        if (shakerBeType != null && shakerBeType == ModBlocks.SHAKER_BE.get()) {
            registerBlockEntity(event, shakerBeType);
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerBlockEntity(RegisterCapabilitiesEvent event, BlockEntityType<?> type) {
        BlockEntityType<ShakerBlockEntity> typed = (BlockEntityType<ShakerBlockEntity>) type;
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, typed,
            (be, ctx) -> new ShakerFluidHandler(new ShakerContentsAccess.BlockShakerContentsAccess(be), new ItemStack(ModItems.SHAKER.get())));
    }

    private static IFluidHandlerItem createItemHandler(ItemStack stack) {
        return new ShakerFluidHandler(new ShakerContentsAccess.ItemShakerContentsAccess(stack), stack);
    }
}
