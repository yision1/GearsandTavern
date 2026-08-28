package com.yision.creategearsandtavern.compat.jei;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.ysbbbbbb.kaleidoscopetavern.crafting.recipe.ShakerRecipe;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.ShakerItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.util.CocktailEffectHelper;
import com.github.ysbbbbbb.kaleidoscopetavern.util.ColorUtils;
import com.simibubi.create.AllBlocks;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.ShakerIngredientConversions;
import com.yision.creategearsandtavern.compat.kaleidoscope.shaker.ShakerMixing;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;
import com.yision.creategearsandtavern.registry.CGTFluids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemStackHandler;

@JeiPlugin
@ParametersAreNonnullByDefault
public class CGTJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(CreateGearsandTavern.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CGTShakerJeiCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        List<ShakerRecipe> ktRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.SHAKER_RECIPE);
        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (ShakerRecipe recipe : ktRecipes) {
            Optional<List<ItemStack>> representative = getRepresentativeBottles(recipe);
            if (representative.isEmpty()) {
                continue;
            }

            List<ItemStack> bottles = representative.get();
            ItemStack cocktail = recipe.assemble(createInput(bottles), level.registryAccess());
            if (cocktail.isEmpty()) {
                continue;
            }

            ItemStack input = createShaker(bottles);
            inputs.add(input);
            outputs.add(createReadyShaker(input, cocktail));
        }

        createSignatureInput(level, ktRecipes)
            .ifPresent(input -> {
                inputs.add(input);
                outputs.add(createReadyShaker(input, createSignatureCocktail(input)));
            });

        if (!inputs.isEmpty()) {
            registration.addRecipes(CGTShakerJeiCategory.TYPE,
                List.of(new CGTShakerJeiRecipe(inputs, outputs)));
        }
    }

    private static Optional<List<ItemStack>> getRepresentativeBottles(ShakerRecipe recipe) {
        List<ItemStack> bottles = new ArrayList<>(ShakerIngredientConversions.INPUT_SLOTS);
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }

            Optional<ItemStack> representative = java.util.Arrays.stream(ingredient.getItems())
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
        ClientLevel level, List<ShakerRecipe> ktRecipes) {
        List<ItemStack> bottles = List.of(
            minimumQuality(new ItemStack(ModItems.PLUM_WINE.get())),
            minimumQuality(new ItemStack(ModItems.LUMINOUS_BRIDE.get())),
            minimumQuality(new ItemStack(ModItems.ICE_WINE.get()))
        );
        SimpleContainer input = createInput(bottles);
        if (ktRecipes.stream().anyMatch(recipe -> recipe.matches(input, level))) {
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

    private static SimpleContainer createInput(List<ItemStack> bottles) {
        return new SimpleContainer(bottles.toArray(ItemStack[]::new));
    }

    private static ItemStack createShaker(List<ItemStack> bottles) {
        ItemStack shaker = new ItemStack(ModItems.SHAKER.get());
        ItemStackHandler storage = new ItemStackHandler(ShakerIngredientConversions.INPUT_SLOTS);
        for (int slot = 0; slot < Math.min(storage.getSlots(), bottles.size()); slot++) {
            storage.setStackInSlot(slot, bottles.get(slot).copyWithCount(1));
        }
        ShakerItem.setStorage(shaker, storage);
        return shaker;
    }

    private static ItemStack createReadyShaker(ItemStack input, ItemStack cocktail) {
        ItemStack output = input.copy();
        ItemStack result = cocktail.copy();
        result.getOrCreateTag().putInt(ShakerMixing.RESULT_AMOUNT_TAG, ShakerIngredientConversions.SLOT_AMOUNT);
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

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AllBlocks.MECHANICAL_MIXER.asStack(), CGTShakerJeiCategory.TYPE);
        registration.addRecipeCatalyst(AllBlocks.BASIN.asStack(), CGTShakerJeiCategory.TYPE);
    }

    @Override
    public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
        CGTJeiDrinkFluidSubtypeInterpreter interpreter = new CGTJeiDrinkFluidSubtypeInterpreter();
        CGTFluids.allEntries().forEach(entry -> {
            registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, entry.get().getSource(), interpreter);
            registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, entry.get().getFlowing(), interpreter);
        });
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(ForgeTypes.FLUID_STACK,
            extraIngredientVariants(loadedMods()).stream()
                .map(variant -> CGTFluids.of(variant.drinkId(), FluidType.BUCKET_VOLUME, variant.brewLevel()))
                .toList());
    }

    static Set<String> extraIngredientUids(Set<String> loadedMods) {
        return extraIngredientVariants(loadedMods).stream()
            .map(variant -> variant.drinkId() + ";" + variant.brewLevel())
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<KaleidoscopeDrinkVariant> extraIngredientVariants(Set<String> loadedMods) {
        Set<KaleidoscopeDrinkVariant> variants = new LinkedHashSet<>();
        CGTDrinkCatalog.enabledDefinitions(loadedMods).forEach(definition -> {
            variants.add(new KaleidoscopeDrinkVariant(definition.drinkId(), CGTDrinkCatalog.LEVELLESS_BREW_LEVEL));
        });
        return variants;
    }

    private static Set<String> loadedMods() {
        return ModList.get().getMods().stream()
            .map(modInfo -> modInfo.getModId())
            .collect(java.util.stream.Collectors.toSet());
    }
}
