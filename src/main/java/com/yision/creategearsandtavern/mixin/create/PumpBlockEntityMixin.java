package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.fluids.pump.PumpBlockEntity;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Mixin(PumpBlockEntity.class)
public class PumpBlockEntityMixin {
    @Inject(method = "hasReachedValidEndpoint", at = @At("HEAD"), cancellable = true)
    private void cgt$allowFluidCapabilityWithoutBlockEntity(LevelAccessor world, BlockFace blockFace, boolean pull,
                                                            CallbackInfoReturnable<Boolean> cir) {
        BlockPos connectedPos = blockFace.getConnectedPos();
        if (world.getBlockEntity(connectedPos) != null) {
            return;
        }
        if (!(world instanceof Level level)) {
            return;
        }
        Direction face = blockFace.getFace();
        IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, connectedPos, face.getOpposite());
        if (capability != null) {
            cir.setReturnValue(true);
        }
    }
}
