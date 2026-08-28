package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

public final class CocktailFluidConversions {
    private CocktailFluidConversions() {
    }

    public static FluidStack fromResult(ItemStack result) {
        if (result == null || result.isEmpty() || result.getItem() != ModItems.SIGNATURE_COCKTAIL.get()) {
            return FluidStack.EMPTY;
        }
        return CGTFluids.signatureCocktailStack(
            CGTDrinkCatalog.COCKTAIL_AMOUNT,
            SignatureCocktailBlockItem.getEffects(result),
            SignatureCocktailBlockItem.getColor(result));
    }

    public static FluidStack fromShakerResult(ItemStack result, ItemStackHandler storage) {
        if (result == null || result.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (result.getItem() == ModItems.SIGNATURE_COCKTAIL.get()
            && !SignatureCocktailBlockItem.hasEffects(result) && storage != null) {
            CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(storage);
            List<DrinkEffectData.Entry> effects = CocktailEffectHelper.mergeEffects(data.effects());
            return CGTFluids.signatureCocktailStack(CGTDrinkCatalog.COCKTAIL_AMOUNT, effects,
                ColorUtils.mixColors(data.colors()));
        }
        return fromResult(result);
    }

    public static ItemStack toCocktailItem(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty() || !(fluid.getFluid() instanceof KaleidoscopeDrinkFluid)
            || !CGTDrinkCatalog.isSignatureCocktail(KaleidoscopeDrinkFluid.variant(fluid).drinkId())) {
            return ItemStack.EMPTY;
        }
        ItemStack cocktail = new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
        List<DrinkEffectData.Entry> effects = CGTFluids.signatureCocktailEffects(fluid);
        if (!effects.isEmpty()) {
            SignatureCocktailBlockItem.setEffects(cocktail, effects);
        }
        SignatureCocktailBlockItem.setColor(cocktail, CGTFluids.signatureCocktailColor(fluid));
        return cocktail;
    }
}
