package com.yision.creategearsandtavern.compat.kaleidoscope;

import org.jetbrains.annotations.Nullable;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarrelBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;

public final class CGTKaleidoscopeSchematicRequirements {
    private CGTKaleidoscopeSchematicRequirements() {
    }

    public static void registerCreateCompat() {
        SchematicRequirementRegistries.BLOCKS.register(ModBlocks.BARREL.get(),
            CGTKaleidoscopeSchematicRequirements::barrelRequirement);
        SchematicRequirementRegistries.BLOCKS.register(ModBlocks.GRAPEVINE_TRELLIS.get(),
            CGTKaleidoscopeSchematicRequirements::grapevineTrellisRequirement);

        registerLowerHalfRequirement(ModBlocks.STEPLADDER.get(), ModItems.STEPLADDER.get());
        registerLowerHalfRequirement(ModBlocks.CHALKBOARD.get(), ModItems.CHALKBOARD.get());

        registerLowerHalfRequirement(ModBlocks.BASE_SANDWICH_BOARD.get(), ModItems.BASE_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.GRASS_SANDWICH_BOARD.get(), ModItems.GRASS_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.ALLIUM_SANDWICH_BOARD.get(), ModItems.ALLIUM_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.AZURE_BLUET_SANDWICH_BOARD.get(),
            ModItems.AZURE_BLUET_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.CORNFLOWER_SANDWICH_BOARD.get(), ModItems.CORNFLOWER_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.ORCHID_SANDWICH_BOARD.get(), ModItems.ORCHID_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.PEONY_SANDWICH_BOARD.get(), ModItems.PEONY_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.PINK_PETALS_SANDWICH_BOARD.get(),
            ModItems.PINK_PETALS_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.PITCHER_PLANT_SANDWICH_BOARD.get(),
            ModItems.PITCHER_PLANT_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.POPPY_SANDWICH_BOARD.get(), ModItems.POPPY_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.SUNFLOWER_SANDWICH_BOARD.get(), ModItems.SUNFLOWER_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.TORCHFLOWER_SANDWICH_BOARD.get(),
            ModItems.TORCHFLOWER_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.TULIP_SANDWICH_BOARD.get(), ModItems.TULIP_SANDWICH_BOARD.get());
        registerLowerHalfRequirement(ModBlocks.WITHER_ROSE_SANDWICH_BOARD.get(),
            ModItems.WITHER_ROSE_SANDWICH_BOARD.get());
    }

    private static ItemRequirement barrelRequirement(BlockState state, @Nullable BlockEntity blockEntity) {
        if (state.getValue(BarrelBlock.LAYER) == AttachFace.FLOOR
            && state.getValue(BarrelBlock.INDEX) == 4) {
            return new ItemRequirement(ItemUseType.CONSUME, ModItems.BARREL.get());
        }
        return ItemRequirement.INVALID;
    }

    private static ItemRequirement grapevineTrellisRequirement(BlockState state, @Nullable BlockEntity blockEntity) {
        return ItemRequirement.of(ModBlocks.TRELLIS.get().defaultBlockState(), null)
            .union(new ItemRequirement(ItemUseType.CONSUME, ModItems.GRAPEVINE.get()));
    }

    private static void registerLowerHalfRequirement(Block block, Item item) {
        SchematicRequirementRegistries.BLOCKS.register(block,
            (state, blockEntity) -> lowerHalfRequirement(state, blockEntity, item));
    }

    private static ItemRequirement lowerHalfRequirement(BlockState state, @Nullable BlockEntity blockEntity, Item item) {
        if (state.getValue(BlockStateProperties.HALF) == Half.BOTTOM) {
            return new ItemRequirement(ItemUseType.CONSUME, item);
        }
        return ItemRequirement.INVALID;
    }
}
