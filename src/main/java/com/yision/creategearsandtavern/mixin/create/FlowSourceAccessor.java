package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.FlowSource;

import net.createmod.catnip.math.BlockFace;

@Mixin(FlowSource.class)
public interface FlowSourceAccessor {
    @Accessor("location")
    BlockFace cgt$getLocation();
}
