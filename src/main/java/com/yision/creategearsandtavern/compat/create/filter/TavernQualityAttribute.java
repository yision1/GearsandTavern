package com.yision.creategearsandtavern.compat.create.filter;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.item.DrinkBlockItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record TavernQualityAttribute(int brewLevel) implements ItemAttribute {
    public static final MapCodec<TavernQualityAttribute> CODEC =
        Codec.intRange(IBarrel.BREWING_STARTED, IBarrel.BREWING_FINISHED)
            .xmap(TavernQualityAttribute::new, TavernQualityAttribute::brewLevel)
            .fieldOf("brew_level");

    public static final StreamCodec<ByteBuf, TavernQualityAttribute> STREAM_CODEC = ByteBufCodecs.VAR_INT
        .map(TavernQualityAttribute::new, TavernQualityAttribute::brewLevel);

    private static int extractBrewLevel(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof DrinkBlockItem)) {
            return -1;
        }
        int brewLevel = stack.getOrDefault(ModDataComponents.BREW_LEVEL.get(), 0);
        return brewLevel >= IBarrel.BREWING_STARTED && brewLevel <= IBarrel.BREWING_FINISHED ? brewLevel : -1;
    }

    @Override
    public boolean appliesTo(ItemStack stack, Level level) {
        return extractBrewLevel(stack) == brewLevel;
    }

    @Override
    public ItemAttributeType getType() {
        return CGTItemAttributeTypes.TAVERN_QUALITY;
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

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
