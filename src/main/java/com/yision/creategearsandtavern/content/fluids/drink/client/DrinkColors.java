package com.yision.creategearsandtavern.content.fluids.drink.client;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.ysbbbbbb.kaleidoscopetavern.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopetavern.item.SignatureCocktailBlockItem;
import com.yision.creategearsandtavern.CreateGearsandTavern;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkCatalog;
import com.yision.creategearsandtavern.content.fluids.drink.CGTDrinkDefinition;
import com.yision.creategearsandtavern.content.fluids.drink.DrinkFluidConversions;
import com.yision.creategearsandtavern.content.fluids.drink.KaleidoscopeDrinkFluid;
import com.yision.creategearsandtavern.registry.CGTFluids;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

@EventBusSubscriber(modid = CreateGearsandTavern.MOD_ID, value = Dist.CLIENT)
public final class DrinkColors {
	private static final int DEFAULT_COLOR = 0xB8A080;
	private static final int CACHE_LIMIT = 1024;
	private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath("create", "fluid/potion_still");
	private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath("create",
		"fluid/potion_flow");
	private static final Map<ColorKey, Integer> COLORS = new LinkedHashMap<>(128, 0.75f, true);
	private static final Map<ColorKey, ItemStack> PENDING = new LinkedHashMap<>();
	private static boolean ready;

	private DrinkColors() {
	}

	@SubscribeEvent
	public static void registerExtensions(RegisterClientExtensionsEvent event) {
		IClientFluidTypeExtensions extension = new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return STILL;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return FLOWING;
			}

