package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.yision.creategearsandtavern.registry.CGTDataComponents;

import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public final class CGTShakerInteractionEvents {
    private CGTShakerInteractionEvents() {
    }

    @SubscribeEvent
    public static void onUseItemOnBlock(UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (held.getItem() != ModItems.SHAKER.get()) {
            return;
        }
        if (!ShakerItem.hasResult(held)) {
            return;
        }
        var clickedState = event.getLevel().getBlockState(event.getPos());
        if (!clickedState.is(ModBlocks.EMPTY_GLASSWARE.get())) {
            return;
        }
        ItemStack result = ShakerItem.getResult(held);
        Integer amount = result.get(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT);
        if (amount == null || amount < 1 || amount > 249) {
            return;
        }
        event.cancelWithResult(ItemInteractionResult.FAIL);
    }
}
