package com.yision.creategearsandtavern.compat.kaleidoscope.cabinet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.BarCabinetBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BarCabinetLineCache {
    private static final int MAX_CONNECTED_CABINETS = 16;
    private static final Map<Level, Map<BlockPos, LineView>> CACHE = new IdentityHashMap<>();

    private BarCabinetLineCache() {
    }

    public static LineView get(Level level, BlockPos pos, BlockState state) {
        if (level == null || pos == null || !isCabinetState(state)) {
            return LineView.empty();
        }

        Map<BlockPos, LineView> levelCache = CACHE.computeIfAbsent(level, ignored -> new HashMap<>());
        LineView cached = levelCache.get(pos);
        if (cached != null && cached.isValidFor(level, pos, state)) {
            return cached;
        }

        LineView rebuilt = scan(level, pos, state);
        for (BlockPos part : rebuilt.positions()) {
            levelCache.put(part, rebuilt);
        }
        return rebuilt;
    }

    public static void invalidateAround(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return;
        }

        Map<BlockPos, LineView> levelCache = CACHE.get(level);
        if (levelCache == null) {
            invalidatePosAndNeighbors(level, pos);
            return;
        }

        LineView existing = levelCache.remove(pos);
        if (existing != null) {
            for (BlockPos part : existing.positions()) {
                levelCache.remove(part);
                level.invalidateCapabilities(part);
            }
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(direction);
            LineView neighborLine = levelCache.remove(neighbor);
            if (neighborLine == null) {
                level.invalidateCapabilities(neighbor);
                continue;
            }
            for (BlockPos part : neighborLine.positions()) {
                levelCache.remove(part);
                level.invalidateCapabilities(part);
            }
        }

        level.invalidateCapabilities(pos);
    }

    private static void invalidatePosAndNeighbors(Level level, BlockPos pos) {
        level.invalidateCapabilities(pos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            level.invalidateCapabilities(pos.relative(direction));
        }
    }

    private static LineView scan(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(BarCabinetBlock.FACING);
        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        BlockPos start = pos;
        int leftSteps = 0;
        while (matches(level, start.relative(left), facing)) {
            if (leftSteps >= MAX_CONNECTED_CABINETS - 1) {
                return LineView.empty();
            }
            start = start.relative(left);
            leftSteps++;
        }

        List<BlockPos> positions = new ArrayList<>();
        BlockPos cursor = start;
        while (positions.size() < MAX_CONNECTED_CABINETS && matches(level, cursor, facing)) {
            positions.add(cursor.immutable());
            cursor = cursor.relative(right);
        }
        if (matches(level, cursor, facing)) {
            return LineView.empty();
        }

        return new LineView(List.copyOf(positions), facing, level.getGameTime(), hash(level, positions, facing));
    }

    private static boolean matches(Level level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        return isCabinetState(state) && state.getValue(BarCabinetBlock.FACING) == facing
            && level.getBlockEntity(pos) != null;
    }

    private static boolean isCabinetState(BlockState state) {
        return state != null && state.getBlock() instanceof BarCabinetBlock
            && state.hasProperty(BarCabinetBlock.FACING);
    }

    private static int hash(Level level, List<BlockPos> positions, Direction facing) {
        int result = facing.ordinal();
        for (BlockPos part : positions) {
            BlockState state = level.getBlockState(part);
            result = 31 * result + part.hashCode();
            result = 31 * result + state.getBlock().hashCode();
            result = 31 * result + state.getValue(BarCabinetBlock.FACING).ordinal();
        }
        return result;
    }

    public record LineView(List<BlockPos> positions, Direction facing, long scannedAtGameTime, int structureHash) {
        private static final LineView EMPTY = new LineView(List.of(), Direction.NORTH, -1, 0);

        static LineView empty() {
            return EMPTY;
        }

        boolean isValidFor(Level level, BlockPos queriedPos, BlockState queriedState) {
            if (positions.isEmpty() || !positions.contains(queriedPos)) {
                return false;
            }
            if (!isCabinetState(queriedState) || queriedState.getValue(BarCabinetBlock.FACING) != facing) {
                return false;
            }
            if (hash(level, positions, facing) != structureHash) {
                return false;
            }

            Direction left = facing.getClockWise();
            Direction right = facing.getCounterClockWise();
            BlockPos first = positions.getFirst();
            BlockPos last = positions.getLast();
            return !matches(level, first.relative(left), facing)
                && !matches(level, last.relative(right), facing);
        }

        BlockEntity blockEntityAt(Level level, int cabinetIndex) {
            if (cabinetIndex < 0 || cabinetIndex >= positions.size()) {
                return null;
            }
            return level.getBlockEntity(positions.get(cabinetIndex));
        }
    }
}
