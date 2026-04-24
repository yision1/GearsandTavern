package com.yision.creategearsandtavern.compat.jei;

import java.util.Map;

import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.resources.DrinkEffectDataReloadListener;

import net.minecraft.world.item.Item;

final class CGTDrinkEffectDataAccess {
    private CGTDrinkEffectDataAccess() {
    }

    static DrinkEffectData get(Item item) {
        return get(item, DrinkEffectDataReloadListener.INSTANCE, CGTKdwDrinkEffectReloadListener.INSTANCE);
    }

    static <K, V> V get(K key, Map<K, V> primary, Map<K, V> fallback) {
        V value = primary.get(key);
        return value != null ? value : fallback.get(key);
    }
}
