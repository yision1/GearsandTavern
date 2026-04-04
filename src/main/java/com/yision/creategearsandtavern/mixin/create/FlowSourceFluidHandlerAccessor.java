package com.yision.creategearsandtavern.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.foundation.ICapabilityProvider;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Mixin(FlowSource.FluidHandler.class)
public interface FlowSourceFluidHandlerAccessor {
    @Accessor("fluidHandlerCache")
    ICapabilityProvider<IFluidHandler> cgt$getFluidHandlerCache();

    @Accessor("fluidHandlerCache")
    void cgt$setFluidHandlerCache(ICapabilityProvider<IFluidHandler> cache);
}
