package com.yision.creategearsandtavern.compat.create.filter;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class CGTItemAttributeTypes {
    public static final ItemAttributeType TAVERN_QUALITY = new TavernQualityAttribute.Type();

    private CGTItemAttributeTypes() {
    }

    public static void register(RegisterEvent event) {
        event.register(CreateRegistries.ITEM_ATTRIBUTE_TYPE, helper -> helper.register(
            ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID, "tavern_quality"),
            TAVERN_QUALITY
        ));
    }
}
