package com.yision.creategearsandtavern.mixin.kaleidoscope;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarCabinetBlock;
import com.yision.creategearsandtavern.compat.kaleidoscope.cabinet.BarCabinetLineCache;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BarCabinetBlockBehaviourMixin {
    @Inject(method = "onRemove", at = @At("HEAD"))
    private void cgt$invalidateCabinetLineOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
                                                  boolean isMoving, CallbackInfo ci) {
        if (!level.isClientSide && state.getBlock() instanceof BarCabinetBlock
            && state.getBlock() != newState.getBlock()) {
            BarCabinetLineCache.invalidateAround(level, pos);
        }
    }
}
