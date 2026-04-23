package com.yision.creategearsandtavern.content.fluids.drink;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record KaleidoscopeDrinkVariant(ResourceLocation drinkId, int brewLevel) {
    public static final Codec<KaleidoscopeDrinkVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("drink_id").forGetter(KaleidoscopeDrinkVariant::drinkId),
        Codec.INT.fieldOf("brew_level").forGetter(KaleidoscopeDrinkVariant::brewLevel)
    ).apply(instance, KaleidoscopeDrinkVariant::new));

    public static final StreamCodec<ByteBuf, KaleidoscopeDrinkVariant> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, KaleidoscopeDrinkVariant::drinkId,
        ByteBufCodecs.VAR_INT, KaleidoscopeDrinkVariant::brewLevel,
        KaleidoscopeDrinkVariant::new
    );

    public static KaleidoscopeDrinkVariant of(KaleidoscopeDrinkType drinkType, int brewLevel) {
        return new KaleidoscopeDrinkVariant(drinkType.id(), brewLevel);
    }

    public CGTDrinkDefinition definition() {
        return CGTDrinkCatalog.byDrinkId(drinkId);
    }

    public String qualityTranslationKey() {
        return "message.kaleidoscope_tavern.barrel.brew_level." + brewLevel;
    }
}
