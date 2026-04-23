package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;

public enum KaleidoscopeDrinkType {
    WINE("wine"),
    CHAMPAGNE("champagne"),
    VODKA("vodka"),
    BRANDY("brandy"),
    CARIGNAN("carignan"),
    SAKURA_WINE("sakura_wine"),
    PLUM_WINE("plum_wine"),
    WHISKEY("whiskey"),
    ICE_WINE("ice_wine"),
    VINEGAR("vinegar");

    public static final String NAMESPACE = "kaleidoscope_tavern";

    private static final Map<ResourceLocation, KaleidoscopeDrinkType> BY_ID = Arrays.stream(values())
        .collect(Collectors.toUnmodifiableMap(KaleidoscopeDrinkType::id, Function.identity()));

    private final ResourceLocation id;

    KaleidoscopeDrinkType(String path) {
        this.id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    public ResourceLocation id() {
        return id;
    }

    public static KaleidoscopeDrinkType byId(ResourceLocation id) {
        KaleidoscopeDrinkType type = BY_ID.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown Kaleidoscope Tavern drink id: " + id);
        }
        return type;
    }
}
