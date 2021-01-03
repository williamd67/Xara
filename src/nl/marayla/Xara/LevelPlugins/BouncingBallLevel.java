package nl.marayla.Xara.LevelPlugins;

import java.util.Random;

import nl.marayla.Xara.ElementCollisions.Bounce;
import nl.marayla.Xara.ElementCollisions.Eaten;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.Neutral;
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

public class BouncingBallLevel extends Level {
    public BouncingBallLevel(final Figure figure) {
        super(figure, levelRendererCreator);
    }

    @Override
    public final boolean failed() {
        return false;
    }

    @Override
    public final boolean succeeded() {
        return false;
    }

    @Override
    protected final void doInitialize() {
        Field.initialize(SIZE, Field.Direction.STATIC);

        Field.Position point1 = new Field.Position(0, 0);
        Field.Position point2 = new Field.Position(0, SIZE.getHeight() - 1);
        for (int x = 0; x < SIZE.getWidth(); x++) {
            Field.addStaticElement(LevelElements.WALL_VERTICAL, point1);
            Field.addStaticElement(LevelElements.WALL_VERTICAL, point2);
            point1.set(point1.getX() + 1, point1.getY());
            point2.set(point2.getX() + 1, point2.getY());
        }
        point1.set(0, 1);
        point2.set(SIZE.getWidth() - 1, 1);
        for (int y = 1; y < (SIZE.getHeight() - 1); y++) {
            Field.addStaticElement(LevelElements.WALL_HORIZONTAL, point1);
            Field.addStaticElement(LevelElements.WALL_HORIZONTAL, point2);
            point1.set(point1.getX(), point1.getY() + 1);
            point2.set(point2.getX(), point2.getY() + 1);
        }
        // TODO remove
        Random random = new Random(47 + new Random().nextInt(100));
        for (int i = 0; i < 100; i++) {
            point1.set(random.nextInt(SIZE.getWidth() - 2) + 1, random.nextInt(SIZE.getHeight() - 2) + 1);
            Field.addStaticElement(LevelElements.BLOCK, point1);
        }
        point1.set(random.nextInt(SIZE.getWidth() - 2) + 1, random.nextInt(SIZE.getHeight() - 2) + 1);
        Field.addMovingElement(LevelElements.BALL, point1, Field.Direction.LEFT_DOWN);

        SimpleFigureGameElement figureGameElement = new SimpleFigureGameElement(figure, Field.Direction.STATIC);
        figure.setFigureGameElement(figureGameElement);
    }

    @Override
    protected void doNextFrame() {
    }

    @Override
    protected final int initialTime() {
        return 100;
    }

    @Override
    protected final Field.ConstantPosition getFigurePosition() {
        return new Field.Position(SIZE.getWidth() / 2, SIZE.getHeight() / 2);
    }

    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(1, 1);
    }

    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(SIZE.getWidth() - 1, SIZE.getHeight() - 1);
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Neutral.INSTANCE);
        resolver.addElementCollision(Eaten.INSTANCE, LevelElements.BLOCK);
        resolver.addElementElementCollision(Bounce.VERTICAL, LevelElements.BALL, LevelElements.WALL_HORIZONTAL);
        resolver.addElementElementCollision(Bounce.HORIZONTAL, LevelElements.BALL, LevelElements.WALL_VERTICAL);
        resolver.addElementElementCollision(Bounce.REVERSE, LevelElements.BALL, LevelElements.BLOCK);
    }

    @Override
    public final ElementEffect determineElementEffect(
        final Field.PlacingAfterCollision placing,
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        if ((dynamicElement == LevelElements.BALL) && (staticElement == LevelElements.BLOCK)) {
            return figure.new IncreaseScore(10);
        }
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
            case WALL_VERTICAL:
            case WALL_HORIZONTAL:
                return new Circle(Color.rgb(164, 0, 32));
            case BLOCK:
                return new Circle(Color.rgb(255, 164, 32));
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
        WALL_VERTICAL,
        WALL_HORIZONTAL,
        BALL,
        BLOCK
    }

    private static final LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();
    private static final Field.ConstantSize SIZE = new Field.Size(24, 20);
}