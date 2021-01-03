package nl.marayla.Xara.GameElements;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.FigureInfo;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;

/*
 * TODO Side action
 *  levelGamePlay.decreaseLife();
 */
public abstract class FigureGameElement implements ElementRenderer {
    public FigureGameElement(final FigureInfo figureInfo, Field.ConstantDirection direction) {
        this.figureInfo = figureInfo;
        this.initialDirection = direction;
        this.direction = new Field.UpdatableDirection(direction);
    }

    public void moveLeft() {
        direction.update(initialDirection.combine(Field.Direction.LEFT));
    }

    public void moveRight() {
        direction.update(initialDirection.combine(Field.Direction.RIGHT));
    }

    public void stopMoving() {
        direction.update(initialDirection);
    }

    public final Field.ConstantDirection getDirection() {
        return direction;
    }

    protected final FigureInfo getFigureInfo() {
        return figureInfo;
    }

    private final FigureInfo figureInfo;
    private final Field.ConstantDirection initialDirection;
    private final Field.UpdatableDirection direction;
}