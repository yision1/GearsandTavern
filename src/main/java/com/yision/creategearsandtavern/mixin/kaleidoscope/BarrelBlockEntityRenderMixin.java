package com.yision.creategearsandtavern.mixin.kaleidoscope;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.client.render.block.BarrelBlockEntityRender;

import net.minecraft.world.level.block.state.BlockState;

@Mixin(BarrelBlockEntityRender.class)
public class BarrelBlockEntityRenderMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cgt$renderOnlyController(BarrelBlockEntity barrel, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                          net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight,
                                          int packedOverlay, CallbackInfo ci) {
        BlockState state = barrel.getBlockState();
        if (!(state.getBlock() instanceof BarrelBlock)) {
            return;
        }
        if (!BarrelBlock.getOriginPos(barrel.getBlockPos(), state).equals(barrel.getBlockPos())) {
            ci.cancel();
        }
    }
}
