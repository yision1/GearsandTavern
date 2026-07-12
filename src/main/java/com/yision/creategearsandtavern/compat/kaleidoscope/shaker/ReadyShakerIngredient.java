package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.yision.creategearsandtavern.registry.CGTIngredientTypes;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public final class ReadyShakerIngredient implements ICustomIngredient {
    public static final MapCodec<ReadyShakerIngredient> CODEC = MapCodec.unit(new ReadyShakerIngredient());

    private static final ReadyShakerIngredient INSTANCE = new ReadyShakerIngredient();

    public static ReadyShakerIngredient get() {
        return INSTANCE;
    }

    private ReadyShakerIngredient() {
    }

    @Override
    public boolean test(@NotNull ItemStack stack) {
        return ShakerMixing.captureMixerResult(stack);
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return CGTIngredientTypes.READY_SHAKER.get();
    }
}
