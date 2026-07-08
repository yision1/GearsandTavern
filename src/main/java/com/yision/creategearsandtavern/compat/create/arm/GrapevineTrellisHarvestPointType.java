package com.yision.creategearsandtavern.compat.create.arm;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapevineTrellisBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 判断哪些方块能成为动力臂目标，并创建实际交互点。
 *
 * <p>只把永久结构 {@link GrapevineTrellisBlock}（普通 / 冰 / 黄金葡萄藤架）作为动力臂目标，
 * 不在这里检查下方 {@code GrapeCropBlock} 是否成熟——动力臂选择应绑定到永久藤架，
 * 成熟性只影响当前是否可抽取，避免临时作物消失导致动力臂丢失选择。</p>
 */
public class GrapevineTrellisHarvestPointType extends ArmInteractionPointType {
	@Override
	public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
		return state.getBlock() instanceof GrapevineTrellisBlock;
	}

	@Override
	public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
		return new GrapevineTrellisHarvestPoint(this, level, pos, state);
	}

	@Override
	public int getPriority() {
		return 100;
	}
}
