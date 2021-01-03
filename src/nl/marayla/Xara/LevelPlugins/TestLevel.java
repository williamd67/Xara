package nl.marayla.Xara.LevelPlugins;

import nl.marayla.Xara.ElementCollisions.*;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.Figure;
import nl.marayla.Xara.LevelRendererCreator; // TODO Move to Levels
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.SimpleLevelRenderer; // TODO Move to levels
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.ElementEffects.NoEffect;
import nl.marayla.Xara.ElementRenderers.Circle;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.GameElements.SimpleFigureGameElement;
import nl.marayla.Xara.Levels.Level;
import org.jetbrains.annotations.Contract;

public class TestLevel extends Level {
    public TestLevel(final Figure figure) {
        super(figure, TestLevel.levelRendererCreator);
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
    @Override
    protected final int initialTime() {
        return 250;
    }

    @Override
    public final ElementEffect determineElementEffect(
        final Field.PlacingAfterCollision placing,
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return NoEffect.INSTANCE;
    }

    @Override
    protected final void doInitialize() {
        final Field.ConstantDirection fieldDirection = Field.Direction.DOWN;
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), fieldDirection);

        for (int y = 0; y < FIELD_SIZE; y += 2) {
            for (int x = 0; x < FIELD_SIZE; x += 2) {
                Field.addMovingElement(LevelElements.BALL, new Field.Position(x, y), fieldDirection.reverse());
                // Field.addStaticElement(LevelElements.BALL, new Field.Position(x+5, y+5));
            }
        }
        SimpleFigureGameElement figureGameElement = new SimpleFigureGameElement(figure, Field.Direction.STATIC);
        figure.setFigureGameElement(figureGameElement);
    }

    @Override
    protected final void doNextFrame() {
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Neutral.INSTANCE);
    }

    @Override
    protected final ElementRenderer createElementRenderer(final GameElement element) {
        return new Circle(Color.rgb(255, 255, 0));
    }

    @Override
    protected final GameElement[] getLevelElements() {
        return LevelElements.values();
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigurePosition() {
        return new Field.Position(FIELD_SIZE / 2, FIELD_SIZE / 2);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(1, 1);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1);
    }


    private enum LevelElements implements GameElement {
        BALL
    }

    private static final LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();

    protected static final int FIELD_SIZE = 15;
}