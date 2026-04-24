package com.yision.creategearsandtavern.content.fluids.drink;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class KaleidoscopeDrinkVariant {
    private final ResourceLocation drinkId;
    private final int brewLevel;

    public KaleidoscopeDrinkVariant(ResourceLocation drinkId, int brewLevel) {
        this.drinkId = drinkId;
        this.brewLevel = brewLevel;
    }

    public ResourceLocation drinkId() {
        return drinkId;
    }

    public int brewLevel() {
        return brewLevel;
    }

    public CGTDrinkDefinition definition() {
        return CGTDrinkCatalog.byDrinkId(drinkId);
    }

    public String qualityTranslationKey() {
        return "message.kaleidoscope_tavern.barrel.brew_level." + brewLevel;
    }

    public static KaleidoscopeDrinkVariant of(KaleidoscopeDrinkType drinkType, int brewLevel) {
        return new KaleidoscopeDrinkVariant(drinkType.id(), brewLevel);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("drink_id", drinkId.toString());
        tag.putInt("brew_level", brewLevel);
        return tag;
    }

    public static KaleidoscopeDrinkVariant fromTag(CompoundTag tag) {
        ResourceLocation drinkId = new ResourceLocation(tag.getString("drink_id"));
        int brewLevel = tag.getInt("brew_level");
        return new KaleidoscopeDrinkVariant(drinkId, brewLevel);
    }
}
