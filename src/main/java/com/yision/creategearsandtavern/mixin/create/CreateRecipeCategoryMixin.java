package com.yision.creategearsandtavern.mixin.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.yision.creategearsandtavern.compat.jei.CGTJeiDrinkFluidHelper;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

@Mixin(CreateRecipeCategory.class)
public class CreateRecipeCategoryMixin {
	@Inject(method = "addPotionTooltip", at = @At("TAIL"))
	private static void cgt$addDrinkTooltip(IRecipeSlotView view, List<Component> tooltip, CallbackInfo ci) {
		Optional<FluidStack> displayed = view.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK);
		if (displayed.isEmpty()) {
			return;
		}

		FluidStack fluidStack = displayed.get();
		if (!CGTJeiDrinkFluidHelper.isDrinkFluid(fluidStack)) {
			return;
		}

		List<Component> drinkTooltip = new ArrayList<>();
		CGTJeiDrinkFluidHelper.addDrinkTooltip(fluidStack, drinkTooltip);
		if (!drinkTooltip.isEmpty()) {
			tooltip.addAll(1, drinkTooltip);
		}
	}
}
