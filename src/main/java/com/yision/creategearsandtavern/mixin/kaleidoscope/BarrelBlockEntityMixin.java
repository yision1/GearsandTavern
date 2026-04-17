package com.yision.creategearsandtavern.mixin.kaleidoscope;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelParts;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelProxy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BarrelBlockEntity.class)
public class BarrelBlockEntityMixin implements KaleidoscopeBarrelProxy {
    @Unique
    private static final String CGT_CONTROLLER_POS_TAG = "cgt_controller_pos";

    @Unique
    private static final String CGT_PROXY_PART_TAG = "cgt_proxy_part";

    @Unique
    private BlockPos cgt$controllerPos;

    @Unique
    private boolean cgt$proxyPart;

    @Unique
    private boolean cgt$structureInitialized;

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void cgt$loadProxyData(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        BarrelBlockEntity self = (BarrelBlockEntity) (Object) this;
        if (tag.contains(CGT_CONTROLLER_POS_TAG)) {
            cgt$controllerPos = BlockPos.of(tag.getLong(CGT_CONTROLLER_POS_TAG));
            cgt$proxyPart = tag.getBoolean(CGT_PROXY_PART_TAG);
        } else {
            cgt$controllerPos = self.getBlockPos();
            cgt$proxyPart = false;
        }
        cgt$structureInitialized = false;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void cgt$saveProxyData(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (cgt$controllerPos != null) {
            tag.putLong(CGT_CONTROLLER_POS_TAG, cgt$controllerPos.asLong());
        }
        tag.putBoolean(CGT_PROXY_PART_TAG, cgt$proxyPart);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cgt$manageProxyTick(Level level, CallbackInfo ci) {
        BarrelBlockEntity self = (BarrelBlockEntity) (Object) this;
        if (cgt$proxyPart) {
            ci.cancel();
            return;
        }
        if (level.isClientSide || cgt$structureInitialized) {
            return;
        }
        cgt$initializeStructure(self);
    }

    @Unique
    private void cgt$initializeStructure(BarrelBlockEntity controller) {
        Level level = controller.getLevel();
        if (level == null) {
            return;
        }
        BlockState state = controller.getBlockState();
        if (!(state.getBlock() instanceof BarrelBlock)) {
            cgt$structureInitialized = true;
            return;
        }

        BlockPos origin = BarrelBlock.getOriginPos(controller.getBlockPos(), state);
        for (BlockPos partPos : KaleidoscopeBarrelParts.positions(origin)) {
            BlockState partState = level.getBlockState(partPos);
            if (!(partState.getBlock() instanceof BarrelBlock)) {
                continue;
            }

            BlockEntity existing = level.getBlockEntity(partPos);
            BarrelBlockEntity barrelPart;
            if (existing instanceof BarrelBlockEntity typed) {
                barrelPart = typed;
            } else {
                BlockEntity created = new BarrelBlockEntity(partPos, partState);
                level.setBlockEntity(created);
                if (!(created instanceof BarrelBlockEntity typed)) {
                    continue;
                }
                barrelPart = typed;
            }

            KaleidoscopeBarrelProxy proxy = (KaleidoscopeBarrelProxy) barrelPart;
            proxy.cgt$setControllerPos(origin);
            proxy.cgt$setProxyPart(!partPos.equals(origin));
            proxy.cgt$setStructureInitialized(true);
            barrelPart.setChanged();
        }

        cgt$controllerPos = origin;
        cgt$proxyPart = false;
        cgt$structureInitialized = true;
        controller.setChanged();
    }

    @Override
    public BlockPos cgt$getControllerPos() {
        BarrelBlockEntity self = (BarrelBlockEntity) (Object) this;
        return cgt$controllerPos == null ? self.getBlockPos() : cgt$controllerPos;
    }

    @Override
    public void cgt$setControllerPos(BlockPos controllerPos) {
        this.cgt$controllerPos = controllerPos;
    }

    @Override
    public boolean cgt$isProxyPart() {
        return cgt$proxyPart;
    }

    @Override
    public void cgt$setProxyPart(boolean proxyPart) {
        this.cgt$proxyPart = proxyPart;
    }

    @Override
    public boolean cgt$isStructureInitialized() {
        return cgt$structureInitialized;
    }

    @Override
    public void cgt$setStructureInitialized(boolean initialized) {
        this.cgt$structureInitialized = initialized;
    }
}

