package com.yision.creategearsandtavern.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class BrewLevelIngredient extends AbstractIngredient {
    public static final Serializer SERIALIZER = new Serializer();

    private final Item item;
    private final int brewLevel;

    @Nullable
    private ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;

    public BrewLevelIngredient(Item item, int brewLevel) {
        this.item = item;
        this.brewLevel = brewLevel;
    }

    private void dissolve() {
        if (itemStacks == null) {
            ItemStack stack = new ItemStack(item);
            BottleBlockItem.setBrewLevel(stack, brewLevel);
            itemStacks = new ItemStack[] { stack };
        }
    }

    @Override
    public ItemStack[] getItems() {
        dissolve();
        return itemStacks;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.is(item)) {
            return false;
        }
        return BottleBlockItem.getBrewLevel(stack) == brewLevel;
    }

    @Override
    public IntList getStackingIds() {
        if (stackingIds == null || checkInvalidation()) {
            markValid();
            dissolve();
            stackingIds = new IntArrayList(itemStacks.length);
            for (ItemStack stack : itemStacks) {
                stackingIds.add(StackedContents.getStackingIndex(stack));
            }
            stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return stackingIds;
    }

    @Override
    protected void invalidate() {
        itemStacks = null;
        stackingIds = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", new ResourceLocation(CreateGearsandTavern.MOD_ID, "brew_level_ingredient").toString());
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(item).toString());
        json.addProperty("brew_level", brewLevel);
        return json;
    }

    public Item getItem() {
        return item;
    }

    public int getBrewLevel() {
        return brewLevel;
    }

    public static class Serializer implements IIngredientSerializer<BrewLevelIngredient> {
        @Override
        public BrewLevelIngredient parse(JsonObject json) {
            Item item = CraftingHelper.getItemStack(json, false).getItem();
            int brewLevel = GsonHelper.getAsInt(json, "brew_level");
            return new BrewLevelIngredient(item, brewLevel);
        }

        @Override
        public BrewLevelIngredient parse(FriendlyByteBuf buffer) {
            Item item = buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            int brewLevel = buffer.readVarInt();
            return new BrewLevelIngredient(item, brewLevel);
        }

        @Override
        public void write(FriendlyByteBuf buffer, BrewLevelIngredient ingredient) {
            buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, ingredient.getItem());
            buffer.writeVarInt(ingredient.getBrewLevel());
        }
    }
}
