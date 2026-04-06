package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;

@Mixin(PackagerBlock.class)
public abstract class PackagerBlockMixin extends WrenchableDirectionalBlock {
    private static final ResourceLocation KALEIDOSCOPE_BARREL_ID =
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "barrel");

    protected PackagerBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void cgt$preferKaleidoscopeBarrel(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        Direction preferredFacing = cgt$getPreferredFacing(context, context.getClickedFace().getOpposite());

        if (preferredFacing == null) {
            for (Direction face : context.getNearestLookingDirections()) {
                preferredFacing = cgt$getPreferredFacing(context, face);
                if (preferredFacing != null) {
                    break;
                }
            }
        }

        if (preferredFacing == null) {
            return;
        }

        Player player = context.getPlayer();
        if (player != null && !(player instanceof FakePlayer)) {
            BlockPos checkPos = context.getClickedPos().relative(preferredFacing.getOpposite());
            if (AllBlocks.PORTABLE_STORAGE_INTERFACE.has(context.getLevel().getBlockState(checkPos))) {
                CreateLang.translate("packager.no_portable_storage").sendStatus(player);
                cir.setReturnValue(null);
                return;
            }
        }

        cir.setReturnValue(super.getStateForPlacement(context)
            .setValue(PackagerBlock.POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
            .setValue(PackagerBlock.FACING, preferredFacing));
    }

    private Direction cgt$getPreferredFacing(BlockPlaceContext context, Direction adjacentFace) {
        BlockPos targetPos = context.getClickedPos().relative(adjacentFace);
        BlockEntity be = context.getLevel().getBlockEntity(targetPos);
        if (be instanceof PackagerBlockEntity) {
            return null;
        }

        IItemHandler capability = null;
        Direction accessSide = adjacentFace.getOpposite();
        if (be != null && be.hasLevel()) {
            capability = be.getLevel().getCapability(ItemHandler.BLOCK, be.getBlockPos(), accessSide);
        } else {
            BlockState targetState = context.getLevel().getBlockState(targetPos);
            if (KALEIDOSCOPE_BARREL_ID.equals(BuiltInRegistries.BLOCK.getKey(targetState.getBlock()))) {
                capability = context.getLevel().getCapability(ItemHandler.BLOCK, targetPos, accessSide);
            }
        }

        return capability != null ? adjacentFace.getOpposite() : null;
    }
}
