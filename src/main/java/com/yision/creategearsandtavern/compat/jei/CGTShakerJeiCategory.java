package com.yision.creategearsandtavern.compat.jei;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModItems;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.yision.creategearsandtavern.CreateGearsandTavern;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class CGTShakerJeiCategory implements IRecipeCategory<CGTShakerJeiRecipe> {
    public static final RecipeType<CGTShakerJeiRecipe> TYPE = RecipeType.create(
        CreateGearsandTavern.MOD_ID, "shaker_mixing", CGTShakerJeiRecipe.class);
    private static final int WIDTH = 177;
    private static final int HEIGHT = 103;

    private final AnimatedMixer mixer = new AnimatedMixer();
    private final IDrawable icon;

    public CGTShakerJeiCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemLike(ModItems.SHAKER.get());
    }

    @Override
    public RecipeType<CGTShakerJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("creategearsandtavern.recipe.shaker_mixing");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getRegistryName(CGTShakerJeiRecipe recipe) {
        return new ResourceLocation(CreateGearsandTavern.MOD_ID, "shaker_mixing");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CGTShakerJeiRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 36, 51)
            .setStandardSlotBackground()
            .addItemStacks(recipe.inputs());
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 51)
            .setStandardSlotBackground()
            .addItemStacks(recipe.outputs());
        builder.createFocusLink(input, output);
    }

    @Override
    public void draw(CGTShakerJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, 32);
        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        mixer.draw(graphics, WIDTH / 2 + 3, 34);
    }
}
