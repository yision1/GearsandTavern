package com.yision.creategearsandtavern.compat.create.arm;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegisterEvent;

/**
 * 把 CGT 的动力臂交互点类型写入 Create 6.0.8 的内置 registry。
 *
 * <p>Create 自己的内置类型在 {@code AllArmInteractionPointTypes} 的 static 块里用
 * {@link Registry#register} 写入 {@link CreateBuiltInRegistries#ARM_INTERACTION_POINT_TYPE}，
 * 因此 CGT 也走同样的注册路径，不依赖 NeoForge 的 registry helper。</p>
 *
 * <p>注册必须在 registry 冻结和 {@code ArmInteractionPointType.init()} 排序前完成，
 * 因此挂在 {@link RegisterEvent} 监听器里立即执行，不要放进 enqueueWork。</p>
 */
public final class CGTArmInteractionPointTypes {
	private static final ArmInteractionPointType GRAPEVINE_TRELLIS_HARVEST =
		new GrapevineTrellisHarvestPointType();
	private static boolean registered;

	private CGTArmInteractionPointTypes() {
	}

	public static void register(RegisterEvent event) {
		if (registered) {
			return;
		}
		registered = true;
		Registry.register(
			CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE,
			new ResourceLocation(CreateGearsandTavern.MOD_ID, "grapevine_trellis_harvest"),
			GRAPEVINE_TRELLIS_HARVEST
		);
	}
}
