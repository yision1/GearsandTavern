package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModFluids;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import dev.xkmc.fruitsdelight.init.food.FruitType;
import dev.xkmc.fruitsdelight.init.plants.FDBushes;
import dev.xkmc.fruitsdelight.init.plants.FDTrees;
import net.zhaiji.kaleidoscope_fruit_brew.register.KFBFluid;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidType;

public class CGTCompactingRecipeGen extends CompactingRecipeGen {
    private static final TagKey<Item> GRAPES = ItemTags.create(new ResourceLocation("forge", "fruits/grapes"));
    private static final int JUICE_INGREDIENT_COUNT = 8;

    public CGTCompactingRecipeGen(PackOutput output) {
        super(output, CreateGearsandTavern.MOD_ID);

        createJuiceFromTag("grape_juice_from_grapes", GRAPES, ModFluids.GRAPE_JUICE.get());
        createJuiceFromItem("sweet_berries_juice_from_sweet_berries", Items.SWEET_BERRIES, ModFluids.SWEET_BERRIES_JUICE.get());
        createJuiceFromItem("glow_berries_juice_from_glow_berries", Items.GLOW_BERRIES, ModFluids.GLOW_BERRIES_JUICE.get());
        createJuiceFromItem("ice_grape_juice_from_ice_grape", ModItems.ICE_GRAPE.get(), ModFluids.ICE_GRAPE_JUICE.get());
        createJuiceFromItem("gold_grape_juice_from_gold_grape", ModItems.GOLD_GRAPE.get(), ModFluids.GOLD_GRAPE_JUICE.get());
        createJuiceFromItem("green_grape_juice_from_green_grape", ModItems.GREEN_GRAPE.get(), ModFluids.GREEN_GRAPE_JUICE.get());

        // 果酒果汁压缩盆配方
        createFruitJuiceFromTag("bayberry_juice_from_bayberry", FDTrees.BAYBERRY.getFruitTag(), KFBFluid.BAYBERRY_JUICE.get());
        createFruitJuiceFromTag("blueberry_juice_from_blueberry", FDBushes.BLUEBERRY.getFruitTag(), KFBFluid.BLUEBERRY_JUICE.get());
        createFruitJuiceFromTag("cranberry_juice_from_cranberry", FDBushes.CRANBERRY.getFruitTag(), KFBFluid.CRANBERRY_JUICE.get());
        createFruitJuiceFromTag("fig_juice_from_fig", FDTrees.FIG.getFruitTag(), KFBFluid.FIG_JUICE.get());
        createFruitJuiceFromTag("hawberry_juice_from_hawberry", FDTrees.HAWBERRY.getFruitTag(), KFBFluid.HAWBERRY_JUICE.get());
        createFruitJuiceFromTag("kiwi_juice_from_kiwi", FDTrees.KIWI.getFruitTag(), KFBFluid.KIWI_JUICE.get());
        createFruitJuiceFromTag("lemon_juice_from_lemon", FDBushes.LEMON.getFruitTag(), KFBFluid.LEMON_JUICE.get());
        createFruitJuiceFromTag("lychee_juice_from_lychee", FDTrees.LYCHEE.getFruitTag(), KFBFluid.LYCHEE_JUICE.get());
        createFruitJuiceFromTag("mango_juice_from_mango", FDTrees.MANGO.getFruitTag(), KFBFluid.MANGO_JUICE.get());
        createFruitJuiceFromTag("mangosteen_juice_from_mangosteen", FDTrees.MANGOSTEEN.getFruitTag(), KFBFluid.MANGOSTEEN_JUICE.get());
        createFruitJuiceFromTag("orange_juice_from_orange", FDTrees.ORANGE.getFruitTag(), KFBFluid.ORANGE_JUICE.get());
        createFruitJuiceFromTag("peach_juice_from_peach", FDTrees.PEACH.getFruitTag(), KFBFluid.PEACH_JUICE.get());
        createFruitJuiceFromTag("pear_juice_from_pear", FDTrees.PEAR.getFruitTag(), KFBFluid.PEAR_JUICE.get());
        createFruitJuiceFromTag("persimmon_juice_from_persimmon", FDTrees.PERSIMMON.getFruitTag(), KFBFluid.PERSIMMON_JUICE.get());
        createFruitJuiceFromTag("pineapple_juice_from_pineapple", ItemTags.create(new ResourceLocation("forge", "fruits/pineapple")), KFBFluid.PINEAPPLE_JUICE.get());
        createFruitJuiceFromTag("hamimelon_juice_from_cantaloupe", ItemTags.create(new ResourceLocation("forge", "fruits/cantaloupe")), KFBFluid.HAMIMELON_JUICE.get());
        createFruitJuiceFromItem("apple_juice_from_apple", Items.APPLE, KFBFluid.APPLE_JUICE.get());
        createFruitJuiceFromItem("durian_juice_from_durian", FruitType.DURIAN.getFruit(), KFBFluid.DURIAN_JUICE.get());
        createFruitJuiceFromItem("cactus_juice_from_cactus", Items.CACTUS, KFBFluid.CACTUS_JUICE.get());
    }

    private void createJuiceFromTag(String name, TagKey<Item> ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> require(b, ingredient).output(result, FluidType.BUCKET_VOLUME));
    }

    private void createJuiceFromItem(String name, ItemLike ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> require(b, ingredient).output(result, FluidType.BUCKET_VOLUME));
    }

    private static <T extends ProcessingRecipeBuilder<?>> T require(T builder, TagKey<Item> ingredient) {
        for (int i = 0; i < JUICE_INGREDIENT_COUNT; i++) {
            builder.require(ingredient);
        }
        return builder;
    }

    private static <T extends ProcessingRecipeBuilder<?>> T require(T builder, ItemLike ingredient) {
        for (int i = 0; i < JUICE_INGREDIENT_COUNT; i++) {
            builder.require(ingredient);
        }
        return builder;
    }

    private void createFruitJuiceFromTag(String name, TagKey<Item> ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> {
            require(b, ingredient).output(result, FluidType.BUCKET_VOLUME);
            b.whenModLoaded("kaleidoscope_fruit_brew");
            b.whenModLoaded("fruitsdelight");
            b.whenModLoaded("farmersdelight");
            return b;
        });
    }

    private void createFruitJuiceFromItem(String name, ItemLike ingredient, net.minecraft.world.level.material.Fluid result) {
        create(name, b -> {
            require(b, ingredient).output(result, FluidType.BUCKET_VOLUME);
            b.whenModLoaded("kaleidoscope_fruit_brew");
            b.whenModLoaded("fruitsdelight");
            b.whenModLoaded("farmersdelight");
            return b;
        });
    }
}
