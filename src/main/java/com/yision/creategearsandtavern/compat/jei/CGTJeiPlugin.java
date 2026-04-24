package com.yision.creategearsandtavern.compat.jei;

import javax.annotation.ParametersAreNonnullByDefault;

import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;
import com.yision.creategearsandtavern.registry.CGTFluids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;

import java.util.LinkedHashSet;
import java.util.Set;

@JeiPlugin
@ParametersAreNonnullByDefault
public class CGTJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(CreateGearsandTavern.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
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
