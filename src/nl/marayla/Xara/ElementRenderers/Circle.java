package nl.marayla.Xara.ElementRenderers;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.Renderer.Color;

public class Circle implements ElementRenderer {
    public Circle(final Color color) {
        this.color = color;
    }
    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        Paint paint = renderData.getPaint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        ConstantRectangle area = renderData.getElementArea(position);
        renderData.getCanvas().drawCircle(area.center(), area.width()/2, paint);
    }
    private final Color color;
}