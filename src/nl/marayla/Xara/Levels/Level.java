package nl.marayla.Xara.Levels;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Figure;
import nl.marayla.Xara.LevelRenderer;
import nl.marayla.Xara.LevelRendererCreator;
import nl.marayla.Xara.Renderer.ConstantRectangle;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.ElementCollisions.ElementCollision;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.StandardElementCollisionResolver;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.GameElements.GameElement;

/**
 *
 * Level contains:
 *      field
 *      figure
 *      renderer
 *      elementRenderers
 *      elementCollisionResolver
 *
 * @author wid
 *
 */
public abstract class Level implements LevelGamePlay {
    @Override
    public final ElementCollision determineElementCollision(
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return elementCollisionResolver.getElementElementCollision(dynamicElement, staticElement);
    }

    @Override
    public final void renderElement(
        final GameElement element,
        final RenderData renderData,
        final Field.ConstantPosition position
    ) {
        elementRenderers[element.ordinal()].render(renderData, position);
    }

    public final void render(final RenderData renderData) {
        renderer.render(this, renderData);
    }

    public final void initialize() {
        Field.initialize(getSize(), getTopLinePosition());
        figure.initialize(getFigureMinArea(), getFigureMaxArea());

        time = initialTime();

        doInitialize();
        elementRenderers = createElementRenderers();
        elementCollisionResolver = createElementCollisionResolver();
    }

    public final void nextFrame() {
        Field.nextFrame(this);
        figure.nextFrame();
        doNextFrame();

        time--;
    }

    public abstract boolean failed();
    public abstract boolean succeeded();
    public abstract Field.ConstantSize getSize(); // TODO Check visibility was protected
    public final int getTime() {
        return time;
    }

    protected Level(
        final Figure figure,
        final LevelRendererCreator levelRendererCreator
    ) {
        this.figure = figure;
        this.renderer = levelRendererCreator.create(figure);
    }

    protected void doInitialize() {
    }

    protected ElementCollisionResolver doCreateElementCollisionResolver(
        final int numberOfElements
    ) {
        return new StandardElementCollisionResolver(numberOfElements);
    }

    protected abstract void doNextFrame();
    protected abstract int initialTime(); // TODO Remove
    protected abstract ElementRenderer createElementRenderer(GameElement element);
    protected abstract void setupElementCollisionResolver(ElementCollisionResolver resolver);
    protected abstract GameElement[] getLevelElements();
    protected abstract Field.TopLinePosition getTopLinePosition();
    protected abstract Field.ConstantPosition getFigurePosition();
    protected abstract Field.ConstantPosition getFigureMinArea();
    protected abstract Field.ConstantPosition getFigureMaxArea();

    private ElementRenderer[] createElementRenderers() {
        GameElement[] levelElements = getLevelElements();
        ElementRenderer[] renderers = new ElementRenderer[levelElements.length];
        for (GameElement element : levelElements) {
            int index = element.ordinal();
            renderers[index] = createElementRenderer(element);
            assert (renderers[index] != null);
        }
        return renderers;
    }
    private ElementCollisionResolver createElementCollisionResolver() {
        ElementCollisionResolver resolver = doCreateElementCollisionResolver(
            getLevelElements().length
        );
        assert (resolver != null);
        setupElementCollisionResolver(resolver);
        return resolver;
    }
    private int time;
    protected Figure figure; // TODO make private again
    private LevelRenderer renderer;
    private ElementRenderer[] elementRenderers;
    private ElementCollisionResolver elementCollisionResolver;
}