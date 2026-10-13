package com.yision.creategearsandtavern.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.DrinkFluidConversions;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTFluids;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.DataComponentFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public final class CGTJeiDrinkRecipes {
	public CGTJeiDrinkRecipes() {
	}

	public static void registerSubtypes(ISubtypeRegistration registration) {
		ISubtypeInterpreter<FluidStack> interpreter = new ISubtypeInterpreter<>() {
			@Override
			public Object getSubtypeData(FluidStack ingredient, UidContext context) {
				FluidStack normalized = DrinkFluidConversions.fromItem(DrinkFluidConversions.toItem(ingredient));
				return normalized.isEmpty() ? ingredient.getComponentsPatch() : normalized.getComponentsPatch();
			}

			@Override
			public String getLegacyStringSubtypeInfo(FluidStack ingredient, UidContext context) {
				return getSubtypeData(ingredient, context).toString();
			}
		};
		for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
			if (definition.qualityAware()) {
				continue;
			}
			var fluids = CGTFluids.getEntry(definition.drinkId());
			registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, fluids.source(), interpreter);
			registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, fluids.flowing(), interpreter);
		}
	}

	public static List<FluidStack> fluidVariants() {
		return samples(0).stream().map(DrinkFluidConversions::fromItem).toList();
	}

	public void registerRecipes(IRecipeRegistration registration,
		RecipeType<RecipeHolder<FillingRecipe>> fillingType, RecipeType<RecipeHolder<EmptyingRecipe>> emptyingType) {
		List<RecipeHolder<FillingRecipe>> filling = new ArrayList<>();
		List<RecipeHolder<EmptyingRecipe>> emptying = new ArrayList<>();
		for (ItemStack sample : samples(1)) {
			CGTDrinkDefinition definition = CGTDrinkCatalog.byItem(sample.getItem());
			FluidStack fluid = DrinkFluidConversions.fromItem(sample);
			ItemStack container = definition.containerItem().getDefaultInstance();
			String path = "jei/" + definition.itemId().getNamespace() + "/" + definition.itemId().getPath()
				+ "/" + KaleidoscopeDrinkFluid.variant(fluid).brewLevel();
			ResourceLocation fillingId = ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID,
				path + "/filling");
			ResourceLocation emptyingId = ResourceLocation.fromNamespaceAndPath(CreateGearsandTavern.MOD_ID,
				path + "/emptying");
			FillingRecipe fill = new StandardProcessingRecipe.Builder<>(FillingRecipe::new, fillingId)
				.withItemIngredients(DataComponentIngredient.of(true, container))
				.withFluidIngredients(
					new SizedFluidIngredient(DataComponentFluidIngredient.of(true, fluid), fluid.getAmount()))
				.withSingleItemOutput(sample).build();
			EmptyingRecipe empty = new StandardProcessingRecipe.Builder<>(EmptyingRecipe::new, emptyingId)
				.withItemIngredients(DataComponentIngredient.of(true, sample))
				.withFluidOutputs(fluid).withSingleItemOutput(container).build();
			RecipeHolder<FillingRecipe> fillingHolder = new RecipeHolder<>(fillingId, fill);
			RecipeHolder<EmptyingRecipe> emptyingHolder = new RecipeHolder<>(emptyingId, empty);
			filling.add(fillingHolder);
			emptying.add(emptyingHolder);
		}
		registration.addRecipes(fillingType, filling);
		registration.addRecipes(emptyingType, emptying);
	}

	private static List<ItemStack> samples(int firstQuality) {
		List<ItemStack> samples = new ArrayList<>();
		for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
			int highest = definition.qualityAware() ? IBarrel.BREWING_FINISHED : 0;
			for (int quality = definition.qualityAware() ? firstQuality : 0; quality <= highest; quality++) {
				ItemStack sample = definition.item().getDefaultInstance();
				if (definition.qualityAware()) {
					sample.set(ModDataComponents.BREW_LEVEL.get(), quality);
				}
				samples.add(sample);
			}
		}
		return samples;
	}

}
