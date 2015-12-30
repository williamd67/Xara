package nl.marayla.Xara.ElementRenderers;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.RenderData;

public interface ElementRenderer {
    void render(final RenderData renderData, final Field.ConstantPosition position);
}