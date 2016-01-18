package nl.marayla.Xara.LevelPlugins;

import java.util.Random;

import nl.marayla.Xara.ElementCollisions.Bounce;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementEffects.NoEffect;
import nl.marayla.Xara.ElementRenderers.Circle;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.Figure;
import nl.marayla.Xara.GameElements.SimpleFigureGameElement;
import nl.marayla.Xara.LevelRendererCreator;
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.SimpleLevelRenderer;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.Levels.Level;
import org.jetbrains.annotations.Contract;

public class BouncingBallLevel extends Level {
    public BouncingBallLevel(final Figure figure) {
        super(figure, levelRendererCreator);
    }

    @Contract(value = " -> false", pure = true)
    @Override
    public final boolean failed() {
        return false;
    }

    @Contract(value = " -> false", pure = true)
    @Override
    public final boolean succeeded() {
        return false;
    }

    @Override
    protected final void doInitialize() {
        Field.Position point1 = new Field.Position(0, 0);
        Field.Position point2 = new Field.Position(0, size.getHeight() - 1);
        for (int x = 0; x < size.getWidth(); x++) {
            Field.addStaticElement(LevelElements.WALL, point1);
            Field.addStaticElement(LevelElements.WALL, point2);
            point1.set(point1.getX() + 1, point1.getY());
            point2.set(point2.getX() + 1, point2.getY());
        }
        point1.set(0, 1);
        point2.set(size.getWidth() - 1, 1);
        for (int y = 1; y < (size.getHeight() - 1); y++) {
            Field.addStaticElement(LevelElements.WALL, point1);
            Field.addStaticElement(LevelElements.WALL, point2);
            point1.set(point1.getX(), point1.getY() + 1);
            point2.set(point2.getX(), point2.getY() + 1);
        }
        // TODO remove
        Random random = new Random(47);
        for (Field.Direction direction : Field.Direction.values()) {
            for (int i = 0; i < 2; i++) {
                point1.set(random.nextInt(size.getWidth() - 2) + 1, random.nextInt(size.getHeight() - 2) + 1);
                Field.addMovingElement(LevelElements.BALL, point1, direction);
            }
        }

        SimpleFigureGameElement figureGameElement = new SimpleFigureGameElement(figure, Field.Direction.STATIC);
        figure.setFigureGameElement(figureGameElement);
    }

    @Override
    protected void doNextFrame() {
    }

    @Contract(pure = true)
    @Override
    protected final int initialTime() {
        return 100;
    }

    @Contract(pure = true)
    @Override
    public final Field.ConstantSize getSize() {
        return size;
    }

    @Contract(pure = true)
    @Override
    protected final Field.TopLinePosition getTopLinePosition() {
        return Field.TopLinePosition.NONE;
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigurePosition() {
        return new Field.Position(size.getWidth() / 2, size.getHeight() / 2);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(1, 1);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(size.getWidth() - 1, size.getHeight() - 1);
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Bounce.INSTANCE);
    }

    @Contract(value = "_, _, _ -> null", pure = true)
    @Override
    public final ElementEffect determineElementEffect(
        final Field.PlacingAfterCollision placing,
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return NoEffect.INSTANCE;
    }

    @Override
    protected final ElementRenderer createElementRenderer(final GameElement element) {
        switch ((LevelElements) element) {
            case FIGURE:
                // TODO improve this
                return figure.getFigureGameElement();
            case BALL:
                return new Circle(Color.rgb(255, 255, 0));
            case WALL:
                return new Circle(Color.rgb(164, 0, 32));
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected final GameElement[] getLevelElements() {
        return LevelElements.values();
    }

    private enum LevelElements implements GameElement {
        FIGURE,
        WALL,
        BALL
    }

    private static LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();
    private final Field.ConstantSize size = new Field.Size(20, 24);
}