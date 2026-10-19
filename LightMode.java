public final class LightMode {
	public static final int PAGE_BACKGROUND = 0xFFF4F6FA;
	public static final int PANEL_BACKGROUND = 0xFFFFFFFF;
	public static final int HOVER_BACKGROUND = 0xFFE8EDF5;
	public static final int BORDER = 0xFFD3DAE5;
	public static final int ACCENT = 0xFF4169D8;
	public static final int TEXT_PRIMARY = 0xFF202633;
	public static final int TEXT_SECONDARY = 0xFF596579;
	public static final int TEXT_ON_ACCENT = 0xFFFFFFFF;

	private LightMode() {
	}

	public static int textColorFor(int backgroundArgb) {
		int red = (backgroundArgb >>> 16) & 0xFF;
		int green = (backgroundArgb >>> 8) & 0xFF;
		int blue = backgroundArgb & 0xFF;
		double luminance = 0.2126 * linearize(red / 255.0)
				+ 0.7152 * linearize(green / 255.0)
				+ 0.0722 * linearize(blue / 255.0);
		return luminance > 0.179 ? TEXT_PRIMARY : TEXT_ON_ACCENT;
	}

	private static double linearize(double channel) {
		return channel <= 0.04045
				? channel / 12.92
				: Math.pow((channel + 0.055) / 1.055, 2.4);
	}

    public static int getContrastColor(int backgroundColor) {
        double luminance = 0.2126 * linearize(((backgroundColor >> 16) & 0xFF) / 255.0)
                + 0.7152 * linearize(((backgroundColor >> 8) & 0xFF) / 255.0)
                + 0.0722 * linearize((backgroundColor & 0xFF) / 255.0);

        return luminance > 0.5 ? 0xFF000000 : 0xFFFFFFFF;
    }

    private static double linearize(int channel) {
        double normalized = channel / 255.0;
        return normalized <= 0.04045
                ? normalized / 12.92
                : Math.pow((normalized + 0.055) / 1.055, 2.4);
    }
}
