package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.foundation.ICapabilityProvider;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Mixin(FlowSource.FluidHandler.class)
public class FlowSourceFluidHandlerMixin {
    @Inject(method = "manageSource", at = @At("HEAD"), cancellable = true)
    private void cgt$allowBlockCapabilityWithoutBlockEntity(Level level, BlockEntity networkBE, CallbackInfo ci) {
        FlowSourceFluidHandlerAccessor fluidHandlerAccessor = (FlowSourceFluidHandlerAccessor) this;
        if (fluidHandlerAccessor.cgt$getFluidHandlerCache() != null) {
            return;
        }
        BlockFace location = ((FlowSourceAccessor) this).cgt$getLocation();
        BlockPos connectedPos = location.getConnectedPos();
        if (level.getBlockEntity(connectedPos) != null) {
            return;
        }
        ICapabilityProvider<IFluidHandler> provider = ICapabilityProvider.of(
            () -> level.getCapability(Capabilities.FluidHandler.BLOCK, connectedPos, location.getOppositeFace()));
        fluidHandlerAccessor.cgt$setFluidHandlerCache(provider);
        ci.cancel();
    }
}
