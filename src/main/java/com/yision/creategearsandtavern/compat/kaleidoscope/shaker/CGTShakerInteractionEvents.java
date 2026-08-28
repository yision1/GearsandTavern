package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CGTShakerInteractionEvents {
    private CGTShakerInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() != ModItems.SHAKER.get() || !ShakerItem.hasResult(held)) {
            return;
        }
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlocks.EMPTY_GLASSWARE.get())) {
            return;
        }

        ItemStack result = ShakerItem.getResult(held);
        int amount = result.getTag() == null || !result.getTag().contains(ShakerMixing.RESULT_AMOUNT_TAG)
            ? ShakerIngredientConversions.SLOT_AMOUNT
            : result.getTag().getInt(ShakerMixing.RESULT_AMOUNT_TAG);
        if (amount < 1 || amount >= ShakerIngredientConversions.SLOT_AMOUNT) {
            return;
        }

        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setCanceled(true);
    }
}
