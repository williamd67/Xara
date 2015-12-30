package nl.marayla.Xara;

import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.Paint;
import nl.marayla.Xara.Renderer.RenderData;

public class SimpleLevelRenderer extends LevelRenderer {
    @Override
    public final void renderBackground(final RenderData renderData) {
        renderData.getPaint().setColor(Color.rgb(102, 153, 51));
        renderData.getPaint().setStyle(Paint.Style.FILL);
        ConstantRectangle hotspotArea = renderData.getHotspotArea();
        renderData.getCanvas().drawRectangle(hotspotArea, renderData.getPaint());
    }

    @Override
    public void renderForeground(final RenderData renderData) {
        // nothing to do
    }
}