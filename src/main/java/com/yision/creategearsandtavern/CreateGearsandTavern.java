package com.yision.creategearsandtavern;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeBarrelFluids;
import com.yision.creategearsandtavern.datagen.DataGenerators;
import com.yision.creategearsandtavern.registry.CGTDataComponents;
import com.yision.creategearsandtavern.registry.CGTFluids;
import com.yision.creategearsandtavern.registry.CGTItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CreateGearsandTavern.MOD_ID)
public class CreateGearsandTavern {
    public static final String MOD_ID = "creategearsandtavern";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateGearsandTavern(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(CGTItems::registerCapabilities);
        modEventBus.addListener(CGTItems::registerCapabilitiesForKdw);
        modEventBus.addListener(CGTKaleidoscopeBarrelFluids::registerCapabilities);
        modEventBus.addListener(CreateGearsandTavern::commonSetup);
        CreateGearsAndTavernRegistrate.registrate().registerEventListeners(modEventBus);
        CGTDataComponents.register(modEventBus);
        CGTItems.register(modEventBus);
        CGTFluids.register();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CGTKaleidoscopeBarrelFluids::registerCreateCompat);
    }
}
