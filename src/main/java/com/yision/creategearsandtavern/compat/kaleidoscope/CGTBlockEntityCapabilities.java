package com.yision.creategearsandtavern.compat.kaleidoscope;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CGTBlockEntityCapabilities {
    public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();
        if (!(be instanceof BarrelBlockEntity barrel)) {
            return;
        }
        BarrelCapabilityProvider provider = new BarrelCapabilityProvider(barrel);
        event.addCapability(
            ResourceLocation.tryBuild("creategearsandtavern", "barrel_handler"),
            provider
        );
        event.addListener(provider::invalidate);
    }

    private static class BarrelCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
        private final BarrelBlockEntity barrel;
        private final Map<Direction, LazyOptional<IFluidHandler>> fluidHandlers = new EnumMap<>(Direction.class);
        private final Map<Direction, LazyOptional<IItemHandler>> itemHandlers = new EnumMap<>(Direction.class);
        private LazyOptional<IFluidHandler> unsidedFluidHandler = LazyOptional.empty();
        private LazyOptional<IItemHandler> unsidedItemHandler = LazyOptional.empty();

        private BarrelCapabilityProvider(BarrelBlockEntity barrel) {
            this.barrel = barrel;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.FLUID_HANDLER) {
                return getFluidCapability(side).cast();
            }
            if (cap == ForgeCapabilities.ITEM_HANDLER) {
                return getItemCapability(side).cast();
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

        private LazyOptional<IFluidHandler> getFluidCapability(@Nullable Direction side) {
            if (!hasFluidHandler(side)) {
                return LazyOptional.empty();
            }
            if (side == null) {
                if (!unsidedFluidHandler.isPresent()) {
                    unsidedFluidHandler = LazyOptional.of(() -> new DelegatingBarrelFluidHandler(barrel, null));
                }
                return unsidedFluidHandler;
            }
            return fluidHandlers.computeIfAbsent(side, direction ->
                LazyOptional.of(() -> new DelegatingBarrelFluidHandler(barrel, direction)));
        }

        private LazyOptional<IItemHandler> getItemCapability(@Nullable Direction side) {
            if (!hasItemHandler(side)) {
                return LazyOptional.empty();
            }
            if (side == null) {
                if (!unsidedItemHandler.isPresent()) {
                    unsidedItemHandler = LazyOptional.of(() -> new DelegatingBarrelItemHandler(barrel, null));
                }
                return unsidedItemHandler;
            }
            return itemHandlers.computeIfAbsent(side, direction ->
                LazyOptional.of(() -> new DelegatingBarrelItemHandler(barrel, direction)));
        }

        private boolean hasFluidHandler(@Nullable Direction side) {
            return CGTKaleidoscopeBarrelFluids.getBarrelFluidHandler(
                barrel.getLevel(), barrel.getBlockPos(), barrel.getBlockState(), barrel, side) != null;
        }

        private boolean hasItemHandler(@Nullable Direction side) {
            return CGTKaleidoscopeBarrelFluids.getBarrelItemHandler(
                barrel.getLevel(), barrel.getBlockPos(), barrel.getBlockState(), barrel, side) != null;
        }

        private void invalidate() {
            unsidedFluidHandler.invalidate();
            unsidedItemHandler.invalidate();
            fluidHandlers.values().forEach(LazyOptional::invalidate);
            itemHandlers.values().forEach(LazyOptional::invalidate);
            fluidHandlers.clear();
            itemHandlers.clear();
        }
    }

    private static final class DelegatingBarrelFluidHandler implements IFluidHandler {
        private final BarrelBlockEntity barrel;
        @Nullable
        private final Direction side;

        private DelegatingBarrelFluidHandler(BarrelBlockEntity barrel, @Nullable Direction side) {
            this.barrel = barrel;
            this.side = side;
        }

        @Override
        public int getTanks() {
            IFluidHandler handler = handler();
            return handler == null ? 0 : handler.getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            IFluidHandler handler = handler();
            return handler == null ? FluidStack.EMPTY : handler.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            IFluidHandler handler = handler();
            return handler == null ? 0 : handler.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            IFluidHandler handler = handler();
            return handler != null && handler.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            IFluidHandler handler = handler();
            return handler == null ? 0 : handler.fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            IFluidHandler handler = handler();
            return handler == null ? FluidStack.EMPTY : handler.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            IFluidHandler handler = handler();
            return handler == null ? FluidStack.EMPTY : handler.drain(maxDrain, action);
        }

        @Nullable
        private IFluidHandler handler() {
            return CGTKaleidoscopeBarrelFluids.getBarrelFluidHandler(
                barrel.getLevel(), barrel.getBlockPos(), barrel.getBlockState(), barrel, side);
        }
    }

    private static final class DelegatingBarrelItemHandler implements IItemHandler {
        private final BarrelBlockEntity barrel;
        @Nullable
        private final Direction side;

        private DelegatingBarrelItemHandler(BarrelBlockEntity barrel, @Nullable Direction side) {
            this.barrel = barrel;
            this.side = side;
        }

        @Override
        public int getSlots() {
            IItemHandler handler = handler();
            return handler == null ? 0 : handler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            IItemHandler handler = handler();
            return handler == null ? ItemStack.EMPTY : handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            IItemHandler handler = handler();
            return handler == null ? stack : handler.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            IItemHandler handler = handler();
            return handler == null ? ItemStack.EMPTY : handler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            IItemHandler handler = handler();
            return handler == null ? 0 : handler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            IItemHandler handler = handler();
            return handler != null && handler.isItemValid(slot, stack);
        }

        @Nullable
        private IItemHandler handler() {
            return CGTKaleidoscopeBarrelFluids.getBarrelItemHandler(
                barrel.getLevel(), barrel.getBlockPos(), barrel.getBlockState(), barrel, side);
        }
    }
}
