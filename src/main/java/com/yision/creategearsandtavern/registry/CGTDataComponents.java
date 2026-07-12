package com.yision.creategearsandtavern.registry;

import java.util.function.UnaryOperator;

import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.mojang.serialization.Codec;

public class CGTDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateGearsandTavern.MOD_ID);

    public static final DataComponentType<KaleidoscopeDrinkVariant> KALEIDOSCOPE_DRINK_VARIANT = register(
        "kaleidoscope_drink_variant",
        builder -> builder.persistent(KaleidoscopeDrinkVariant.CODEC).networkSynchronized(KaleidoscopeDrinkVariant.STREAM_CODEC)
    );

    public static final DataComponentType<Integer> SHAKER_COCKTAIL_AMOUNT = register(
        "shaker_cocktail_amount",
        builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
