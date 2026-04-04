package com.yision.creategearsandtavern.datagen.recipe;

import java.util.concurrent.CompletableFuture;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModFluids;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidType;

public class CGTCompactingRecipeGen extends CompactingRecipeGen {
    private static final TagKey<Item> GRAPES = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "fruits/grapes"));
    private static final int JUICE_INGREDIENT_COUNT = 8;

    public CGTCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateGearsandTavern.MOD_ID);

        createJuiceFromTag("grape_juice_from_grapes", GRAPES, ModFluids.GRAPE_JUICE.get());
        createJuiceFromItem("sweet_berries_juice_from_sweet_berries", Items.SWEET_BERRIES, ModFluids.SWEET_BERRIES_JUICE.get());
        createJuiceFromItem("glow_berries_juice_from_glow_berries", Items.GLOW_BERRIES, ModFluids.GLOW_BERRIES_JUICE.get());
    }

    private void createJuiceFromTag(String name, TagKey<Item> ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> require(b, ingredient).output(result, FluidType.BUCKET_VOLUME));
    }

    private void createJuiceFromItem(String name, ItemLike ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> require(b, ingredient).output(result, FluidType.BUCKET_VOLUME));
    }

    private static <T extends com.simibubi.create.content.processing.recipe.StandardProcessingRecipe.Builder<?>> T require(T builder, TagKey<Item> ingredient) {
        for (int i = 0; i < JUICE_INGREDIENT_COUNT; i++) {
            builder.require(ingredient);
        }
        return builder;
    }

    private static <T extends com.simibubi.create.content.processing.recipe.StandardProcessingRecipe.Builder<?>> T require(T builder, ItemLike ingredient) {
        for (int i = 0; i < JUICE_INGREDIENT_COUNT; i++) {
            builder.require(ingredient);
        }
        return builder;
    }
}
