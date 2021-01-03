package nl.marayla.Xara.LevelPlugins;

import java.util.Random;

import nl.marayla.Xara.ElementCollisions.Bounce;
import nl.marayla.Xara.ElementCollisions.Eaten;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.Neutral;
import nl.marayla.Xara.ElementEffects.NoEffect;
import nl.marayla.Xara.ElementRenderers.Circle;
import nl.marayla.Xara.ElementRenderers.Rectangle;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.Figure;
import nl.marayla.Xara.GameElements.HorizontalFigureGameElement;
import nl.marayla.Xara.LevelRendererCreator;
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.SimpleLevelRenderer;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.Levels.Level;

/*
 * TODO: move figure as one in stead of one by one -> causes holes in case of collision with ball
 * TODO: take move into account for new direction of ball
 */

public class BrickLevel extends Level {
    public BrickLevel(final Figure figure) {
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
        Field.initialize(new Field.Size(WIDTH, HEIGHT), Field.Direction.STATIC);

        Field.Position point1 = new Field.Position(-1, -1);
        Field.Position point2 = new Field.Position(-1, HEIGHT);
        for (int x = 0; x < WIDTH + 2; x++) {
            Field.addStaticElement(LevelElements.WALL_VERTICAL, point1);
            Field.addStaticElement(LevelElements.WALL_VERTICAL, point2);
            point1.set(point1.getX() + 1, point1.getY());
            point2.set(point2.getX() + 1, point2.getY());
        }
        point1.set(-1, 0);
        point2.set(WIDTH, 0);
        for (int y = 0; y < HEIGHT; y++) {
            Field.addStaticElement(LevelElements.WALL_HORIZONTAL, point1);
            Field.addStaticElement(LevelElements.WALL_HORIZONTAL, point2);
            point1.set(point1.getX(), point1.getY() + 1);
            point2.set(point2.getX(), point2.getY() + 1);
        }
        // TODO remove
        Random random = new Random(47 + new Random().nextInt(100));

        for (int i = 0; i < 20; i++) {
            point1.set(random.nextInt(WIDTH - 2) + 1, random.nextInt(HEIGHT - 2) + 1);
            Field.addStaticElement(LevelElements.BRICK, point1);
        }

        point1.set(random.nextInt(WIDTH - 2) + 1, random.nextInt(HEIGHT - 2) + 1);
        Field.addMovingElement(LevelElements.BALL, point1, Field.Direction.LEFT_DOWN);

        figure.setFigureGameElement(new HorizontalFigureGameElement(figure, Field.Direction.STATIC));
        Field.Position figurePosition = new Field.Position(getFigurePosition());
        Field.addMovingElement(
            LevelElements.BAT_LEFT,
            getFigurePosition(),
            figure.getFigureGameElement().getDirection()
        );
        figurePosition.set(figurePosition.getX() + 1, figurePosition.getY());
        Field.addMovingElement(LevelElements.BAT, figurePosition, figure.getFigureGameElement().getDirection());
        figurePosition.set(figurePosition.getX() + 1, figurePosition.getY());
        Field.addMovingElement(LevelElements.BAT_RIGHT, figurePosition, figure.getFigureGameElement().getDirection());
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
        return new Field.Position(WIDTH / 2, HEIGHT - 3);
    }

    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(1, 1);
    }

    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(WIDTH - 1, HEIGHT - 1);
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Neutral.INSTANCE);
        resolver.addElementCollision(Eaten.INSTANCE, LevelElements.BRICK);
        resolver.addElementElementCollision(Bounce.VERTICAL, LevelElements.BALL, LevelElements.WALL_HORIZONTAL);
        resolver.addElementElementCollision(Bounce.HORIZONTAL, LevelElements.BALL, LevelElements.WALL_VERTICAL);
        resolver.addElementElementCollision(Bounce.REVERSE, LevelElements.BALL, LevelElements.BRICK);
        resolver.addElementElementCollision(Bounce.REVERSE, LevelElements.BALL, LevelElements.BAT_LEFT);
        resolver.addElementElementCollision(Bounce.HORIZONTAL, LevelElements.BALL, LevelElements.BAT);
        resolver.addElementElementCollision(Bounce.REVERSE, LevelElements.BALL, LevelElements.BAT_RIGHT);
    }

    @Override
    public final ElementEffect determineElementEffect(
        final Field.PlacingAfterCollision placing,
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        if ((dynamicElement == LevelElements.BALL) && (staticElement == LevelElements.BRICK)) {
            return figure.new IncreaseScore(10);
        }
        return NoEffect.INSTANCE;
    }

    @Override
    protected final ElementRenderer createElementRenderer(final GameElement element) {
        switch ((LevelElements) element) {
            case BAT_LEFT:
            case BAT:
            case BAT_RIGHT:
                // TODO improve this
                return figure.getFigureGameElement();
            case BALL:
                return new Circle(Color.rgb(255, 255, 0));
            case WALL_VERTICAL:
            case WALL_HORIZONTAL:
                return new Circle(Color.rgb(164, 0, 32));
            case BRICK:
                return new Rectangle(Color.rgb(255, 164, 32));
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected final GameElement[] getLevelElements() {
        return LevelElements.values();
    }

    private enum LevelElements implements GameElement {
        BAT_LEFT,
        BAT,
        BAT_RIGHT,
        WALL_VERTICAL,
        WALL_HORIZONTAL,
        BALL,
        BRICK
    }

    private static final LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();
    private static final int WIDTH = 20;
    private static final int HEIGHT = 24;
}