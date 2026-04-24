package com.yision.creategearsandtavern.mixin.kaleidoscope;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.BarrelBlockEntity;

import net.minecraft.resources.ResourceLocation;

@Mixin(BarrelBlockEntity.class)
public interface BarrelBlockEntityAccessor {
    @Accessor(value = "recipeId", remap = false)
    void cgt$setRecipeId(ResourceLocation recipeId);

    @Accessor(value = "brewLevel", remap = false)
    void cgt$setBrewLevel(int brewLevel);

    @Accessor(value = "brewTime", remap = false)
    void cgt$setBrewTime(int brewTime);
}
