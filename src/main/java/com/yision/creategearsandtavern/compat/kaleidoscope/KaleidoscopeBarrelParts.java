package com.yision.creategearsandtavern.compat.kaleidoscope;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;

public final class KaleidoscopeBarrelParts {
    private KaleidoscopeBarrelParts() {}

    public static List<BlockPos> positions(BlockPos origin) {
        List<BlockPos> positions = new ArrayList<>(27);
        for (int y = 0; y < 3; y++) {
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    positions.add(origin.offset(x, y, z));
                }
            }
        }
        return positions;
    }
}
