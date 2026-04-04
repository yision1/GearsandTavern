package com.yision.creategearsandtavern.mixin.kaleidoscope;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;

import net.minecraft.resources.ResourceLocation;

@Mixin(BarrelBlockEntity.class)
public interface BarrelBlockEntityAccessor {
    @Accessor("recipeId")
    void cgt$setRecipeId(ResourceLocation recipeId);

    @Accessor("brewLevel")
    void cgt$setBrewLevel(int brewLevel);

    @Accessor("brewTime")
    void cgt$setBrewTime(int brewTime);
}
