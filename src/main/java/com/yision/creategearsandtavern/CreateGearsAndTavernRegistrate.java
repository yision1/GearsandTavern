package com.yision.creategearsandtavern;

import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class CreateGearsAndTavernRegistrate {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateGearsandTavern.MOD_ID)
        .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}
