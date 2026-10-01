package com.yision.creategearsandtavern.compat.create.filter;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;

public final class CGTItemAttributeTypes {
    public static final ItemAttributeType TAVERN_QUALITY = new TavernQualityAttribute.Type();
    private static boolean registered;

    private CGTItemAttributeTypes() {
    }

    public static void register(RegisterEvent event) {
        if (registered) {
            return;
        }
        Registry.register(
            CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE,
            new ResourceLocation(CreateGearsandTavern.MOD_ID, "tavern_quality"),
            TAVERN_QUALITY
        );
        registered = true;
    }
}
