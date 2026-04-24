package com.yision.creategearsandtavern.datagen.recipe;

import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = CreateGearsandTavern.MOD_ID, bus = Bus.MOD)
public class CGTIngredients {
    private static final ResourceLocation BREW_LEVEL_INGREDIENT_ID =
        new ResourceLocation(CreateGearsandTavern.MOD_ID, "brew_level_ingredient");

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(BREW_LEVEL_INGREDIENT_ID, BrewLevelIngredient.SERIALIZER);
        }
    }
}
