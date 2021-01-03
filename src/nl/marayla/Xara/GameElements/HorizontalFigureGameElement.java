package nl.marayla.Xara.GameElements;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.FigureInfo;
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;

public class HorizontalFigureGameElement extends FigureGameElement {
    public HorizontalFigureGameElement(final FigureInfo figureInfo, final Field.ConstantDirection direction) {
        super(figureInfo, direction);
    }

    @Override
    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        Paint paint = renderData.getPaint();
        paint.setColor(getColor());
        paint.setStyle(Paint.Style.FILL);
        ConstantRectangle area = renderData.getElementArea(position);
        renderData.getCanvas().drawRectangle(area, paint);
    }

    private Color getColor() {
        return Color.rgb(0, 128, 255);
    }
}