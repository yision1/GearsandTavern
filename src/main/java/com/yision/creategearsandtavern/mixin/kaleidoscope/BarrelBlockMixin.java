package com.yision.creategearsandtavern.mixin.kaleidoscope;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin {
    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void cgt$createBlockEntityForEveryPart(BlockPos pos, BlockState state,
                                                   CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new BarrelBlockEntity(pos, state));
    }
}
