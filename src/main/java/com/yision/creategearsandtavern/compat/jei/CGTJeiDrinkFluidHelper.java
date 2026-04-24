package com.yision.creategearsandtavern.compat.jei;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class CGTJeiDrinkFluidHelper {
	private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

	private CGTJeiDrinkFluidHelper() {
	}

	public static boolean isDrinkFluid(FluidStack fluidStack) {
		return isDrinkFluid(fluidStack.getFluid());
	}

	public static boolean isDrinkFluid(Fluid fluid) {
		return fluid instanceof KaleidoscopeDrinkFluid;
	}

	public static void addDrinkTooltip(FluidStack fluidStack, List<Component> tooltip) {
		if (!isDrinkFluid(fluidStack)) {
			return;
		}

		KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(fluidStack);
		Optional<Item> drinkItem = BuiltInRegistries.ITEM.getOptional(variant.drinkId());
		if (drinkItem.isEmpty()) {
			return;
		}

        DrinkEffectData effectData = CGTDrinkEffectDataAccess.get(drinkItem.get());
		if (effectData == null) {
			return;
		}

		int effectIndex = variant.brewLevel() - IBarrel.BREWING_STARTED;
		if (effectIndex < 0 || effectIndex >= effectData.effects().size()) {
			return;
		}

		List<DrinkEffectData.Entry> entries = effectData.effects().get(effectIndex);
		if (entries.isEmpty()) {
			tooltip.add(NO_EFFECT);
			return;
		}

		for (DrinkEffectData.Entry entry : entries) {
			tooltip.add(formatEffect(entry));
		}
	}

	private static Component formatEffect(DrinkEffectData.Entry entry) {
		MobEffectInstance effectInstance = new MobEffectInstance(entry.effect(), entry.duration() * 20, entry.amplifier());
		MutableComponent line = Component.translatable(entry.effect().getDescriptionId());

		if (entry.amplifier() > 0) {
			line.append(" ")
				.append(Component.translatable("potion.potency." + entry.amplifier()).getString());
		}

		if (effectInstance.getDuration() > 20) {
			line.append(" (")
				.append(MobEffectUtil.formatDuration(effectInstance, 1.0f))
				.append(")");
		}

		if (entry.probability() < 0.999f) {
			line.append(" ")
				.append(Component.literal(formatProbability(entry.probability()))
					.withStyle(ChatFormatting.GOLD));
		}

		MobEffect effect = entry.effect();
		return line.withStyle(effect.getCategory().getTooltipFormatting());
	}

	private static String formatProbability(float probability) {
		float percent = probability * 100.0f;
		if (Math.abs(percent - Math.round(percent)) < 0.05f) {
			return Math.round(percent) + "%";
		}
		return String.format(Locale.ROOT, "%.1f%%", percent);
	}
}
