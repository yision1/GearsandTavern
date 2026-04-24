package com.yision.creategearsandtavern;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class CreateGearsAndTavernRegistrate {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateGearsandTavern.MOD_ID);

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}
