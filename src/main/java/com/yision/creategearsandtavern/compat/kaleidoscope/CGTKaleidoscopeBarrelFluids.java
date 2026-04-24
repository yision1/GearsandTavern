package com.yision.creategearsandtavern.compat.kaleidoscope;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.yision.creategearsandtavern.compat.kaleidoscope.KaleidoscopeBarrelProxy;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.mixin.kaleidoscope.BarrelBlockEntityAccessor;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class CGTKaleidoscopeBarrelFluids {
    private static final ResourceLocation BARREL_BE_ID = new ResourceLocation("kaleidoscope_tavern", "barrel");
    private static final ResourceLocation BARREL_BLOCK_ID = new ResourceLocation("kaleidoscope_tavern", "barrel");
    private static final Map<String, Integer> VIRTUAL_DRAIN_REMAINDER = new ConcurrentHashMap<>();

    public static void registerCreateCompat() {
        Block barrelBlock = ForgeRegistries.BLOCKS.getValue(BARREL_BLOCK_ID);
        if (barrelBlock == null || barrelBlock == Blocks.AIR) {
            return;
        }

        InventoryIdentifier.REGISTRY.register(barrelBlock, (level, state, face) -> {
            BlockPos origin = BarrelBlock.getOriginPos(face.getPos(), state);
            return new InventoryIdentifier.Bounds(BoundingBox.fromCorners(origin.offset(-1, 0, -1), origin.offset(1, 2, 1)));
        });
        UnpackingHandler.REGISTRY.register(barrelBlock, CGTKaleidoscopeBarrelFluids::unpackToBarrel);
    }

    private static boolean unpackToBarrel(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items,
                                          PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity be = level.getBlockEntity(pos);
        IItemHandler targetInv = getBarrelItemHandler(level, pos, state, be, side);
        if (targetInv == null) {
            return false;
        }

        if (!simulate) {
            for (ItemStack itemStack : items) {
                ItemHandlerHelper.insertItemStacked(targetInv, itemStack.copy(), false);
            }
            return true;
        }

        for (int slot = 0; slot < targetInv.getSlots(); slot++) {
            ItemStack itemInSlot = targetInv.getStackInSlot(slot);
            int itemsAddedToSlot = 0;

            for (int boxSlot = 0; boxSlot < items.size(); boxSlot++) {
                ItemStack toInsert = items.get(boxSlot);
                if (toInsert.isEmpty()) {
                    continue;
                }

                if (targetInv.insertItem(slot, toInsert, true).getCount() == toInsert.getCount()) {
                    continue;
                }

                if (itemInSlot.isEmpty()) {
                    int maxStackSize = targetInv.getSlotLimit(slot);
                    if (maxStackSize < toInsert.getCount()) {
                        toInsert.shrink(maxStackSize);
                        toInsert = toInsert.copyWithCount(maxStackSize);
                    } else {
                        items.set(boxSlot, ItemStack.EMPTY);
                    }

                    itemInSlot = toInsert;
                    targetInv.insertItem(slot, toInsert, true);
                    continue;
                }

                if (!ItemStack.isSameItemSameTags(toInsert, itemInSlot)) {
                    continue;
                }

                int insertedAmount = toInsert.getCount() - targetInv.insertItem(slot, toInsert, true).getCount();
                int slotLimit = Math.min(itemInSlot.getMaxStackSize(), targetInv.getSlotLimit(slot));
                int insertableAmountWithPreviousItems =
                    Math.min(toInsert.getCount(), slotLimit - itemInSlot.getCount() - itemsAddedToSlot);

                int added = Math.min(insertedAmount, Math.max(0, insertableAmountWithPreviousItems));
                itemsAddedToSlot += added;
                items.set(boxSlot, toInsert.copyWithCount(toInsert.getCount() - added));
            }
        }

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static IFluidHandler getBarrelFluidHandler(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction side) {
        if (state == null || !(state.getBlock() instanceof BarrelBlock)) {
            return null;
        }
        if (!isAllowedAccessSide(state, side)) {
            return null;
        }
        BlockEntity controller = resolveController(level, be, pos, state);
        if (!(controller instanceof BarrelBlockEntity barrel)) {
            return null;
        }
        return new BarrelFluidHandler(barrel, side);
    }

    public static IItemHandler getBarrelItemHandler(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction side) {
        if (state == null || !(state.getBlock() instanceof BarrelBlock)) {
            return null;
        }
        if (!isAllowedItemAccessSide(state, side)) {
            return null;
        }
        BlockEntity controller = resolveController(level, be, pos, state);
        if (!(controller instanceof BarrelBlockEntity barrel)) {
            return null;
        }
        return new BarrelItemHandler(barrel, side);
    }

    private static BlockEntity resolveController(Level level, BlockEntity fallback, BlockPos blockPos, BlockState blockState) {
        if (fallback instanceof KaleidoscopeBarrelProxy proxy) {
            BlockPos controllerPos = proxy.cgt$getControllerPos();
            if (controllerPos != null && level != null) {
                BlockEntity controller = level.getBlockEntity(controllerPos);
                if (controller != null) {
                    return controller;
                }
            }
        }
        if (blockState == null) {
            return fallback;
        }
        if (!(blockState.getBlock() instanceof BarrelBlock)) {
            return fallback;
        }
        BlockPos originPos = BarrelBlock.getOriginPos(blockPos, blockState);
        BlockEntity controller = level == null ? null : level.getBlockEntity(originPos);
        return controller == null ? fallback : controller;
    }

    private static BarrelBlockEntity asBarrel(BlockEntity blockEntity) {
        return blockEntity instanceof BarrelBlockEntity barrel ? barrel : null;
    }

    public static FluidStack getVisibleFluid(BarrelBlockEntity barrel) {
        if (barrel == null) {
            return FluidStack.EMPTY;
        }
        FluidStack current = barrel.getFluid().getFluidInTank(0);
        if (!current.isEmpty()) {
            clearRemainder(barrel);
            return current;
        }
        if (!barrel.isBrewing()) {
            clearRemainder(barrel);
            return FluidStack.EMPTY;
        }
        ItemStack result = barrel.getOutput().getStackInSlot(0);
        if (result.isEmpty()) {
            clearRemainder(barrel);
            return FluidStack.EMPTY;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(result.getItem());
        if (itemId == null || !"kaleidoscope_tavern".equals(itemId.getNamespace())) {
            clearRemainder(barrel);
            return FluidStack.EMPTY;
        }
        try {
            KaleidoscopeDrinkType type = KaleidoscopeDrinkType.byId(itemId);
            int availableAmount = Math.max(0, result.getCount() * 250 - getRemainder(barrel));
            return CGTFluids.of(type, availableAmount, Math.max(1, barrel.getBrewLevel()));
        } catch (IllegalArgumentException ignored) {
            clearRemainder(barrel);
            return FluidStack.EMPTY;
        }
    }

    private static void syncChanged(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return;
        }
        if (blockEntity instanceof BarrelBlockEntity barrel) {
            barrel.refresh();
            return;
        }
        blockEntity.setChanged();
        Level barrelLevel = blockEntity.getLevel();
        if (barrelLevel == null) {
            return;
        }
        BlockPos barrelPos = blockEntity.getBlockPos();
        BlockState barrelState = barrelLevel.getBlockState(barrelPos);
        barrelLevel.sendBlockUpdated(barrelPos, barrelState, barrelState, Block.UPDATE_ALL);
    }

    private static String key(BlockEntity barrel) {
        if (barrel == null || barrel.getLevel() == null) {
            return null;
        }
        ResourceLocation dimension = barrel.getLevel().dimension().location();
        return dimension + "|" + barrel.getBlockPos().asLong();
    }

    private static int getRemainder(BlockEntity barrel) {
        String key = key(barrel);
        if (key == null) {
            return 0;
        }
        return VIRTUAL_DRAIN_REMAINDER.getOrDefault(key, 0);
    }

    private static void setRemainder(BlockEntity barrel, int remainder) {
        String key = key(barrel);
        if (key == null) {
            return;
        }
        if (remainder <= 0) {
            VIRTUAL_DRAIN_REMAINDER.remove(key);
        } else {
            VIRTUAL_DRAIN_REMAINDER.put(key, remainder);
        }
    }

    private static void clearRemainder(BlockEntity barrel) {
        String key = key(barrel);
        if (key != null) {
            VIRTUAL_DRAIN_REMAINDER.remove(key);
        }
    }

    private static boolean consumeVirtualOutput(BlockEntity barrel, int drainedAmount) {
        BarrelBlockEntity typed = asBarrel(barrel);
        if (typed == null || drainedAmount <= 0) {
            return false;
        }

        int pending = getRemainder(typed) + drainedAmount;
        int bottles = pending / 250;
        int remainder = pending % 250;
        ItemStackHandler output = typed.getOutput();

        if (bottles > 0) {
            output.extractItem(0, bottles, false);
        }

        if (!output.getStackInSlot(0).isEmpty()) {
            setRemainder(typed, remainder);
            syncChanged(typed);
            return true;
        }

        clearRemainder(typed);
        typed.clearItemsAndFluid();
        BarrelBlockEntityAccessor accessor = (BarrelBlockEntityAccessor) typed;
        accessor.cgt$setRecipeId(null);
        accessor.cgt$setBrewLevel(0);
        accessor.cgt$setBrewTime(-1);
        syncChanged(typed);
        return true;
    }

    private static void resetFinishedBarrel(BarrelBlockEntity barrel) {
        if (barrel == null) {
            return;
        }
        clearRemainder(barrel);
        barrel.clearItemsAndFluid();
        BarrelBlockEntityAccessor accessor = (BarrelBlockEntityAccessor) barrel;
        accessor.cgt$setRecipeId(null);
        accessor.cgt$setBrewLevel(0);
        accessor.cgt$setBrewTime(-1);
        syncChanged(barrel);
    }

    public static boolean isAllowedAccessSide(BlockState state, Direction accessSide) {
        if (accessSide == null || state == null) {
            return true;
        }
        Direction facing = readFacing(state);
        if (facing == null) {
            return true;
        }
        return accessSide == Direction.UP
            || accessSide == Direction.DOWN
            || accessSide == facing
            || accessSide == facing.getOpposite();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Direction readFacing(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (!"facing".equals(property.getName())) {
                continue;
            }
            Object value = state.getValue((Property) property);
            if (value instanceof Direction direction) {
                return direction;
            }
        }
        return null;
    }

    private static boolean isAllowedItemAccessSide(BlockState state, Direction accessSide) {
        return isAllowedAccessSide(state, accessSide);
    }

    public static class BarrelFluidHandler implements IFluidHandler {
        private final BarrelBlockEntity barrel;
        private final Direction accessSide;

        public BarrelFluidHandler(BarrelBlockEntity barrel, Direction accessSide) {
            this.barrel = barrel;
            this.accessSide = accessSide;
        }

        private FluidTank tank() {
            return barrel == null ? new FluidTank(0) : barrel.getFluid();
        }

        private boolean isBrewing() {
            return barrel != null && barrel.isBrewing();
        }

        private int brewLevel() {
            return barrel == null ? 1 : Math.max(1, barrel.getBrewLevel());
        }

        private ItemStack resultDrink() {
            if (barrel == null) {
                return ItemStack.EMPTY;
            }
            return barrel.getOutput().getStackInSlot(0);
        }

        private FluidStack visibleFluid() {
            return CGTKaleidoscopeBarrelFluids.getVisibleFluid(barrel);
        }

        private FluidStack drainVirtual(int maxDrain, FluidAction action) {
            if (maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            FluidStack visible = visibleFluid();
            if (visible.isEmpty()) {
                return FluidStack.EMPTY;
            }
            int available = visible.getAmount();
            int drainedAmount = Math.min(maxDrain, available);
            if (drainedAmount <= 0) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = new FluidStack(visible.getFluid(), drainedAmount, visible.getTag());
            if (action.execute()) {
                CGTKaleidoscopeBarrelFluids.consumeVirtualOutput(barrel, drainedAmount);
            }
            return drained;
        }

        @Override
        public int getTanks() {
            return tank().getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }
            return visibleFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            if (tank != 0) {
                return 0;
            }
            if (isBrewing()) {
                return visibleFluid().getAmount();
            }
            return tank().getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (isBrewing()) {
                return false;
            }
            return tank().isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (isBrewing()) {
                return 0;
            }
            int filled = tank().fill(resource, action);
            if (filled > 0 && action.execute()) {
                syncChanged(barrel);
            }
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FluidTank fluidTank = tank();
            if (fluidTank.getFluidAmount() <= 0 && isBrewing()) {
                FluidStack virtual = visibleFluid();
                if (!virtual.isEmpty() && resource.getFluid().isSame(virtual.getFluid())
                    && Objects.equals(resource.getTag(), virtual.getTag())) {
                    return drainVirtual(resource.getAmount(), action);
                }
                return FluidStack.EMPTY;
            }
            if (isBrewing()) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = fluidTank.drain(resource, action);
            if (!drained.isEmpty() && action.execute()) {
                syncChanged(barrel);
            }
            return drained;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidTank fluidTank = tank();
            if (fluidTank.getFluidAmount() <= 0 && isBrewing()) {
                return drainVirtual(maxDrain, action);
            }
            if (isBrewing()) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = fluidTank.drain(maxDrain, action);
            if (!drained.isEmpty() && action.execute()) {
                syncChanged(barrel);
            }
            return drained;
        }
    }

    public static class BarrelItemHandler implements IItemHandler {
        private final BarrelBlockEntity barrel;
        private final Direction accessSide;

        public BarrelItemHandler(BarrelBlockEntity barrel, Direction accessSide) {
            this.barrel = barrel;
            this.accessSide = accessSide;
        }

        private ItemStackHandler ingredient() {
            return barrel == null ? new ItemStackHandler(0) : barrel.getIngredient();
        }

        private ItemStackHandler output() {
            return barrel == null ? new ItemStackHandler(0) : barrel.getOutput();
        }

        private boolean isBrewing() {
            return barrel != null && barrel.isBrewing();
        }

        private boolean canInsert(ItemStack stack) {
            if (stack.isEmpty()) {
                return true;
            }
            return barrel != null && !isBrewing();
        }

        private boolean canExtract() {
            return barrel != null;
        }

        private boolean exposingOutput() {
            return isBrewing();
        }

        @Override
        public int getSlots() {
            return exposingOutput() ? output().getSlots() : ingredient().getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return exposingOutput() ? output().getStackInSlot(slot) : ingredient().getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!canInsert(stack) || exposingOutput()) {
                return stack;
            }
            ItemStack remain = ingredient().insertItem(slot, stack, simulate);
            if (!simulate && remain.getCount() < stack.getCount()) {
                syncChanged(barrel);
            }
            return remain;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!canExtract()) {
                return ItemStack.EMPTY;
            }
            ItemStackHandler handler = exposingOutput() ? output() : ingredient();
            ItemStack extracted = handler.extractItem(slot, amount, simulate);
            if (!simulate && !extracted.isEmpty()) {
                if (exposingOutput() && output().getStackInSlot(0).isEmpty()) {
                    resetFinishedBarrel(barrel);
                } else {
                    syncChanged(barrel);
                }
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return exposingOutput() ? output().getSlotLimit(slot) : ingredient().getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!canInsert(stack) || exposingOutput()) {
                return false;
            }
            return ingredient().isItemValid(slot, stack);
        }
    }
}
