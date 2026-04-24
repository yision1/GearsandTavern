package com.yision.creategearsandtavern.compat.kaleidoscope;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CGTItemCapabilities {
    public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.isEmpty()) {
            return;
        }
        IFluidHandlerItem handler = CGTItems.createFluidHandler(stack);
        if (handler != null) {
            DrinkFluidCapabilityProvider provider = new DrinkFluidCapabilityProvider(handler);
            event.addCapability(ResourceLocation.tryBuild("creategearsandtavern", "drink_fluid"), provider);
            event.addListener(provider::invalidate);
        }
    }

    private static class DrinkFluidCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<IFluidHandlerItem> lazyHandler;

        private DrinkFluidCapabilityProvider(IFluidHandlerItem handler) {
            this.lazyHandler = LazyOptional.of(() -> handler);
        }

        private void invalidate() {
            lazyHandler.invalidate();
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                return lazyHandler.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return new CompoundTag();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
        }
    }
}
