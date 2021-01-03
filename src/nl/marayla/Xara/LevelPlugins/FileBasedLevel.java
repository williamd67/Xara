package nl.marayla.Xara.LevelPlugins;

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

import java.io.File;
import javax.imageio.ImageIO;

public class FileBasedLevel extends Level {
    public FileBasedLevel(final Figure figure, final String fileName) {
        super(figure, levelRendererCreator);

        assert fileName != null;
        assert !fileName.isEmpty();
        this.fileName = fileName;
    }

    @Override
    public final boolean failed() {
        return false;
    }

    @Override
    public final boolean succeeded() {
        return false;
    }

    private static Field.ConstantDirection translateRGBToDirection(final int rgb) {
        switch (rgb) {
            case 0xF08000:
                return Field.Direction.RIGHT;
            case 0xF08080:
                return Field.Direction.RIGHT_DOWN;
            case 0xF080F0:
                return Field.Direction.RIGHT_UP;
            case 0x808000:
                return Field.Direction.LEFT;
            case 0x808080:
                return Field.Direction.LEFT_DOWN;
            case 0x8080F0:
                return Field.Direction.LEFT_UP;
            case 0x008080:
                return Field.Direction.DOWN;
            case 0x0080F0:
                return Field.Direction.UP;
            case 0x008000:
            default:
                return Field.Direction.STATIC;
        }
    }

    private static GameElement translateRGBToElement(final int rgb) {
        if (rgb == 0xFF0000) {
            return LevelElements.WALL_HORIZONTAL;
        }
        else if (rgb == 0x0000FF) {
            return LevelElements.WALL_VERTICAL;
        }
        else if ((rgb & 0x00FF00) == 0x008000) {
            return LevelElements.BALL;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void doInitialize() {
        try {
            Field.initializeFromImage(
                ImageIO.read(new File(fileName)),
                FileBasedLevel::translateRGBToElement,
                FileBasedLevel::translateRGBToDirection,
                Field.Direction.STATIC
            );
        }
        catch (Exception e) {
            System.out.println("uncaught exception: " + e.getMessage());
        }

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
        return new Field.Position(Field.getSize().getWidth() / 2, Field.getSize().getHeight() / 2);
    }

    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(1, 1);
    }

    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(Field.getSize().getWidth() - 1, Field.getSize().getHeight() - 1);
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Neutral.INSTANCE);
        resolver.addElementCollision(Eaten.INSTANCE, LevelElements.BLOCK);
        resolver.addElementCollision(Bounce.REVERSE, LevelElements.BALL);
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
    private final String fileName;
}