package nl.marayla.Xara;

import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.Renderer.RenderData;

public abstract class LevelRenderer {
    public final void render(final LevelGamePlay levelGamePlay, final RenderData renderData) {
        renderBackground(renderData);
        renderGamePlay(levelGamePlay, renderData);
        renderForeground(renderData);
    }
    public final void renderGamePlay(
        final LevelGamePlay levelGamePlay,
        final RenderData renderData
    ) {
        Field.render(levelGamePlay, renderData);
    }
    public abstract void renderBackground(RenderData renderData);
    public abstract void renderForeground(RenderData renderData);
}