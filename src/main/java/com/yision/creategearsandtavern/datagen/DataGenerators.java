package com.yision.creategearsandtavern.datagen;

import com.yision.creategearsandtavern.datagen.recipe.CGTEmptyingRecipeGen;
import com.yision.creategearsandtavern.datagen.recipe.CGTFillingRecipeGen;
import com.yision.creategearsandtavern.datagen.recipe.CGTCompactingRecipeGen;

import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();

        generator.addProvider(event.includeServer(), new CGTFillingRecipeGen(output));
        generator.addProvider(event.includeServer(), new CGTEmptyingRecipeGen(output));
        generator.addProvider(event.includeServer(), new CGTCompactingRecipeGen(output));
    }
}
