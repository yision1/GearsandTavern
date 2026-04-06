package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

@Mixin(PackagerBlockEntity.class)
public class PackagerBlockEntityMixin {
    private static final ResourceLocation KALEIDOSCOPE_BARREL_ID =
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "barrel");

    @Shadow
    public InvManipulationBehaviour targetInventory;

    @Inject(method = "supportsBlockEntity", at = @At("HEAD"), cancellable = true)
    private void cgt$allowBlockItemCapabilityWithoutBlockEntity(BlockEntity target,
                                                                CallbackInfoReturnable<Boolean> cir) {
        if (target != null || targetInventory == null) {
            return;
        }

        PackagerBlockEntity self = (PackagerBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) {
            return;
        }

        BlockFace targetFace = targetInventory.getTarget().getOpposite();
        BlockState targetState = level.getBlockState(targetFace.getPos());
        if (!KALEIDOSCOPE_BARREL_ID.equals(BuiltInRegistries.BLOCK.getKey(targetState.getBlock()))) {
            return;
        }

        IItemHandler capability = level.getCapability(Capabilities.ItemHandler.BLOCK, targetFace.getPos(),
            targetFace.getFace());
        if (capability != null) {
            cir.setReturnValue(true);
        }
    }
}
