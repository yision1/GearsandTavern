package com.yision.creategearsandtavern.mixin.kaleidoscope;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarCabinetBlock;
import com.yision.creategearsandtavern.compat.kaleidoscope.cabinet.BarCabinetLineCache;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarCabinetBlock.class)
public class BarCabinetBlockMixin {
    @Inject(method = "updateShape", at = @At("RETURN"))
    private void cgt$invalidateCabinetLineOnShapeUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                       LevelAccessor level, BlockPos pos, BlockPos neighborPos,
                                                       CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof Level realLevel && !realLevel.isClientSide) {
            BarCabinetLineCache.invalidateAround(realLevel, pos);
            BarCabinetLineCache.invalidateAround(realLevel, neighborPos);
        }
    }
}
