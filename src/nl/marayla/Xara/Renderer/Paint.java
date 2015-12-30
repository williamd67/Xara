package nl.marayla.Xara.Renderer;

public class Paint {
    public enum Flag {DITHER_FLAG}
    public enum Style {FILL}

    public Paint(Flag flag) {
        this.flag = flag;
    }

    public final Color getColor() { return color; }
    public final Style getStyle() { return style; }
    public final Flag getFlag() { return flag; }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    private Color color;
    private Flag flag = Flag.DITHER_FLAG;
    private Style style = Style.FILL;
}