package com.yision.creategearsandtavern.compat.create.arm;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapeCropBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 KT 藤架下方的成熟葡萄伪装成动力臂可抽取的单槽输入点。
 *
 * <p>交互点本身绑定在永久藤架上（{@code pos} 是藤架坐标），实际抽取时收割下方一格的成熟
 * {@link GrapeCropBlock}。一次收割可能产生多组掉落，多余的部分缓存在 {@link #pendingDrops}，
 * 动力臂每轮只取一组，剩余在后续轮次继续输出；缓存会随交互点序列化保存。</p>
 *
 * <p>这个点是只读输入点：永远保持 {@link Mode#TAKE}，{@link #insert} 直接拒绝。收获复用 KT
 * 的方块掉落表，相当于破坏成熟下挂葡萄作物（只清下方作物、保留上方藤架），不模拟剪刀右键，不消耗工具。</p>
 */
public class GrapevineTrellisHarvestPoint extends ArmInteractionPoint {
	private final List<ItemStack> pendingDrops = new ArrayList<>();

	public GrapevineTrellisHarvestPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
		super(type, level, pos, state);
		mode = Mode.TAKE;
	}

	@Override
	public void cycleMode() {
		mode = Mode.TAKE;
	}

	@Override
	public ItemStack insert(ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public int getSlotCount() {
		if (!pendingDrops.isEmpty()) {
			return 1;
		}
		return hasMatureCropBelow() && getServerLevelIfDropsAllowed() != null ? 1 : 0;
	}

	@Override
	public ItemStack extract(int slot, int amount, boolean simulate) {
		if (!pendingDrops.isEmpty()) {
			return takeFromPending(amount, simulate);
		}

		BlockPos cropPos = pos.below();
		BlockState cropState = level.getBlockState(cropPos);
		if (!(cropState.getBlock() instanceof GrapeCropBlock cropBlock) || !cropBlock.isMaxAge(cropState)) {
			return ItemStack.EMPTY;
		}

		ServerLevel serverLevel = getServerLevelIfDropsAllowed();
		if (serverLevel == null) {
			return ItemStack.EMPTY;
		}

		if (simulate) {
			List<ItemStack> preview = Block.getDrops(cropState, serverLevel, cropPos, null, null, ItemStack.EMPTY);
			for (ItemStack stack : preview) {
				if (!stack.isEmpty()) {
					return copyWithCount(stack, Math.min(amount, stack.getCount()));
				}
			}
			return ItemStack.EMPTY;
		}

		performHarvest(cropPos);
		return takeFromPending(amount, false);
	}

	@Override
	protected Vec3 getInteractionPositionVector() {
		return Vec3.atLowerCornerOf(pos.below()).add(0.5, 0.75, 0.5);
	}

	@Override
	protected void serialize(CompoundTag nbt, BlockPos anchor) {
		super.serialize(nbt, anchor);
		nbt.put("PendingDrops", NBTHelper.writeItemList(pendingDrops));
	}

	@Override
	protected void deserialize(CompoundTag nbt, BlockPos anchor) {
		super.deserialize(nbt, anchor);
		pendingDrops.clear();
		if (nbt.contains("PendingDrops", Tag.TAG_LIST)) {
			pendingDrops.addAll(NBTHelper.readItemList(nbt.getList("PendingDrops", Tag.TAG_COMPOUND)));
			pendingDrops.removeIf(ItemStack::isEmpty);
		}
		mode = Mode.TAKE;
	}

	/**
	 * 下方一格是否是成熟的 {@link GrapeCropBlock}。
	 */
	private boolean hasMatureCropBelow() {
		BlockState cropState = level.getBlockState(pos.below());
		if (!(cropState.getBlock() instanceof GrapeCropBlock cropBlock)) {
			return false;
		}
		return cropBlock.isMaxAge(cropState);
	}

	/**
	 * 镜像 Create 6.0.8 {@code BlockHelper.destroyBlockAs} 的基础掉落门槛：
	 * 必须是 {@link ServerLevel}，开启 doBlockDrops，且不在恢复快照过程中。
	 * 不满足时模拟和真实抽取都返回空，也不清掉作物。
	 */
	private ServerLevel getServerLevelIfDropsAllowed() {
		if (!(level instanceof ServerLevel serverLevel)) {
			return null;
		}
		if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
			return null;
		}
		if (serverLevel.restoringBlockSnapshots) {
			return null;
		}
		return serverLevel;
	}

	/**
	 * 真实收割：复用 Create 6.0.8 的 {@link BlockHelper#destroyBlock}，把所有掉落收集进
	 * {@link #pendingDrops}。{@code BlockHelper.destroyBlock} 内部已重新校验 doBlockDrops /
	 * 快照门槛，这里再校验一次只是为了让 {@link #extract} 提前短路。
	 */
	private void performHarvest(BlockPos cropPos) {
		BlockHelper.destroyBlock(level, cropPos, 1.0f, stack -> {
			if (!stack.isEmpty()) {
				pendingDrops.add(stack.copy());
			}
		});
	}

	/**
	 * 从缓存掉落中切出最多 {@code amount} 个物品的第一组。
	 */
	private ItemStack takeFromPending(int amount, boolean simulate) {
		for (ItemStack pending : pendingDrops) {
			if (pending.isEmpty()) {
				continue;
			}
			ItemStack taken = copyWithCount(pending, Math.min(amount, pending.getCount()));
			if (!simulate) {
				pending.shrink(taken.getCount());
				pendingDrops.removeIf(ItemStack::isEmpty);
			}
			return taken;
		}
		return ItemStack.EMPTY;
	}

	private static ItemStack copyWithCount(ItemStack stack, int count) {
		ItemStack copy = stack.copy();
		copy.setCount(count);
		return copy;
	}
}
