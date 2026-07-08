package com.yision.creategearsandtavern.compat.create.arm;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapevineTrellisBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 让动力臂绑定 KT 永久藤架 {@link GrapevineTrellisBlock}，而不是绑定会被收割清掉的下方葡萄作物。
 *
 * <p>{@link #canCreatePoint} 只检查方块是否是藤架本身，不检查下方是否成熟：交互点必须稳定绑定藤架，
 * 否则一次收割清掉下方作物后动力臂配置会失效。是否真的能抽取由 {@link GrapevineTrellisHarvestPoint} 运行时决定。</p>
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
