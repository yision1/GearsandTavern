package com.yision.creategearsandtavern;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yision.creategearsandtavern.compat.create.arm.CGTArmInteractionPointTypes;
import com.yision.creategearsandtavern.compat.jei.CGTExtraDrinkEffectReloadListener;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTBlockEntityCapabilities;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTItemCapabilities;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeBarrelFluids;
import com.yision.creategearsandtavern.compat.kaleidoscope.CGTKaleidoscopeSchematicRequirements;
import com.yision.creategearsandtavern.compat.kaleidoscope.cabinet.CGTKaleidoscopeBarCabinets;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerInteractionEvents;
import com.yision.creategearsandtavern.datagen.DataGenerators;
import com.yision.creategearsandtavern.registry.CGTFluids;
import com.yision.creategearsandtavern.registry.CGTRecipeSerializers;

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
        CGTRecipeSerializers.register(modEventBus);

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onAttachItemStackCapabilities);
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::onAttachBlockEntityCapabilities);
        MinecraftForge.EVENT_BUS.addListener(CGTExtraDrinkEffectReloadListener::onAddReloadListenerEvent);
        MinecraftForge.EVENT_BUS.register(CGTShakerInteractionEvents.class);

        modEventBus.addListener(this::onFMLCommonSetup);
        modEventBus.addListener(DataGenerators::gatherData);
        // 动力臂交互点必须在 Create 内置 registry 冻结和 ArmInteractionPointType.init() 排序前注册，
        // 因此直接挂在 RegisterEvent 监听器里，不放进 enqueueWork。
        modEventBus.addListener(CGTArmInteractionPointTypes::register);
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
            CGTKaleidoscopeBarCabinets.registerCreateCompat();
            CGTKaleidoscopeSchematicRequirements.registerCreateCompat();
        });
    }
}
