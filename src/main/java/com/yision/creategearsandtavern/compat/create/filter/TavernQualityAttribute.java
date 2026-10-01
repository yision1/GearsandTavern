package com.yision.creategearsandtavern.compat.create.filter;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.DrinkBlockItem;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TavernQualityAttribute implements ItemAttribute {
    private int brewLevel;

    public TavernQualityAttribute(int brewLevel) {
        this.brewLevel = brewLevel;
    }

    private static int extractBrewLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (stack.isEmpty() || !(stack.getItem() instanceof DrinkBlockItem) || tag == null) {
            return -1;
        }
        int brewLevel = tag.getInt(BottleBlockItem.BREW_LEVEL_KEY);
        return brewLevel >= IBarrel.BREWING_STARTED && brewLevel <= IBarrel.BREWING_FINISHED ? brewLevel : -1;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level level) {
        return brewLevel >= IBarrel.BREWING_STARTED && extractBrewLevel(stack) == brewLevel;
    }

    @Override
    public ItemAttributeType getType() {
        return CGTItemAttributeTypes.TAVERN_QUALITY;
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putInt("brew_level", brewLevel);
    }

    @Override
    public void load(CompoundTag nbt) {
        brewLevel = nbt.getInt("brew_level");
    }

    @Override
    public String getTranslationKey() {
        return CreateGearsandTavern.MOD_ID + ".tavern_quality";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{Component.translatable("message.kaleidoscope_tavern.barrel.brew_level." + brewLevel)};
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new TavernQualityAttribute(IBarrel.BREWING_STARTED);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            int brewLevel = extractBrewLevel(stack);
            return brewLevel < IBarrel.BREWING_STARTED ? List.of() : List.of(new TavernQualityAttribute(brewLevel));
        }
    }
}
