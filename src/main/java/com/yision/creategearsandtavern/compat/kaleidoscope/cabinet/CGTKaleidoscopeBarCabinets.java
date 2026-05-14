package com.yision.creategearsandtavern.compat.kaleidoscope.cabinet;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarCabinetBlock;
import com.simibubi.create.api.packager.InventoryIdentifier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;

public class CGTKaleidoscopeBarCabinets {
    private static final ResourceLocation BAR_CABINET_BLOCK_ID =
        new ResourceLocation("kaleidoscope_tavern", "bar_cabinet");
    private static final ResourceLocation GLASS_BAR_CABINET_BLOCK_ID =
        new ResourceLocation("kaleidoscope_tavern", "glass_bar_cabinet");

    public static void registerCreateCompat() {
        registerInventoryIdentifier(ForgeRegistries.BLOCKS.getValue(BAR_CABINET_BLOCK_ID));
        registerInventoryIdentifier(ForgeRegistries.BLOCKS.getValue(GLASS_BAR_CABINET_BLOCK_ID));
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
            BoundingBox bounds = BoundingBox.fromCorners(line.positions().get(0), line.positions().get(line.positions().size() - 1));
            return new InventoryIdentifier.Bounds(bounds);
        });
    }
}
