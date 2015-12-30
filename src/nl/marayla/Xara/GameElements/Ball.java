package nl.marayla.Xara.GameElements;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;

public class Ball {
    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        Paint paint = renderData.getPaint();
        paint.setColor(Color.rgb(0, 224, 0));
        paint.setStyle(Paint.Style.FILL);
        ConstantRectangle area = renderData.getElementArea(position);
        renderData.getCanvas().drawCircle(area.center(), area.width() / 2, paint);
    }
}