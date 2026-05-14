package com.yision.creategearsandtavern.mixin.kaleidoscope;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelParts;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelProxy;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelSides;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Mixin(BarrelBlock.class)
public abstract class BarrelBlockMixin extends BaseEntityBlock {
    protected BarrelBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void cgt$createBlockEntityForEveryPart(BlockPos pos, BlockState state,
                                                   CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new BarrelBlockEntity(pos, state));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide) {
            return;
        }
        cgt$syncRedstoneLid(level, BarrelBlock.getOriginPos(pos, state));
    }

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    private void cgt$syncLidAfterPlacement(Level level, BlockPos pos, BlockState state,
                                           @Nullable LivingEntity entity, ItemStack stack, CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }
        cgt$syncRedstoneLid(level, BarrelBlock.getOriginPos(pos, state));
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void cgt$lockLidInteractionWhenPowered(BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hitResult,
                                                   CallbackInfoReturnable<InteractionResult> cir) {
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        if (state.getValue(BarrelBlock.LAYER) != AttachFace.CEILING) {
            return;
        }
        BlockPos origin = BarrelBlock.getOriginPos(pos, state);
        if (!cgt$isRedstonePowered(level, origin)) {
            return;
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private static void cgt$syncRedstoneLid(Level level, BlockPos origin) {
        if (!(level.getBlockEntity(origin) instanceof BarrelBlockEntity barrel)) {
            return;
        }
        if (!(barrel instanceof KaleidoscopeBarrelProxy proxy)) {
            return;
        }

        boolean powered = cgt$hasAllowedRedstoneSignal(level, origin);
        if (powered == proxy.cgt$isRedstonePowered()) {
            return;
        }

        proxy.cgt$setRedstonePowered(powered);
        if (powered) {
            barrel.closeLid(null);
        } else {
            barrel.openLid(null);
        }
        barrel.setChanged();
    }

    @Unique
    private static boolean cgt$isRedstonePowered(Level level, BlockPos origin) {
        if (level.getBlockEntity(origin) instanceof BarrelBlockEntity barrel
            && barrel instanceof KaleidoscopeBarrelProxy proxy) {
            return proxy.cgt$isRedstonePowered();
        }
        return cgt$hasAllowedRedstoneSignal(level, origin);
    }

    @Unique
    private static boolean cgt$hasAllowedRedstoneSignal(Level level, BlockPos origin) {
        for (BlockPos partPos : KaleidoscopeBarrelParts.positions(origin)) {
            BlockState partState = level.getBlockState(partPos);
            if (!(partState.getBlock() instanceof BarrelBlock)) {
                continue;
            }
            for (Direction side : Direction.values()) {
                if (!KaleidoscopeBarrelSides.isAllowedAutomationSide(partState, side)) {
                    continue;
                }
                if (level.getSignal(partPos.relative(side), side) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
