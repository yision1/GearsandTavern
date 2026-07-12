package com.yision.creategearsandtavern;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yision.creategearsandtavern.compat.create.arm.CGTArmInteractionPointTypes;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeBarrelFluids;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeSchematicRequirements;
import com.yision.creategearsandtavern.compat.kaleidoscope.cabinet.CGTKaleidoscopeBarCabinets;
import com.yision.creategearsandtavern.compat.kaleidoscope.cocktail.CocktailItemFluidHandlers;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTKaleidoscopeShakerFluids;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerInteractionEvents;
import com.yision.creategearsandtavern.datagen.DataGenerators;
import com.yision.creategearsandtavern.registry.CGTDataComponents;
import com.yision.creategearsandtavern.registry.CGTFluids;
import com.yision.creategearsandtavern.registry.CGTIngredientTypes;
import com.yision.creategearsandtavern.registry.CGTItems;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CreateGearsandTavern.MOD_ID)
public class CreateGearsandTavern {
    public static final String MOD_ID = "creategearsandtavern";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateGearsandTavern(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(DataGenerators::gatherData);
        modEventBus.addListener(CGTArmInteractionPointTypes::register);
        modEventBus.addListener(CGTItems::registerCapabilities);
        modEventBus.addListener(CGTItems::registerCapabilitiesForKdw);
        modEventBus.addListener(CGTItems::registerCapabilitiesForKt);
        modEventBus.addListener(CGTItems::registerCapabilitiesForKb);
        modEventBus.addListener(CGTKaleidoscopeBarrelFluids::registerCapabilities);
        modEventBus.addListener(CGTKaleidoscopeBarCabinets::registerCapabilities);
        modEventBus.addListener(CGTKaleidoscopeShakerFluids::registerCapabilities);
        modEventBus.addListener(CocktailItemFluidHandlers::registerCapabilities);
        modEventBus.addListener(CreateGearsandTavern::commonSetup);
        CreateGearsAndTavernRegistrate.registrate().registerEventListeners(modEventBus);
        CGTDataComponents.register(modEventBus);
        CGTRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        CGTIngredientTypes.INGREDIENT_TYPES.register(modEventBus);
        CGTIngredientTypes.FLUID_INGREDIENT_TYPES.register(modEventBus);
        CGTItems.register(modEventBus);
        CGTFluids.register();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CGTKaleidoscopeBarrelFluids::registerCreateCompat);
        event.enqueueWork(CGTKaleidoscopeBarCabinets::registerCreateCompat);
        event.enqueueWork(CGTKaleidoscopeSchematicRequirements::registerCreateCompat);
        event.enqueueWork(() -> NeoForge.EVENT_BUS.register(CGTShakerInteractionEvents.class));
    }
}
