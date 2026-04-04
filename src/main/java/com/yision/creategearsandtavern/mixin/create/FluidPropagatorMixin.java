package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.FluidPropagator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Mixin(FluidPropagator.class)
public class FluidPropagatorMixin {
    @Inject(method = "hasFluidCapability", at = @At("HEAD"), cancellable = true)
    private static void cgt$checkCapabilityWithoutBlockEntity(BlockGetter world, BlockPos pos, Direction side,
                                                              CallbackInfoReturnable<Boolean> cir) {
        if (!(world instanceof Level level)) {
            return;
        }
        IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
        if (capability != null) {
            cir.setReturnValue(true);
        }
    }
}

