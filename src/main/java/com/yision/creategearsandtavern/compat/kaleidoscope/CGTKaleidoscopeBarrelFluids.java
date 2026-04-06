package com.yision.creategearsandtavern.compat.kaleidoscope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.mixin.kaleidoscope.BarrelBlockEntityAccessor;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CGTKaleidoscopeBarrelFluids {
    private static final ResourceLocation BARREL_BE_ID = ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "barrel");
    private static final ResourceLocation BARREL_BLOCK_ID = ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "barrel");
    private static final Map<String, Integer> VIRTUAL_DRAIN_REMAINDER = new ConcurrentHashMap<>();

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BlockEntityType<?> barrelType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(BARREL_BE_ID);
        if (barrelType == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        BlockEntityType<BlockEntity> typed = (BlockEntityType<BlockEntity>) barrelType;
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, typed, (be, context) ->
            isAllowedAccessSide(be.getBlockState(), context) ? new BarrelFluidHandler(be, context) : null);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, typed, (be, context) ->
            isAllowedItemAccessSide(be.getBlockState(), context) ? new BarrelItemHandler(be, context) : null);

        Block barrelBlock = BuiltInRegistries.BLOCK.get(BARREL_BLOCK_ID);
        if (barrelBlock == Blocks.AIR) {
            return;
        }
        event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, blockEntity, context) ->
            isAllowedAccessSide(state, context) ? new BarrelFluidHandler(level, pos, state, blockEntity, context) : null, barrelBlock);
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, context) ->
            isAllowedItemAccessSide(state, context) ? new BarrelItemHandler(level, pos, state, blockEntity, context) : null, barrelBlock);
    }

    public static void registerCreateCompat() {
        Block barrelBlock = BuiltInRegistries.BLOCK.get(BARREL_BLOCK_ID);
        if (barrelBlock == Blocks.AIR) {
            return;
        }

        InventoryIdentifier.REGISTRY.register(barrelBlock, (level, state, face) -> {
            BlockPos origin = BarrelBlock.getOriginPos(face.getPos(), state);
            return new InventoryIdentifier.Bounds(BoundingBox.fromCorners(origin.offset(-1, 0, -1), origin.offset(1, 2, 1)));
        });
        UnpackingHandler.REGISTRY.register(barrelBlock, CGTKaleidoscopeBarrelFluids::unpackToBarrel);
    }

    private static boolean unpackToBarrel(Level level, BlockPos pos, BlockState state, Direction side, java.util.List<ItemStack> items,
                                          PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity targetBE = level.getBlockEntity(pos);
        IItemHandler targetInv = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, targetBE, side);
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

                if (!ItemStack.isSameItemSameComponents(toInsert, itemInSlot)) {
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

    private static BlockEntity resolveController(Level level, BlockEntity fallback, BlockPos blockPos, BlockState blockState) {
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

    private static class BarrelFluidHandler implements IFluidHandler {
        private final BlockEntity context;
        private final Level level;
        private final BlockPos pos;
        private final BlockState state;
        private final Direction accessSide;

        private BarrelFluidHandler(BlockEntity context, Direction accessSide) {
            this.context = context;
            this.level = context.getLevel();
            this.pos = context.getBlockPos();
            this.state = context.getBlockState();
            this.accessSide = accessSide;
        }

        private BarrelFluidHandler(Level level, BlockPos pos, BlockState state, BlockEntity context, Direction accessSide) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.context = context;
            this.accessSide = accessSide;
        }

        private BlockEntity controller() {
            if (context != null) {
                BlockEntity resolved = resolveController(context, context.getBlockPos(), context.getBlockState());
                if (resolved != null) {
                    return resolved;
                }
            }
            BlockEntity resolved = resolveController(level == null ? context : level.getBlockEntity(pos), pos, state);
            if (resolved != null) {
                return resolved;
            }
            return context;
        }

        private BlockEntity resolveController(BlockEntity fallback, BlockPos blockPos, BlockState blockState) {
            return CGTKaleidoscopeBarrelFluids.resolveController(level, fallback, blockPos, blockState);
        }

        private boolean isBrewing(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed != null && typed.isBrewing();
        }

        private int brewLevel(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed == null ? 1 : Math.max(1, typed.getBrewLevel());
        }

        private FluidTank tank(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed == null ? new FluidTank(0) : typed.getFluid();
        }

        private ItemStack resultDrink(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            if (typed == null) {
                return ItemStack.EMPTY;
            }
            return typed.getOutput().getStackInSlot(0);
        }

        private FluidStack visibleFluid(BlockEntity barrel) {
            FluidStack current = tank(barrel).getFluidInTank(0);
            if (!current.isEmpty()) {
                CGTKaleidoscopeBarrelFluids.clearRemainder(barrel);
                return current;
            }
            if (!isBrewing(barrel)) {
                CGTKaleidoscopeBarrelFluids.clearRemainder(barrel);
                return FluidStack.EMPTY;
            }
            ItemStack result = resultDrink(barrel);
            if (result.isEmpty()) {
                CGTKaleidoscopeBarrelFluids.clearRemainder(barrel);
                return FluidStack.EMPTY;
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(result.getItem());
            if (itemId == null || !"kaleidoscope_tavern".equals(itemId.getNamespace())) {
                CGTKaleidoscopeBarrelFluids.clearRemainder(barrel);
                return FluidStack.EMPTY;
            }
            try {
                KaleidoscopeDrinkType type = KaleidoscopeDrinkType.byId(itemId);
                int availableAmount = Math.max(0, result.getCount() * 250 - CGTKaleidoscopeBarrelFluids.getRemainder(barrel));
                return CGTFluids.of(type, availableAmount, brewLevel(barrel));
            } catch (IllegalArgumentException ignored) {
                CGTKaleidoscopeBarrelFluids.clearRemainder(barrel);
                return FluidStack.EMPTY;
            }
        }

        private FluidStack drainVirtual(BlockEntity barrel, int maxDrain, FluidAction action) {
            if (maxDrain <= 0) {
                return FluidStack.EMPTY;
            }
            FluidStack visible = visibleFluid(barrel);
            if (visible.isEmpty()) {
                return FluidStack.EMPTY;
            }
            int available = visible.getAmount();
            int drainedAmount = Math.min(maxDrain, available);
            if (drainedAmount <= 0) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = visible.copyWithAmount(drainedAmount);
            if (action.execute()) {
                CGTKaleidoscopeBarrelFluids.consumeVirtualOutput(barrel, drainedAmount);
            }
            return drained;
        }

        @Override
        public int getTanks() {
            if (!canAccess()) {
                return 0;
            }
            return tank(controller()).getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!canAccess() || tank != 0) {
                return FluidStack.EMPTY;
            }
            return visibleFluid(controller());
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!canAccess()) {
                return 0;
            }
            if (tank != 0) {
                return 0;
            }
            BlockEntity barrel = controller();
            if (isBrewing(barrel)) {
                return visibleFluid(barrel).getAmount();
            }
            return this.tank(barrel).getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            BlockEntity barrel = controller();
            if (!canAccess() || isBrewing(barrel)) {
                return false;
            }
            return this.tank(barrel).isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            BlockEntity barrel = controller();
            if (!canAccess() || isBrewing(barrel)) {
                return 0;
            }
            int filled = this.tank(barrel).fill(resource, action);
            if (filled > 0 && action.execute()) {
                syncChanged(barrel);
            }
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            BlockEntity barrel = controller();
            if (!canAccess() || resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            FluidTank fluidTank = tank(barrel);
            if (fluidTank.getFluidAmount() <= 0 && isBrewing(barrel)) {
                FluidStack virtual = visibleFluid(barrel);
                if (!virtual.isEmpty() && FluidStack.isSameFluidSameComponents(resource, virtual)) {
                    return drainVirtual(barrel, resource.getAmount(), action);
                }
                return FluidStack.EMPTY;
            }
            if (isBrewing(barrel)) {
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
            BlockEntity barrel = controller();
            if (!canAccess()) {
                return FluidStack.EMPTY;
            }
            FluidTank fluidTank = tank(barrel);
            if (fluidTank.getFluidAmount() <= 0 && isBrewing(barrel)) {
                return drainVirtual(barrel, maxDrain, action);
            }
            if (isBrewing(barrel)) {
                return FluidStack.EMPTY;
            }
            FluidStack drained = fluidTank.drain(maxDrain, action);
            if (!drained.isEmpty() && action.execute()) {
                syncChanged(barrel);
            }
            return drained;
        }

        private boolean canAccess() {
            return isAllowedAccessSide(state, accessSide);
        }
    }

    private static boolean isAllowedAccessSide(BlockState state, Direction accessSide) {
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

    private static class BarrelItemHandler implements IItemHandler {
        private static final int MAX_FLUID_AMOUNT = 4000;
        private final BlockEntity context;
        private final Level level;
        private final BlockPos pos;
        private final BlockState state;
        private final Direction accessSide;

        private BarrelItemHandler(BlockEntity context, Direction accessSide) {
            this.context = context;
            this.level = context.getLevel();
            this.pos = context.getBlockPos();
            this.state = context.getBlockState();
            this.accessSide = accessSide;
        }

        private BarrelItemHandler(Level level, BlockPos pos, BlockState state, BlockEntity context, Direction accessSide) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.context = context;
            this.accessSide = accessSide;
        }

        private BlockEntity controller() {
            if (context != null) {
                BlockEntity resolved = resolveController(context, context.getBlockPos(), context.getBlockState());
                if (resolved != null) {
                    return resolved;
                }
            }
            BlockEntity resolved = resolveController(level == null ? context : level.getBlockEntity(pos), pos, state);
            if (resolved != null) {
                return resolved;
            }
            return context;
        }

        private BlockEntity resolveController(BlockEntity fallback, BlockPos blockPos, BlockState blockState) {
            return CGTKaleidoscopeBarrelFluids.resolveController(level, fallback, blockPos, blockState);
        }

        private ItemStackHandler ingredient(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed == null ? new ItemStackHandler(0) : typed.getIngredient();
        }

        private FluidTank fluid(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed == null ? new FluidTank(0) : typed.getFluid();
        }

        private boolean isBrewing(BlockEntity barrel) {
            BarrelBlockEntity typed = asBarrel(barrel);
            return typed != null && typed.isBrewing();
        }

        private boolean canAccess() {
            return isAllowedItemAccessSide(state, accessSide);
        }

        private boolean canInsert(BlockEntity barrel, ItemStack stack) {
            if (stack.isEmpty()) {
                return true;
            }
            return barrel != null && !isBrewing(barrel);
        }

        private boolean canExtract(BlockEntity barrel) {
            return barrel != null && !isBrewing(barrel);
        }

        @Override
        public int getSlots() {
            if (!canAccess()) {
                return 0;
            }
            return ingredient(controller()).getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (!canAccess()) {
                return ItemStack.EMPTY;
            }
            return ingredient(controller()).getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            BlockEntity barrel = controller();
            if (!canAccess() || !canInsert(barrel, stack)) {
                return stack;
            }
            ItemStack remain = ingredient(barrel).insertItem(slot, stack, simulate);
            if (!simulate && remain.getCount() < stack.getCount()) {
                syncChanged(barrel);
            }
            return remain;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            BlockEntity barrel = controller();
            if (!canAccess() || !canExtract(barrel)) {
                return ItemStack.EMPTY;
            }
            ItemStack extracted = ingredient(barrel).extractItem(slot, amount, simulate);
            if (!simulate && !extracted.isEmpty()) {
                syncChanged(barrel);
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!canAccess()) {
                return 0;
            }
            return ingredient(controller()).getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            BlockEntity barrel = controller();
            if (!canAccess() || !canInsert(barrel, stack)) {
                return false;
            }
            return ingredient(barrel).isItemValid(slot, stack);
        }
    }
}
