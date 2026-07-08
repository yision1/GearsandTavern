package com.yision.creategearsandtavern.compat.create.arm;

import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * 注册 CGT 自定义的 Create 动力臂交互点类型。
 *
 * <p>{@code ARM_INTERACTION_POINT_TYPE} 是 Create 的内置 registry，通过 NeoForge
 * {@link RegisterEvent} 注册即可被 Create 的 registry bake 流程纳入
 * {@link ArmInteractionPointType#SORTED_TYPES_VIEW}。该监听器必须挂在 mod event bus 上，
 * 不能放到 {@code FMLCommonSetupEvent#enqueueWork} 中。</p>
 */
public final class CGTArmInteractionPointTypes {
	private static final ArmInteractionPointType GRAPEVINE_TRELLIS_HARVEST = new GrapevineTrellisHarvestPointType();

	private CGTArmInteractionPointTypes() {
	}

	public static void register(RegisterEvent event) {
		event.register(CreateRegistries.ARM_INTERACTION_POINT_TYPE, helper -> helper.register(
			ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID, "grapevine_trellis_harvest"),
			GRAPEVINE_TRELLIS_HARVEST
		));
	}
}
