package com.yision.creategearsandtavern.compat.kaleidoscope.shaker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public final class ReadyShakerIngredient extends AbstractIngredient {
    public static final Serializer SERIALIZER = new Serializer();
    private static final ReadyShakerIngredient INSTANCE = new ReadyShakerIngredient();

    private ReadyShakerIngredient() {
    }

    public static ReadyShakerIngredient get() {
        return INSTANCE;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public boolean test(ItemStack stack) {
        return ShakerMixing.captureMixerResult(stack);
    }

    @Override
    public IntList getStackingIds() {
        return new IntArrayList();
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
        json.addProperty("type", CreateGearsandTavern.MOD_ID + ":ready_shaker");
        return json;
    }

    public static final class Serializer implements IIngredientSerializer<ReadyShakerIngredient> {
        @Override
        public ReadyShakerIngredient parse(JsonObject json) {
            return INSTANCE;
        }

        @Override
        public ReadyShakerIngredient parse(FriendlyByteBuf buffer) {
            return INSTANCE;
        }

        @Override
        public void write(FriendlyByteBuf buffer, ReadyShakerIngredient ingredient) {
        }
    }
}
