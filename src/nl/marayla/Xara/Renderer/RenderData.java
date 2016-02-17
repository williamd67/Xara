package nl.marayla.Xara.Renderer;

import nl.marayla.Xara.Field;

public class RenderData {
    public RenderData(
        final Canvas canvas,
        final ConstantRectangle hotspotArea
    ) {
        this.canvas = canvas;
        this.paint = new Paint(Paint.Flag.DITHER_FLAG);
        this.hotspotArea = hotspotArea;
        this.cellSize = calculateCellSize(hotspotArea, Field.getSize());
    }

    public final Canvas getCanvas() {
        return canvas;
    }
    public final Paint getPaint() {
        return paint;
    }
    public final ConstantRectangle getHotspotArea() {
        return hotspotArea;
    }
    public final ConstantRectangle getElementArea(final Field.ConstantPosition position) {
        double left = (position.getX() * cellSize.width());
        double top = (position.getY() * cellSize.height());
        elementArea.set(left, top, left + cellSize.width(), top + cellSize.height());
        return elementArea;
    }

//    public final void setCanvas(final Canvas canvas) {
//        this.canvas = canvas;
//    }
//    public final void setSize(final Field.ConstPoint size) {
//        this.size.set(size);
//        recalculateCellSize();
//    }
//    private void setHotspotArea(final Renderer.ConstRect hotspotArea) {
//        this.hotspotArea = hotspotArea;
//        recalculateCellSize();
//    }

    private static ConstantRectangle calculateCellSize(
        final ConstantRectangle hotspotArea,
        final Field.ConstantSize size
    ) {
        if ((size.getWidth() > 0) && (size.getHeight() > 0)) {
            return new Rectangle(0, 0, hotspotArea.width() / size.getWidth(), hotspotArea.height() / size.getHeight());
        }
        return new Rectangle();
    }

    private Canvas canvas; // reference
    private Paint paint = null;
    private ConstantRectangle hotspotArea; // reference
    private Rectangle elementArea = new Rectangle();
    private ConstantRectangle cellSize;

    private static final int PRECISION = 2^10;
}