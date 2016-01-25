package nl.marayla.Xara.LevelPlugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
import org.jetbrains.annotations.Contract;

import javax.imageio.ImageIO;

public class FileBasedLevel extends Level {
    public FileBasedLevel(final Figure figure, final String fileName) {
        super(figure, levelRendererCreator);

        assert fileName != null;
        assert !fileName.isEmpty();
        try {
            this.image = ImageIO.read(new File("res/level1.bmp"));
            this.size = new Field.Size(image.getWidth(), image.getHeight());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new UnsupportedOperationException();
        }
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

    @Contract(pure = true)
    private static Field.ConstantDirection translateRGBToDirection(final int rgb) {
        if ((rgb & 0x00FF00) == 0) { // No moving object
            return Field.Direction.STATIC;
        }

        final int red = rgb >> 16;
        final int blue = rgb & 0xFF;
        if (red == 0xF0) { // Right
            if (blue == 0xF0) { // Up
                return Field.Direction.RIGHT_UP;
            }
            else if (blue == 0x80) { // Down
                return Field.Direction.RIGHT_DOWN;
            }
            else if (blue == 0x00) { // Neutral
                return Field.Direction.RIGHT;
            }
            throw new UnsupportedOperationException();
        }
        else if (red == 0x80) { // Left
            if (blue == 0xF0) { // Up
                return Field.Direction.LEFT_UP;
            }
            else if (blue == 0x80) { // Down
                return Field.Direction.LEFT_DOWN;
            }
            else if (blue == 0x00) { // Neutral
                return Field.Direction.LEFT;
            }
            throw new UnsupportedOperationException();
        }
        else if (red == 0x00) { // Neutral
            if (blue == 0xF0) { // Up
                return Field.Direction.UP;
            }
            else if (blue == 0x80) { // Down
                return Field.Direction.DOWN;
            }
            else if (blue == 0x00) { // Neutral
                return Field.Direction.STATIC;
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
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
        final int[] colorArray = image.getRGB(0, 0, size.getWidth(), size.getHeight(), null, 0, image.getWidth());
        Field.Position point = new Field.Position(0, 0);
        int index = 0;
        try {
            for (int y = 0; y < size.getHeight(); y++) {
                for (int x = 0; x < size.getWidth(); x++) {
                    final int color = (colorArray[index++] & 0x00FFFFFF);
                    if (color == 0) {
                        continue;
                    }
                    point.set(x, y);
                    Field.addMovingElement(
                        translateRGBToElement(color),
                        point,
                        translateRGBToDirection(color)
                    );
                }
            }
            image = null;
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

    private static LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();
    private Field.ConstantSize size;
    private BufferedImage image;
}