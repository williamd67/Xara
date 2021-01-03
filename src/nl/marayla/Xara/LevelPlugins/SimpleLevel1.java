package nl.marayla.Xara.LevelPlugins;

import java.util.Random;

import nl.marayla.Xara.ElementCollisions.*;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.Figure;
import nl.marayla.Xara.LevelRendererCreator; // TODO Move to Levels
import nl.marayla.Xara.Renderer.Color;
import nl.marayla.Xara.SimpleLevelRenderer; // TODO Move to levels
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.ElementEffects.CompositeElementEffect;
import nl.marayla.Xara.ElementEffects.NoEffect;
import nl.marayla.Xara.ElementRenderers.Circle;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.GameElements.SimpleFigureGameElement;
import nl.marayla.Xara.Levels.Level;
import org.jetbrains.annotations.Contract;

public class SimpleLevel1 extends Level {
    public SimpleLevel1(final Figure figure) {
        super(figure, SimpleLevel1.levelRendererCreator);
        frameEffect = figure.new IncreaseScore(1);
    }

    @Override
    public final boolean failed() {
        return (getTime() <= 0);
    }

    @Override
    public final boolean succeeded() {
        return (getBonuses() < 0);
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
        if (dynamicElement == LevelElements.FIGURE) {
            switch ((LevelElements) staticElement) {
                case BONUS:
                    return bonusEffect;
                case BLOCK:
                    // TODO remove creation of class
                    return figure.new DecreaseLife();
                case WIDENER:
                    // TODO implement widenerEffect
                    // widenerRenderer.render(renderData, area);
                    return NoEffect.INSTANCE;
                case SUPERBONUS:
                    return superBonusEffect;
                case EXTRALIFE:
                    // TODO remove creation of class
                    return figure.new IncreaseLife();
                default:
                    throw new UnsupportedOperationException();
            }
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public final int getBonuses() {
        return bonuses;
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigurePosition() {
        return new Field.Position(SIZE.getWidth() / 2, SIZE.getHeight() - XARA_HEAD_Y);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMinArea() {
        return new Field.Position(0, SIZE.getHeight() - XARA_HEAD_Y);
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition getFigureMaxArea() {
        return new Field.Position(SIZE.getWidth(), SIZE.getHeight() - XARA_HEAD_Y);
    }

    @Override
    protected final void doInitialize() {
        Field.initialize(SIZE, DIRECTION);
        bonuses = 5;

        SimpleFigureGameElement figureGameElement = new SimpleFigureGameElement(figure, DIRECTION.reverse());
        figure.setFigureGameElement(figureGameElement);
        Field.addMovingElement(LevelElements.FIGURE, getFigurePosition(), figureGameElement.getDirection());

        bonusEffect = new CompositeElementEffect();
        bonusEffect.add(new BonusEffect(1));
        bonusEffect.add(figure.new IncreaseScore(10));

        superBonusEffect = new CompositeElementEffect();
        superBonusEffect.add(new BonusEffect(5));
        superBonusEffect.add(figure.new IncreaseScore(50));
    }

    @Override
    protected final void doNextFrame() {
        frameEffect.execute();
        int topLineSize = SIZE.getWidth();
        if (
            (DIRECTION == Field.Direction.LEFT)
                || (DIRECTION == Field.Direction.RIGHT)
        ) {
            topLineSize = SIZE.getHeight();
        }
        Field.addElementInjectionLine(LevelElements.BONUS, random.nextInt(topLineSize));
        Field.addElementInjectionLine(LevelElements.BLOCK, random.nextInt(topLineSize));
        if (random.nextInt(25) == 1) {
            Field.addElementInjectionLine(LevelElements.WIDENER, random.nextInt(topLineSize));
        }
        if (random.nextInt(25) == 1) {
            Field.addElementInjectionLine(LevelElements.SUPERBONUS, random.nextInt(topLineSize));
        }
        if (random.nextInt(250) == 1) {
            Field.addElementInjectionLine(LevelElements.EXTRALIFE, random.nextInt(topLineSize));
        }
    }

    @Override
    protected final void setupElementCollisionResolver(
        final ElementCollisionResolver resolver
    ) {
        resolver.addDefaultCollision(Neutral.INSTANCE);
        resolver.addElementCollision(Eat.INSTANCE, LevelElements.FIGURE);
    }

    @Override
    protected final ElementRenderer createElementRenderer(final GameElement element) {
        switch((LevelElements) element) {
            case FIGURE:
                // TODO improve this
                return figure.getFigureGameElement();
            case BLOCK:
                return new Circle(Color.rgb(164, 0, 32));
            case BONUS:
                return new Circle(Color.rgb(255, 255, 0));
            case EXTRALIFE:
                return new Circle(Color.rgb(0, 128, 255));
            case SUPERBONUS:
                return new Circle(Color.rgb(255, 255, 255));
            case WIDENER:
                return new Circle(Color.rgb(32, 32, 192));
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected final GameElement[] getLevelElements() {
        return LevelElements.values();
    }

    private enum LevelElements implements GameElement {
        FIGURE, BLOCK, BONUS, SUPERBONUS, EXTRALIFE, WIDENER
    }

    private final class BonusEffect implements ElementEffect {
        public BonusEffect(final int value) {
            this.value = value;
        }

        @Override
        public void execute() {
            bonuses -= value;
        }

        private final int value;
    }

    private static final LevelRendererCreator levelRendererCreator = (figureInfo) -> new SimpleLevelRenderer();

    private static final Field.ConstantSize SIZE = new Field.Size(24, 20);
    private static final Field.ConstantDirection DIRECTION = Field.Direction.DOWN;

    private final ElementEffect frameEffect;
    private int bonuses;
    private final Random random = new Random(45);
    private static final int XARA_HEAD_Y = 4;
    private CompositeElementEffect bonusEffect;
    private CompositeElementEffect superBonusEffect;
}