package com.yision.creategearsandtavern.content.fluids.drink.client;

final class DrinkPalette {
	private static final int MIN_BRIGHTNESS = 128;
	private double red;
	private double green;
	private double blue;
	private double weight;

	void add(int[] pixels) {
		for (int pixel : pixels) {
			int alpha = pixel >>> 24;
			if (alpha == 0) {
				continue;
			}
			double amount = alpha / (255.0 * pixels.length);
			red += (pixel >> 16 & 255) * amount;
			green += (pixel >> 8 & 255) * amount;
			blue += (pixel & 255) * amount;
			weight += amount;
		}
	}

	int representative(int fallback) {
		if (weight == 0) {
			return fallback;
		}
		double r = red / weight;
		double g = green / weight;
		double b = blue / weight;
		double maximum = Math.max(r, Math.max(g, b));
		if (maximum == 0) {
			return MIN_BRIGHTNESS << 16 | MIN_BRIGHTNESS << 8 | MIN_BRIGHTNESS;
		}
		double brightness = Math.max(1, MIN_BRIGHTNESS / maximum);
		return (int) Math.round(r * brightness) << 16 | (int) Math.round(g * brightness) << 8
			| (int) Math.round(b * brightness);
	}
}
