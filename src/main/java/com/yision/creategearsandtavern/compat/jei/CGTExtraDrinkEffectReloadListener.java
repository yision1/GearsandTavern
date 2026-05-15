package com.yision.creategearsandtavern.compat.jei;

import java.util.Map;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AddReloadListenerEvent;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CGTExtraDrinkEffectReloadListener extends SimpleJsonResourceReloadListener {
	public static final Map<Item, DrinkEffectData> INSTANCE = Maps.newHashMap();
	private static final Gson GSON = new GsonBuilder().create();
	private static final Set<String> SUPPORTED_NAMESPACES = Set.of(
		"kaleidoscope_dim_wine",
		"kaleidoscope_world_liquor",
		"smc"
	);

	public CGTExtraDrinkEffectReloadListener() {
		super(GSON, "drink_effect");
	}

	public static void onAddReloadListenerEvent(AddReloadListenerEvent event) {
		event.addListener(new CGTExtraDrinkEffectReloadListener());
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
		INSTANCE.clear();
		for (var entry : resources.entrySet()) {
			if (!SUPPORTED_NAMESPACES.contains(entry.getKey().getNamespace())) {
				continue;
			}
			var result = DrinkEffectData.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
			if (result.result().isPresent()) {
				DrinkEffectData data = result.result().get();
				INSTANCE.put(data.item(), data);
			} else if (result.error().isPresent()) {
				CreateGearsandTavern.LOGGER.error("Failed to parse extra drink effect data from '{}': {}",
					entry.getKey(), result.error().get().message());
			}
		}
	}
}
