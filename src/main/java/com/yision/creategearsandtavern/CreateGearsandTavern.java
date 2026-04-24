package com.yision.creategearsandtavern;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yision.creategearsandtavern.compat.jei.CGTKdwDrinkEffectReloadListener;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTBlockEntityCapabilities;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTItemCapabilities;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeBarrelFluids;
import com.yision.creategearsandtavern.datagen.DataGenerators;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateGearsandTavern.MOD_ID)
public class CreateGearsandTavern {
    public static final String MOD_ID = "creategearsandtavern";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreateGearsandTavern() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CreateGearsAndTavernRegistrate.registrate().registerEventListeners(modEventBus);

        CGTFluids.register();

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onAttachItemStackCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::onAttachBlockEntityCapabilities);
        MinecraftForge.EVENT_BUS.addListener(CGTKdwDrinkEffectReloadListener::onAddReloadListenerEvent);

        modEventBus.addListener(this::onFMLCommonSetup);
        modEventBus.addListener(DataGenerators::gatherData);
    }

    private void onAttachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        CGTItemCapabilities.onAttachCapabilities(event);
    }

    private void onAttachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        CGTBlockEntityCapabilities.onAttachCapabilities(event);
    }

    private void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CGTKaleidoscopeBarrelFluids.registerCreateCompat();
        });
    }
}
