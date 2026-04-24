package com.yision.creategearsandtavern.content.fluids.drink;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;

public final class CGTDrinkCatalog {
	private static final ResourceLocation MOLOTOV_ID = new ResourceLocation("kaleidoscope_tavern", "molotov");
	public static final int LEVELLESS_BREW_LEVEL = 0;

	private static final List<CGTDrinkDefinition> DEFINITIONS = List.of(

		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "wine"),
			"block.kaleidoscope_tavern.wine",
			0x7b2442,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "champagne"),
			"block.kaleidoscope_tavern.champagne",
			0xefd58c,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "vodka"),
			"block.kaleidoscope_tavern.vodka",
			0xe9f3f9,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "brandy"),
			"block.kaleidoscope_tavern.brandy",
			0xa85b29,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "carignan"),
			"block.kaleidoscope_tavern.carignan",
			0x5e1d34,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "sakura_wine"),
			"block.kaleidoscope_tavern.sakura_wine",
			0xe695b6,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "plum_wine"),
			"block.kaleidoscope_tavern.plum_wine",
			0xc9ad6a,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "whiskey"),
			"block.kaleidoscope_tavern.whiskey",
			0xad7a2a,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "ice_wine"),
			"block.kaleidoscope_tavern.ice_wine",
			0xf6d768,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_tavern", "vinegar"),
			"block.kaleidoscope_tavern.vinegar",
			0xd2b45d,
			Set.of("kaleidoscope_tavern")
		),
		new CGTDrinkDefinition(
			MOLOTOV_ID,
			"block.kaleidoscope_tavern.molotov",
			0xff6a00,
			Set.of("kaleidoscope_tavern")
		),

		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "crimson_crescendo"),
			"block.kaleidoscope_dim_wine.crimson_crescendo",
			0x72231e,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "ethereal_noble"),
			"block.kaleidoscope_dim_wine.ethereal_noble",
			0x1e555a,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "wart_hennessy"),
			"block.kaleidoscope_dim_wine.wart_hennessy",
			0x9a3b3b,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "blaze_martell"),
			"block.kaleidoscope_dim_wine.blaze_martell",
			0xdb7f31,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "magma_royal_salute"),
			"block.kaleidoscope_dim_wine.magma_royal_salute",
			0x982c2c,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "black_bone_lafite"),
			"block.kaleidoscope_dim_wine.black_bone_lafite",
			0x504949,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "chorus_cognac"),
			"block.kaleidoscope_dim_wine.chorus_cognac",
			0x4b337f,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "dragons_breath_brandy"),
			"block.kaleidoscope_dim_wine.dragons_breath_brandy",
			0x6d029b,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "abyssal_porter"),
			"block.kaleidoscope_dim_wine.abyssal_porter",
			0x755e5e,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "silent_sherry"),
			"block.kaleidoscope_dim_wine.silent_sherry",
			0x9e4fc0,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "forgotten_margarita"),
			"block.kaleidoscope_dim_wine.forgotten_margarita",
			0x9a54ba,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "phantom_dream_bubble"),
			"block.kaleidoscope_dim_wine.phantom_dream_bubble",
			0xad77a5,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "end_starlight"),
			"block.kaleidoscope_dim_wine.end_starlight",
			0x3c3c3d,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "divine_embryo_port"),
			"block.kaleidoscope_dim_wine.divine_embryo_port",
			0xd4871a,
			Set.of("kaleidoscope_dim_wine")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "draconic_blood_wine"),
			"block.kaleidoscope_dim_wine.draconic_blood_wine",
			0x9810d3,
			Set.of("kaleidoscope_dim_wine")
		),

		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "cave_glowbrew"),
			"block.kaleidoscope_dim_wine.cave_glowbrew",
			0x695c63,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "dawnlight_dew"),
			"block.kaleidoscope_dim_wine.dawnlight_dew",
			0xb86260,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "dead_end_spittle"),
			"block.kaleidoscope_dim_wine.dead_end_spittle",
			0xa1cdb6,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "snakeskin_liqueur"),
			"block.kaleidoscope_dim_wine.snakeskin_liqueur",
			0xb1713d,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "frostvein_beastblood"),
			"block.kaleidoscope_dim_wine.frostvein_beastblood",
			0x64a3d7,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "rangers_satchel"),
			"block.kaleidoscope_dim_wine.rangers_satchel",
			0x9d582b,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "embereye"),
			"block.kaleidoscope_dim_wine.embereye",
			0xffdf43,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "stagcall_monsoon"),
			"block.kaleidoscope_dim_wine.stagcall_monsoon",
			0x71e5d2,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "thornheart"),
			"block.kaleidoscope_dim_wine.thornheart",
			0xda757c,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "druids_secret_brew"),
			"block.kaleidoscope_dim_wine.druids_secret_brew",
			0xb488c8,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "moorglow_birds_song"),
			"block.kaleidoscope_dim_wine.moorglow_birds_song",
			0x94744e,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "perennial_frost"),
			"block.kaleidoscope_dim_wine.perennial_frost",
			0x8ec4fd,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "giants_hymn"),
			"block.kaleidoscope_dim_wine.giants_hymn",
			0xea6950,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "goblins_stash"),
			"block.kaleidoscope_dim_wine.goblins_stash",
			0x423f9b,
			Set.of("kaleidoscope_dim_wine", "twilightforest")
		),

		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "sprite"),
			"block.kaleidoscope_dim_wine.sprite",
			0x7dab8a,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "pepsi"),
			"block.kaleidoscope_dim_wine.pepsi",
			0xd6e6f3,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "fanta"),
			"block.kaleidoscope_dim_wine.fanta",
			0xffb031,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "pollen_nectar"),
			"block.kaleidoscope_dim_wine.pollen_nectar",
			0xf58d00,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "honeythorn_mead"),
			"block.kaleidoscope_dim_wine.honeythorn_mead",
			0xf8b75c,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "beeswax_honey_wine"),
			"block.kaleidoscope_dim_wine.beeswax_honey_wine",
			0xf9ab1c,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "sweet_crystal_honey"),
			"block.kaleidoscope_dim_wine.sweet_crystal_honey",
			0xffce5d,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "royal_honey_brew"),
			"block.kaleidoscope_dim_wine.royal_honey_brew",
			0xad69cc,
			Set.of("kaleidoscope_dim_wine", "the_bumblezone")
		),

		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "divine_offering_brew"),
			"block.kaleidoscope_dim_wine.divine_offering_brew",
			0xc3d1d1,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "divine_energy_mist"),
			"block.kaleidoscope_dim_wine.divine_energy_mist",
			0xcaac62,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "swet_fizz"),
			"block.kaleidoscope_dim_wine.swet_fizz",
			0xfffc67,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "gravitite_drunk"),
			"block.kaleidoscope_dim_wine.gravitite_drunk",
			0xe3a2c2,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "healing_elixir"),
			"block.kaleidoscope_dim_wine.healing_elixir",
			0xd9d9d9,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "gingerbread_warmer"),
			"block.kaleidoscope_dim_wine.gingerbread_warmer",
			0x737460,
			Set.of("kaleidoscope_dim_wine", "aether")
		),
		new CGTDrinkDefinition(
			new ResourceLocation("kaleidoscope_dim_wine", "unbound_skyborn"),
			"block.kaleidoscope_dim_wine.unbound_skyborn",
			0xb8bce3,
			Set.of("kaleidoscope_dim_wine", "aether")
		)
	);

	private static final Map<ResourceLocation, CGTDrinkDefinition> BY_ID = DEFINITIONS.stream()
		.collect(Collectors.toUnmodifiableMap(CGTDrinkDefinition::drinkId, Function.identity()));

	private static final Map<String, CGTDrinkDefinition> BY_PATH = DEFINITIONS.stream()
		.collect(Collectors.toUnmodifiableMap(def -> def.drinkId().getPath(), Function.identity()));

	private CGTDrinkCatalog() {
	}

	public static List<CGTDrinkDefinition> allDefinitions() {
		return DEFINITIONS;
	}

	public static List<CGTDrinkDefinition> enabledDefinitions(Set<String> loadedMods) {
		return DEFINITIONS.stream()
			.filter(def -> def.isEnabled(loadedMods))
			.toList();
	}

	public static CGTDrinkDefinition byDrinkId(ResourceLocation id) {
		CGTDrinkDefinition definition = BY_ID.get(id);
		if (definition == null) {
			throw new IllegalArgumentException("Unknown drink id: " + id);
		}
		return definition;
	}

	public static boolean hasDrinkId(ResourceLocation id) {
		return BY_ID.containsKey(id);
	}

	public static boolean hasSingleVariant(ResourceLocation id) {
		return MOLOTOV_ID.equals(id);
	}

	public static int normalizedBrewLevel(ResourceLocation id, int brewLevel) {
		return hasSingleVariant(id) ? LEVELLESS_BREW_LEVEL : brewLevel;
	}

	public static CGTDrinkDefinition byPath(String path) {
		CGTDrinkDefinition definition = BY_PATH.get(path);
		if (definition == null) {
			throw new IllegalArgumentException("Unknown drink path: " + path);
		}
		return definition;
	}
}
