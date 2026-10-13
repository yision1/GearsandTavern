package com.yision.creategearsandtavern.compat.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.ysbbbbbb.kaleidoscopetavern.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.ShakerRecipe;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.CGTShakerMixingRecipe;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.ShakerIngredientConversions;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTDataComponents;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

@JeiPlugin
public final class CGTJeiPlugin implements IModPlugin {
	private static final ResourceLocation UID = id("jei_plugin");
	private static final RecipeType<RecipeHolder<BasinRecipe>> CREATE_MIXING = createHolderType("mixing");
	private static final RecipeType<RecipeHolder<FillingRecipe>> CREATE_FILLING = createHolderType("spout_filling");
	private static final RecipeType<RecipeHolder<EmptyingRecipe>> CREATE_EMPTYING = createHolderType("draining");

	private final CGTJeiDrinkRecipes drinkRecipes = new CGTJeiDrinkRecipes();

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> fluidHelper) {
		CGTJeiDrinkRecipes.registerSubtypes(registration);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new CGTShakerJeiCategory());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		createShakerRecipe()
			.ifPresent(recipe -> registration.addRecipes(CGTShakerJeiCategory.TYPE, List.of(recipe)));
		drinkRecipes.registerRecipes(registration, CREATE_FILLING, CREATE_EMPTYING);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(AllBlocks.MECHANICAL_MIXER.asStack(), CGTShakerJeiCategory.TYPE);
		registration.addRecipeCatalyst(AllBlocks.BASIN.asStack(), CGTShakerJeiCategory.TYPE);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		var manager = jeiRuntime.getRecipeManager();
		List<RecipeHolder<BasinRecipe>> runtimeMixing = manager.createRecipeLookup(CREATE_MIXING)
			.includeHidden()
			.get()
			.filter(holder -> holder.value() instanceof CGTShakerMixingRecipe)
			.toList();
		manager.hideRecipes(CREATE_MIXING, runtimeMixing);

		List<RecipeHolder<FillingRecipe>> automaticFilling = new ArrayList<>();
		List<RecipeHolder<EmptyingRecipe>> automaticEmptying = new ArrayList<>();
		var focusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
		List<FluidStack> variants = new ArrayList<>(CGTJeiDrinkRecipes.fluidVariants());
		variants.addAll(jeiRuntime.getIngredientManager().getAllIngredients(NeoForgeTypes.FLUID_STACK));
		for (FluidStack fluid : variants) {
			if (!(fluid.getFluid() instanceof KaleidoscopeDrinkFluid)) {
				continue;
			}
			var input = focusFactory.createFocus(RecipeIngredientRole.INPUT, NeoForgeTypes.FLUID_STACK, fluid);
			automaticFilling.addAll(manager.createRecipeLookup(CREATE_FILLING)
				.limitFocus(List.of(input))
				.includeHidden()
				.get()
				.filter(CGTJeiPlugin::shouldHideAutomaticDrinkFilling)
				.toList());
			var output = focusFactory.createFocus(RecipeIngredientRole.OUTPUT, NeoForgeTypes.FLUID_STACK, fluid);
			automaticEmptying.addAll(manager.createRecipeLookup(CREATE_EMPTYING)
				.limitFocus(List.of(output))
				.includeHidden()
				.get()
				.filter(CGTJeiPlugin::shouldHideAutomaticDrinkEmptying)
				.toList());
		}
		manager.hideRecipes(CREATE_FILLING, automaticFilling);
		manager.hideRecipes(CREATE_EMPTYING, automaticEmptying);
	}

	private static Optional<CGTShakerJeiRecipe> createShakerRecipe() {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return Optional.empty();
		}

		RecipeManager recipeManager = level.getRecipeManager();
		List<RecipeHolder<ShakerRecipe>> ktRecipes = recipeManager.getAllRecipesFor(ModRecipes.SHAKER_RECIPE);
		List<ItemStack> displayInputs = new ArrayList<>();
		List<ItemStack> displayOutputs = new ArrayList<>();

		for (RecipeHolder<ShakerRecipe> holder : ktRecipes) {
			Optional<List<ItemStack>> representative = getRepresentativeBottles(holder.value());
			if (representative.isEmpty()) {
				continue;
			}

			List<ItemStack> bottles = representative.get();
			ItemStack cocktail = holder.value().assemble(new SimpleInput(bottles), level.registryAccess());
			if (cocktail.isEmpty()) {
				continue;
			}

			ItemStack input = createShaker(bottles);
			displayInputs.add(input);
			displayOutputs.add(createReadyShaker(input, cocktail));
		}

		createSignatureInput(level, ktRecipes)
			.ifPresent(input -> {
				displayInputs.add(input);
				displayOutputs.add(createReadyShaker(input, createSignatureCocktail(input)));
			});

		if (displayInputs.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new CGTShakerJeiRecipe(displayInputs, displayOutputs));
	}

	private static Optional<List<ItemStack>> getRepresentativeBottles(ShakerRecipe recipe) {
		List<ItemStack> bottles = new ArrayList<>(ShakerIngredientConversions.INPUT_SLOTS);
		for (Ingredient ingredient : recipe.getIngredients()) {
			if (ingredient.isEmpty()) {
				continue;
			}

			Optional<ItemStack> representative = Arrays.stream(ingredient.getItems())
				.map(CGTJeiPlugin::minimumQuality)
				.filter(BottleBlockItem::isValidForShaker)
				.findFirst();
			if (representative.isEmpty()) {
				return Optional.empty();
			}
			bottles.add(representative.get());
		}
		if (bottles.size() != ShakerIngredientConversions.INPUT_SLOTS) {
			return Optional.empty();
		}
		return Optional.of(List.copyOf(bottles));
	}

	private static Optional<ItemStack> createSignatureInput(
		ClientLevel level, List<RecipeHolder<ShakerRecipe>> ktRecipes) {
		List<ItemStack> bottles = List.of(
			minimumQuality(new ItemStack(ModItems.PLUM_WINE.get())),
			minimumQuality(new ItemStack(ModItems.LUMINOUS_BRIDE.get())),
			minimumQuality(new ItemStack(ModItems.ICE_WINE.get())));
		SimpleInput input = new SimpleInput(bottles);
		if (ktRecipes.stream().anyMatch(holder -> holder.value().matches(input, level))) {
			return Optional.empty();
		}
		return Optional.of(createShaker(bottles));
	}

	private static ItemStack minimumQuality(ItemStack original) {
		ItemStack stack = original.copyWithCount(1);
		if (stack.getItem() instanceof BottleBlockItem) {
			BottleBlockItem.setBrewLevel(stack, BottleBlockItem.MIN_BREW_LEVEL_FOR_SHAKER);
		}
		return stack;
	}

	private static ItemStack createShaker(List<ItemStack> ingredients) {
		ItemStack shaker = new ItemStack(ModItems.SHAKER.get());
		ItemStackHandler storage = new ItemStackHandler(ShakerIngredientConversions.INPUT_SLOTS);
		for (int i = 0; i < Math.min(storage.getSlots(), ingredients.size()); i++) {
			storage.setStackInSlot(i, ingredients.get(i).copyWithCount(1));
		}
		ShakerItem.setStorage(shaker, storage);
		return shaker;
	}

	private static ItemStack createReadyShaker(ItemStack input, ItemStack cocktail) {
		ItemStack output = input.copy();
		ItemStack result = cocktail.copy();
		result.set(CGTDataComponents.SHAKER_COCKTAIL_AMOUNT, ShakerIngredientConversions.SLOT_AMOUNT);
		ShakerItem.setResult(output, result);
		return output;
	}

	private static ItemStack createSignatureCocktail(ItemStack input) {
		ItemStack cocktail = new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
		CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(ShakerItem.getStorage(input));
		SignatureCocktailBlockItem.setEffects(cocktail, CocktailEffectHelper.mergeEffects(data.effects()));
		SignatureCocktailBlockItem.setColor(cocktail, ColorUtils.mixColors(data.colors()));
		return cocktail;
	}

	private static boolean shouldHideAutomaticDrinkFilling(RecipeHolder<FillingRecipe> holder) {
		return holder.id().getNamespace().equals("create") && holder.id().getPath().startsWith("fill_")
			&& Arrays.stream(holder.value().getRequiredFluid().getFluids())
				.anyMatch(fluid -> fluid.getFluid() instanceof KaleidoscopeDrinkFluid);
	}

	private static boolean shouldHideAutomaticDrinkEmptying(RecipeHolder<EmptyingRecipe> holder) {
		return holder.id().getNamespace().equals("create") && holder.id().getPath().startsWith("empty_")
			&& holder.value().getResultingFluid().getFluid() instanceof KaleidoscopeDrinkFluid;
	}

	private static <T extends Recipe<?>> RecipeType<RecipeHolder<T>> createHolderType(String path) {
		return RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath("create", path));
	}

	private static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID, path);
	}

}
