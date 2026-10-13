package com.yision.creategearsandtavern.datagen;

import com.yision.creategearsandtavern.datagen.recipe.CGTCompactingRecipeGen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {
	public static void gatherData(GatherDataEvent event) {
		var generator = event.getGenerator();
		var output = generator.getPackOutput();
		var registries = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new CGTCompactingRecipeGen(output, registries));
	}
}
