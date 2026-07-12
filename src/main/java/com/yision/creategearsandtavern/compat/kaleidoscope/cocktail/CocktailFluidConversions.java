package com.yision.creategearsandtavern.compat.kaleidoscope.cocktail;

import java.util.List;

import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition.ServingKind;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition.TransferMode;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CocktailFluidConversions {
    private CocktailFluidConversions() {
    }

    public static FluidStack fromResult(ItemStack result) {
        if (result == null || result.isEmpty()) {
            return FluidStack.EMPTY;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (isFormalCocktail(itemId)) {
            return CGTFluids.of(itemId, CGTDrinkCatalog.COCKTAIL_AMOUNT, CGTDrinkCatalog.LEVELLESS_BREW_LEVEL);
        }
        if (result.getItem() == ModItems.SIGNATURE_COCKTAIL.get()) {
            List<DrinkEffectData.Entry> effects = SignatureCocktailBlockItem.getEffects(result);
            int color = SignatureCocktailBlockItem.getColor(result);
            return CGTFluids.signatureCocktailStack(CGTDrinkCatalog.COCKTAIL_AMOUNT, effects, color);
        }
        return FluidStack.EMPTY;
    }

    public static FluidStack fromShakerResult(ItemStack result, ItemStackHandler storage) {
        if (result == null || result.isEmpty()) {
            return FluidStack.EMPTY;
        }
        if (result.getItem() == ModItems.SIGNATURE_COCKTAIL.get()
            && !SignatureCocktailBlockItem.hasEffects(result)
            && storage != null) {
            CocktailEffectHelper.CollectedData data = CocktailEffectHelper.collectFromStorage(storage);
            List<DrinkEffectData.Entry> effects = CocktailEffectHelper.mergeEffects(data.effects());
            int color = ColorUtils.mixColors(data.colors());
            return CGTFluids.signatureCocktailStack(CGTDrinkCatalog.COCKTAIL_AMOUNT, effects, color);
        }
        return fromResult(result);
    }

    public static ItemStack toCocktailItem(FluidStack fluid) {
        if (fluid == null || fluid.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!(fluid.getFluid() instanceof KaleidoscopeDrinkFluid)) {
            return ItemStack.EMPTY;
        }
        KaleidoscopeDrinkVariant variant = KaleidoscopeDrinkFluid.variant(fluid);
        ResourceLocation drinkId = variant.drinkId();
        if (isFormalCocktail(drinkId)) {
            Item item = BuiltInRegistries.ITEM.get(drinkId);
            if (item == Items.AIR) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(item);
        }
        if (CGTDrinkCatalog.isSignatureCocktail(drinkId)) {
            ItemStack signature = new ItemStack(ModItems.SIGNATURE_COCKTAIL.get());
            List<DrinkEffectData.Entry> effects = fluid.get(ModDataComponents.SIGNATURE_COCKTAIL_EFFECTS.get());
            if (effects != null) {
                SignatureCocktailBlockItem.setEffects(signature, effects);
            }
            Integer color = fluid.get(ModDataComponents.SIGNATURE_COCKTAIL_COLOR.get());
            if (color != null) {
                SignatureCocktailBlockItem.setColor(signature, color);
            }
            return signature;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isFormalCocktail(ResourceLocation itemId) {
        if (!CGTDrinkCatalog.hasDrinkId(itemId)) {
            return false;
        }
        return CGTDrinkCatalog.byDrinkId(itemId).servingKind() == ServingKind.COCKTAIL_GLASS
            && CGTDrinkCatalog.byDrinkId(itemId).transferMode() == TransferMode.STATIC_RECIPE;
    }
}
