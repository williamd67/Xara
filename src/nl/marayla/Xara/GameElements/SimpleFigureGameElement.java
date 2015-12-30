package nl.marayla.Xara.GameElements;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.FigureInfo;
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;

public class SimpleFigureGameElement extends FigureGameElement {
    public SimpleFigureGameElement(final FigureInfo figureInfo, final Field.ConstantDirection direction) {
        super(figureInfo, direction);
    }

    @Override
    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        Paint paint = renderData.getPaint();
        paint.setColor(getColor());
        paint.setStyle(Paint.Style.FILL);
        ConstantRectangle area = renderData.getElementArea(position);
        renderData.getCanvas().drawCircle(area.center(), area.width() / 2, paint);
    }

    private Color getColor() {
        FigureInfo figure = getFigureInfo();
        if (figure.isInvulnerable()) {
            return Color.rgb(192, 192, 192);
        }
        else if (figure.willDecreaseSoon()) {
            return Color.rgb(0, 255, 255);
        }
        else {
            return Color.rgb(0, 128, 255);
        }
    }
}