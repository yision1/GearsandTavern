package com.yision.creategearsandtavern.registry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.yision.creategearsandtavern.CreateGearsAndTavernRegistrate;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid.KaleidoscopeDrinkFluidType;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public class CGTFluids {
    private static final CreateRegistrate REGISTRATE = CreateGearsAndTavernRegistrate.registrate();
    private static final ResourceLocation POTION_STILL = ResourceLocation.fromNamespaceAndPath("create", "fluid/potion_still");
    private static final ResourceLocation POTION_FLOW = ResourceLocation.fromNamespaceAndPath("create", "fluid/potion_flow");

    public static final FluidEntry<KaleidoscopeDrinkFluid> WINE = register(KaleidoscopeDrinkType.WINE);
    public static final FluidEntry<KaleidoscopeDrinkFluid> CHAMPAGNE = register(KaleidoscopeDrinkType.CHAMPAGNE);
    public static final FluidEntry<KaleidoscopeDrinkFluid> VODKA = register(KaleidoscopeDrinkType.VODKA);
    public static final FluidEntry<KaleidoscopeDrinkFluid> BRANDY = register(KaleidoscopeDrinkType.BRANDY);
    public static final FluidEntry<KaleidoscopeDrinkFluid> CARIGNAN = register(KaleidoscopeDrinkType.CARIGNAN);
    public static final FluidEntry<KaleidoscopeDrinkFluid> SAKURA_WINE = register(KaleidoscopeDrinkType.SAKURA_WINE);
    public static final FluidEntry<KaleidoscopeDrinkFluid> PLUM_WINE = register(KaleidoscopeDrinkType.PLUM_WINE);
    public static final FluidEntry<KaleidoscopeDrinkFluid> WHISKEY = register(KaleidoscopeDrinkType.WHISKEY);
    public static final FluidEntry<KaleidoscopeDrinkFluid> ICE_WINE = register(KaleidoscopeDrinkType.ICE_WINE);
    public static final FluidEntry<KaleidoscopeDrinkFluid> VINEGAR = register(KaleidoscopeDrinkType.VINEGAR);

    private static FluidEntry<KaleidoscopeDrinkFluid> register(KaleidoscopeDrinkType drinkType) {
        return REGISTRATE.virtualFluid(drinkType.id().getPath(), POTION_STILL, POTION_FLOW,
                KaleidoscopeDrinkFluidType::new, KaleidoscopeDrinkFluid::createSource, KaleidoscopeDrinkFluid::createFlowing)
            .register();
    }

    public static FluidStack of(KaleidoscopeDrinkType drinkType, int amount, int brewLevel) {
        FluidStack fluidStack = new FluidStack(entry(drinkType).get().getSource(), amount);
        fluidStack.set(CGTDataComponents.KALEIDOSCOPE_DRINK_VARIANT, KaleidoscopeDrinkVariant.of(drinkType, brewLevel));
        return fluidStack;
    }

    public static FluidStack bucketOf(KaleidoscopeDrinkType drinkType) {
        return of(drinkType, FluidType.BUCKET_VOLUME, 1);
    }

    public static FluidEntry<KaleidoscopeDrinkFluid> entry(KaleidoscopeDrinkType drinkType) {
        return switch (drinkType) {
            case WINE -> WINE;
            case CHAMPAGNE -> CHAMPAGNE;
            case VODKA -> VODKA;
            case BRANDY -> BRANDY;
            case CARIGNAN -> CARIGNAN;
            case SAKURA_WINE -> SAKURA_WINE;
            case PLUM_WINE -> PLUM_WINE;
            case WHISKEY -> WHISKEY;
            case ICE_WINE -> ICE_WINE;
            case VINEGAR -> VINEGAR;
        };
    }

    public static void register() {
    }
}
