package com.yision.creategearsandtavern.compat.kaleidoscope.cabinet;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarCabinetBlock;
import com.simibubi.create.api.packager.InventoryIdentifier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CGTKaleidoscopeBarCabinets {
    private static final ResourceLocation BAR_CABINET_BE_ID =
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "bar_cabinet");
    private static final ResourceLocation BAR_CABINET_BLOCK_ID =
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "bar_cabinet");
    private static final ResourceLocation GLASS_BAR_CABINET_BLOCK_ID =
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "glass_bar_cabinet");

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BlockEntityType<?> cabinetType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(BAR_CABINET_BE_ID);
        if (cabinetType == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        BlockEntityType<BlockEntity> typed = (BlockEntityType<BlockEntity>) cabinetType;
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, typed,
            (be, side) -> BarCabinetLineItemHandler.create(be, side));
    }

    public static void registerCreateCompat() {
        registerInventoryIdentifier(BuiltInRegistries.BLOCK.get(BAR_CABINET_BLOCK_ID));
        registerInventoryIdentifier(BuiltInRegistries.BLOCK.get(GLASS_BAR_CABINET_BLOCK_ID));
    }

    private static void registerInventoryIdentifier(Block block) {
        if (block == Blocks.AIR) {
            return;
        }
        InventoryIdentifier.REGISTRY.register(block, (level, state, face) -> {
            if (!(state.getBlock() instanceof BarCabinetBlock)) {
                return null;
            }
            BarCabinetLineCache.LineView line = BarCabinetLineCache.get(level, face.getPos(), state);
            if (line.positions().isEmpty()) {
                return null;
            }
            BoundingBox bounds = BoundingBox.fromCorners(line.positions().getFirst(), line.positions().getLast());
            return new InventoryIdentifier.Bounds(bounds);
        });
    }
}
