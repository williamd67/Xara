package nl.marayla.Xara.ElementRenderers;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.Renderer.Color;

public class Rectangle implements ElementRenderer {
    public Rectangle(final Color color) {
        this.color = color;
    }

    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        Paint paint = renderData.getPaint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        renderData.getCanvas().drawRectangle(renderData.getElementArea(position), paint);
    }

    private final Color color;
}