			@Override
			public int getTintColor(FluidStack stack) {
				return color(stack);
			}
		};
		for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
			event.registerFluidType(extension, CGTFluids.getEntry(definition.drinkId()).type());
		}
	}

	@SubscribeEvent
	public static void modelsReady(ModelEvent.BakingCompleted event) {
		COLORS.clear();
		PENDING.clear();
		ready = true;
		for (CGTDrinkDefinition definition : CGTDrinkCatalog.allDefinitions()) {
			ItemStack sample = definition.item().getDefaultInstance();
			sample.remove(ModDataComponents.BREW_LEVEL.get());
			PENDING.put(key(definition, sample), sample);
		}
	}

	private static int color(FluidStack fluid) {
		CGTDrinkDefinition definition = CGTFluids.definition(fluid.getFluid());
		if (definition == null) {
			return 0xFF000000 | DEFAULT_COLOR;
		}
		ItemStack sample = DrinkFluidConversions.toItem(fluid);
		if (sample.isEmpty()) {
			return 0xFF000000 | DEFAULT_COLOR;
		}
		sample.remove(ModDataComponents.BREW_LEVEL.get());
		ColorKey key = key(definition, sample);
		Integer cached = COLORS.get(key);
		if (cached == null && ready && PENDING.size() < CACHE_LIMIT) {
			PENDING.putIfAbsent(key, sample);
		}
		int base = cached == null ? DEFAULT_COLOR : cached;
		int level = KaleidoscopeDrinkFluid.variant(fluid).brewLevel();
		float brightness = definition.qualityAware() && level > 0 ? 0.78f + Math.min(level, 7) * 0.05f : 1;
		int red = Math.min(255, Math.round((base >> 16 & 255) * brightness));
		int green = Math.min(255, Math.round((base >> 8 & 255) * brightness));
		int blue = Math.min(255, Math.round((base & 255) * brightness));
		return 0xFF000000 | red << 16 | green << 8 | blue;
	}

	private static ColorKey key(CGTDrinkDefinition definition, ItemStack sample) {
		return new ColorKey(definition.itemId(), sample.getComponentsPatch());
	}

	private static int extract(ItemStack sample) {
		Minecraft minecraft = Minecraft.getInstance();
		BakedModel model = minecraft.getItemRenderer().getModel(sample, minecraft.level, null, 0);
		if (model.isCustomRenderer() || model == minecraft.getModelManager().getMissingModel()) {
			return DEFAULT_COLOR;
		}
		Map<Material, BitSet> regions = new LinkedHashMap<>();
		ItemStack tintProbe = null;
		if (sample.getItem() instanceof SignatureCocktailBlockItem) {
			tintProbe = sample.copy();
			SignatureCocktailBlockItem.setColor(tintProbe, SignatureCocktailBlockItem.getColor(sample) ^ 0xFFFFFF);
		}
		for (BakedModel pass : model.getRenderPasses(sample, false)) {
			for (var renderType : pass.getRenderTypes(sample, false)) {
				collect(pass.getQuads(null, null, RandomSource.create(0), ModelData.EMPTY, renderType), sample,
					tintProbe, regions);
				for (Direction direction : Direction.values()) {
					collect(pass.getQuads(null, direction, RandomSource.create(0), ModelData.EMPTY, renderType),
						sample, tintProbe, regions);
				}
			}
		}
		DrinkPalette palette = new DrinkPalette();
		boolean hasDrinkTint = tintProbe != null && regions.keySet().stream().anyMatch(Material::tinted);
		for (var entry : regions.entrySet()) {
			Material material = entry.getKey();
			if (hasDrinkTint && !material.tinted()) {
				continue;
			}
			TextureAtlasSprite sprite = material.sprite();
			int width = sprite.contents().width();
			int height = sprite.contents().height();
			int columns = sprite.contents().getOriginalImage().getWidth() / width;
			int rows = sprite.contents().getOriginalImage().getHeight() / height;
			int frame = Math.clamp(sprite.contents().getUniqueFrames().min().orElse(0), 0, columns * rows - 1);
			int sampleWidth = Math.min(width, 64);
			int sampleHeight = Math.min(height, 64);
			int[] samples = new int[sampleWidth * sampleHeight];
			BitSet pixels = entry.getValue();
			for (int index = pixels.nextSetBit(0); index >= 0; index = pixels.nextSetBit(index + 1)) {
				int x = index % sampleWidth;
				int y = index / sampleWidth;
				int pixel = sprite.getPixelRGBA(frame, x * width / sampleWidth, y * height / sampleHeight);
				int alpha = pixel >>> 24;
				if (alpha == 0) {
					continue;
				}
				int red = (pixel & 255) * (material.tint() >> 16 & 255) / 255;
				int green = (pixel >> 8 & 255) * (material.tint() >> 8 & 255) / 255;
				int blue = (pixel >> 16 & 255) * (material.tint() & 255) / 255;
				samples[index] = alpha << 24 | red << 16 | green << 8 | blue;
			}
			palette.add(samples);
		}
		return palette.representative(DEFAULT_COLOR);
	}

	private static void collect(List<BakedQuad> quads, ItemStack sample, ItemStack tintProbe,
		Map<Material, BitSet> regions) {
		for (BakedQuad quad : quads) {
			TextureAtlasSprite sprite = quad.getSprite();
			if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
				continue;
			}
			int tint = quad.isTinted() ? Minecraft.getInstance().getItemColors().getColor(sample, quad.getTintIndex())
				: -1;
			boolean tinted = quad.isTinted() && (tintProbe == null ? (tint & 0xFFFFFF) != 0xFFFFFF
				: tint != Minecraft.getInstance().getItemColors().getColor(tintProbe, quad.getTintIndex()));
			Material material = new Material(sprite, tint, tinted);
			int width = Math.min(sprite.contents().width(), 64);
			int height = Math.min(sprite.contents().height(), 64);
			float minU = 1;
			float minV = 1;
			float maxU = 0;
			float maxV = 0;
			int[] vertices = quad.getVertices();
			int stride = vertices.length / 4;
			for (int vertex = 0; vertex < 4; vertex++) {
				float u = (Float.intBitsToFloat(vertices[vertex * stride + 4]) - sprite.getU0())
					/ (sprite.getU1() - sprite.getU0());
				float v = (Float.intBitsToFloat(vertices[vertex * stride + 5]) - sprite.getV0())
					/ (sprite.getV1() - sprite.getV0());
				minU = Math.min(minU, u);
				maxU = Math.max(maxU, u);
				minV = Math.min(minV, v);
				maxV = Math.max(maxV, v);
			}
			int left = Math.clamp((int) Math.floor(minU * width), 0, width - 1);
			int right = Math.clamp((int) Math.ceil(maxU * width), left + 1, width);
			int top = Math.clamp((int) Math.floor(minV * height), 0, height - 1);
			int bottom = Math.clamp((int) Math.ceil(maxV * height), top + 1, height);
			BitSet pixels = regions.computeIfAbsent(material, ignored -> new BitSet(width * height));
			for (int y = top; y < bottom; y++) {
				pixels.set(y * width + left, y * width + right);
			}
		}
	}

	private record ColorKey(ResourceLocation itemId, DataComponentPatch appearance) {
	}

	private record Material(TextureAtlasSprite sprite, int tint, boolean tinted) {
	}

	@EventBusSubscriber(modid = CreateGearsandTavern.MOD_ID, value = Dist.CLIENT)
	public static final class Updates {
		@SubscribeEvent
		public static void tick(ClientTickEvent.Post event) {
			if (!ready) {
				return;
			}
			for (int i = 0; i < 8 && !PENDING.isEmpty(); i++) {
				var entry = PENDING.entrySet().iterator().next();
				ColorKey key = entry.getKey();
				ItemStack sample = entry.getValue();
				PENDING.remove(key);
				int color;
				try {
					color = extract(sample);
				} catch (RuntimeException exception) {
					CreateGearsandTavern.LOGGER.debug("Cannot extract drink color for {}", key.itemId(), exception);
					color = DEFAULT_COLOR;
				}
				COLORS.put(key, color);
				while (COLORS.size() > CACHE_LIMIT) {
					COLORS.remove(COLORS.keySet().iterator().next());
				}
			}
		}
	}
}
