package com.yision.creategearsandtavern.compat.kaleidoscope;

import net.minecraft.core.BlockPos;

public interface KaleidoscopeBarrelProxy {
    BlockPos cgt$getControllerPos();

    void cgt$setControllerPos(BlockPos controllerPos);

    boolean cgt$isProxyPart();

    void cgt$setProxyPart(boolean proxyPart);

    boolean cgt$isStructureInitialized();

    void cgt$setStructureInitialized(boolean initialized);

    boolean cgt$isRedstonePowered();

    void cgt$setRedstonePowered(boolean powered);
}

