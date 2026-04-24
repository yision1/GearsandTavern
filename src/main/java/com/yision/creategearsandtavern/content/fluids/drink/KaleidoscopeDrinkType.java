package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public enum KaleidoscopeDrinkType {
    WINE("wine", "block.kaleidoscope_tavern.wine", 0x7b2442),
    CHAMPAGNE("champagne", "block.kaleidoscope_tavern.champagne", 0xefd58c),
    VODKA("vodka", "block.kaleidoscope_tavern.vodka", 0xe9f3f9),
    BRANDY("brandy", "block.kaleidoscope_tavern.brandy", 0xa85b29),
    CARIGNAN("carignan", "block.kaleidoscope_tavern.carignan", 0x5e1d34),
    SAKURA_WINE("sakura_wine", "block.kaleidoscope_tavern.sakura_wine", 0xe695b6),
    PLUM_WINE("plum_wine", "block.kaleidoscope_tavern.plum_wine", 0xc9ad6a),
    WHISKEY("whiskey", "block.kaleidoscope_tavern.whiskey", 0xad7a2a),
    ICE_WINE("ice_wine", "block.kaleidoscope_tavern.ice_wine", 0xf6d768),
    VINEGAR("vinegar", "block.kaleidoscope_tavern.vinegar", 0xd2b45d);

    public static final String NAMESPACE = "kaleidoscope_tavern";

    private static final Map<ResourceLocation, KaleidoscopeDrinkType> BY_ID = Arrays.stream(values())
        .collect(Collectors.toUnmodifiableMap(KaleidoscopeDrinkType::id, Function.identity()));

    private final String path;
    private final String translationKey;
    private final int color;

    KaleidoscopeDrinkType(String path, String translationKey, int color) {
        this.path = path;
        this.translationKey = translationKey;
        this.color = color;
    }

    public ResourceLocation id() {
        return new ResourceLocation(NAMESPACE, path);
    }

    public String path() {
        return path;
    }

    public String translationKey() {
        return translationKey;
    }

    public int color() {
        return color;
    }

    public static KaleidoscopeDrinkType byId(ResourceLocation id) {
        KaleidoscopeDrinkType type = BY_ID.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown Kaleidoscope Tavern drink id: " + id);
        }
        return type;
    }

    public static KaleidoscopeDrinkType byFluid(Fluid fluid) {
        ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(fluid);
        return byId(new ResourceLocation(NAMESPACE, fluidId.getPath()));
    }
}
