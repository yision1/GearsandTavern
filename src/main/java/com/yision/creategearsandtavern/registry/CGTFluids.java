package com.yision.creategearsandtavern.registry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.yision.creategearsandtavern.CreateGearsAndTavernRegistrate;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid.KaleidoscopeDrinkFluidType;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkType;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkVariant;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;

public class CGTFluids {
    private static final CreateRegistrate REGISTRATE = CreateGearsAndTavernRegistrate.registrate();
    private static final ResourceLocation POTION_STILL = new ResourceLocation("create", "fluid/potion_still");
    private static final ResourceLocation POTION_FLOW = new ResourceLocation("create", "fluid/potion_flow");

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

    private static final Set<String> LOADED_MODS = ModList.get().getMods().stream()
        .map(modInfo -> modInfo.getModId())
        .collect(Collectors.toUnmodifiableSet());

    private static final Map<ResourceLocation, FluidEntry<KaleidoscopeDrinkFluid>> ALL_ENTRIES = CGTDrinkCatalog.enabledDefinitions(LOADED_MODS).stream()
        .filter(def -> {
            try {
                KaleidoscopeDrinkType.byId(def.drinkId());
                return false;
            } catch (IllegalArgumentException ignored) {
                return true;
            }
        })
        .collect(Collectors.toUnmodifiableMap(
            CGTDrinkDefinition::drinkId,
            def -> registerByPath(def.drinkId().getPath())
        ));

    private static FluidEntry<KaleidoscopeDrinkFluid> register(KaleidoscopeDrinkType drinkType) {
        return REGISTRATE.virtualFluid(drinkType.path(), POTION_STILL, POTION_FLOW,
                (props, still, flow) -> new KaleidoscopeDrinkFluidType(props),
                KaleidoscopeDrinkFluid::createSource, KaleidoscopeDrinkFluid::createFlowing)
            .register();
    }

    private static FluidEntry<KaleidoscopeDrinkFluid> registerByPath(String path) {
        return REGISTRATE.virtualFluid(path, POTION_STILL, POTION_FLOW,
                (props, still, flow) -> new KaleidoscopeDrinkFluidType(props),
                KaleidoscopeDrinkFluid::createSource, KaleidoscopeDrinkFluid::createFlowing)
            .register();
    }

    public static FluidStack of(KaleidoscopeDrinkType drinkType, int amount, int brewLevel) {
        FluidStack fluidStack = new FluidStack(entry(drinkType).get().getSource(), amount);
        KaleidoscopeDrinkFluid.setVariant(fluidStack, KaleidoscopeDrinkVariant.of(drinkType, brewLevel));
        return fluidStack;
    }

    public static FluidStack of(ResourceLocation drinkId, int amount, int brewLevel) {
        FluidEntry<KaleidoscopeDrinkFluid> fluidEntry = getEntry(drinkId);
        FluidStack fluidStack = new FluidStack(fluidEntry.get().getSource(), amount);
        KaleidoscopeDrinkFluid.setVariant(fluidStack, new KaleidoscopeDrinkVariant(drinkId, brewLevel));
        return fluidStack;
    }

    public static FluidStack bucketOf(KaleidoscopeDrinkType drinkType) {
        return of(drinkType, FluidType.BUCKET_VOLUME, 1);
    }

    public static FluidEntry<KaleidoscopeDrinkFluid> entry(KaleidoscopeDrinkType drinkType) {
        switch (drinkType) {
            case WINE: return WINE;
            case CHAMPAGNE: return CHAMPAGNE;
            case VODKA: return VODKA;
            case BRANDY: return BRANDY;
            case CARIGNAN: return CARIGNAN;
            case SAKURA_WINE: return SAKURA_WINE;
            case PLUM_WINE: return PLUM_WINE;
            case WHISKEY: return WHISKEY;
            case ICE_WINE: return ICE_WINE;
            case VINEGAR: return VINEGAR;
            default: throw new IllegalArgumentException("Unknown drink type: " + drinkType);
        }
    }

    public static FluidEntry<KaleidoscopeDrinkFluid> getEntry(ResourceLocation drinkId) {
        KaleidoscopeDrinkType tavernType;
        try {
            tavernType = KaleidoscopeDrinkType.byId(drinkId);
        } catch (IllegalArgumentException ignored) {
            tavernType = null;
        }
        if (tavernType != null) {
            return entry(tavernType);
        }
        FluidEntry<KaleidoscopeDrinkFluid> extra = ALL_ENTRIES.get(drinkId);
        if (extra == null) {
            throw new IllegalArgumentException("No fluid entry for drink id: " + drinkId);
        }
        return extra;
    }

    public static Stream<FluidEntry<KaleidoscopeDrinkFluid>> allEntries() {
        return Stream.concat(
            Stream.of(WINE, CHAMPAGNE, VODKA, BRANDY, CARIGNAN, SAKURA_WINE, PLUM_WINE, WHISKEY, ICE_WINE, VINEGAR),
            ALL_ENTRIES.values().stream()
        );
    }

    public static void register() {
    }
}
