package com.yision.creategearsandtavern.compat.kaleidoscope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class KaleidoscopeBarrelSides {
    private KaleidoscopeBarrelSides() {
    }

    public static boolean isAllowedAutomationSide(BlockState state, Direction accessSide) {
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
            Comparable value = state.getValue((Property) property);
            return value instanceof Direction direction ? direction : null;
        }
        return null;
    }
}
