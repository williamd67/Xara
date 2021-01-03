package nl.marayla.Xara.Renderer;

import org.jetbrains.annotations.Contract;

public class Color {
    public static Color BLACK = Color.rgb(0,0,0);

    @Contract("_, _, _ -> !null")
    public static Color rgb(int red, int green, int blue) { return new Color(red, green, blue); }

    public Color() {
    }

    public Color(int red, int green, int blue) {
        assert ((red >= 0) && (red <= 255));
        assert ((green >= 0) && (green <= 255));
        assert ((blue >= 0) && (blue <= 255));

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public final int getRed() {
        return red;
    }

    public final int getGreen() {
        return green;
    }

    public final int getBlue() {
        return blue;
    }

    public final double getAlpha() {
        return alpha;
    }

    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private final double alpha = 1.0;
}