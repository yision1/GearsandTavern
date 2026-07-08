package com.yision.creategearsandtavern.compat.create.arm;

import java.util.ArrayList;
import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.block.plant.GrapeCropBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
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

/**
 * 把葡萄藤架下方成熟葡萄伪装成动力臂可抽取的单槽输入点。
 *
 * <p>该交互点绑定到永久藤架 {@code pos}，运行时检查下方一格
 * {@link GrapeCropBlock} 是否成熟：</p>
 * <ul>
 *   <li>下方不是葡萄作物 / 未成熟：无可抽取物。</li>
 *   <li>下方葡萄成熟：按 KT 当前方块掉落表收割，清掉作物方块，
 *       把所有掉落缓存到交互点中，动力臂每轮只能拿一个 {@link ItemStack}。</li>
 *   <li>缓存中剩余掉落（如青提）在后续轮次继续被抽取。</li>
 * </ul>
 *
 * <p>始终处于 {@link Mode#TAKE}；{@link #insert} 原样返回输入栈，避免被配置成输出点。
 * 收割只清掉下方作物，保留上方藤架，KT 后续生长逻辑仍会重新生成葡萄作物。</p>
 */
public class GrapevineTrellisHarvestPoint extends ArmInteractionPoint {
	/** 尚未被动力臂取走的缓存掉落。 */
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
	public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
		// 不支持把物品插入藤架，原样返回。
		return stack;
	}

	@Override
	public int getSlotCount(ArmBlockEntity armBlockEntity) {
		// 有缓存掉落时一定可抽取；否则下方成熟葡萄才暴露一个槽位。
		if (!pendingDrops.isEmpty()) {
			return 1;
		}
		return hasMatureCropBelow() ? 1 : 0;
	}

	@Override
	public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
		// 缓存中还有上一轮没收完的掉落，直接从缓存切出。
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
			// 预览：不破坏方块，按当前掉落表取第一组掉落。
			List<ItemStack> preview = Block.getDrops(cropState, serverLevel, cropPos, null);
			for (ItemStack stack : preview) {
				if (!stack.isEmpty()) {
					return stack.copyWithCount(Math.min(amount, stack.getCount()));
				}
			}
			return ItemStack.EMPTY;
		}

		// 执行：走 Create 的 BlockHelper.destroyBlock，复用 Block.getDrops、BlockDropsEvent
		// 与 doBlockDrops 游戏规则，语义与 Create 收割机接近。
		performHarvest(cropPos);
		return takeFromPending(amount, false);
	}

	@Override
	protected Vec3 getInteractionPositionVector() {
		// 指向藤架下方作物中心，让动力臂伸向垂挂葡萄而不是藤架木架本体。
		return Vec3.atLowerCornerOf(pos.below()).add(0.5, 0.75, 0.5);
	}

	@Override
	protected void serialize(CompoundTag nbt, BlockPos anchor) {
		super.serialize(nbt, anchor);
		nbt.put("PendingDrops", NBTHelper.writeItemList(pendingDrops, level.registryAccess()));
	}

	@Override
	protected void deserialize(CompoundTag nbt, BlockPos anchor) {
		super.deserialize(nbt, anchor);
		pendingDrops.clear();
		if (nbt.contains("PendingDrops", Tag.TAG_LIST)) {
			ListTag list = nbt.getList("PendingDrops", Tag.TAG_COMPOUND);
			pendingDrops.addAll(NBTHelper.readItemList(list, level.registryAccess()));
			pendingDrops.removeIf(ItemStack::isEmpty);
		}
		// 反序列化后再次强制 TAKE。
		mode = Mode.TAKE;
	}

	private boolean hasMatureCropBelow() {
		BlockState cropState = level.getBlockState(pos.below());
		return cropState.getBlock() instanceof GrapeCropBlock cropBlock && cropBlock.isMaxAge(cropState);
	}

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

	private void performHarvest(BlockPos cropPos) {
		BlockHelper.destroyBlock(level, cropPos, 1.0f, stack -> {
			if (!stack.isEmpty()) {
				pendingDrops.add(stack.copy());
			}
		});
	}

	private ItemStack takeFromPending(int amount, boolean simulate) {
		for (int i = 0; i < pendingDrops.size(); i++) {
			ItemStack stack = pendingDrops.get(i);
			if (stack.isEmpty()) {
				continue;
			}
			int extracted = Math.min(amount, stack.getCount());
			ItemStack result = stack.copyWithCount(extracted);
			if (!simulate) {
				stack.shrink(extracted);
				if (stack.isEmpty()) {
					pendingDrops.remove(i);
				}
			}
			return result;
		}
		return ItemStack.EMPTY;
	}
}
