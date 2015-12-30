package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

/*
 *  STICK: <code>dynamic</code> sticks to <code>static</code>
 *      Position of <code>dynamic</code> does not change
 *      Position of <code>static</code> does not change
 */
public final class Stick extends ElementCollision {
    public static final ElementCollision INSTANCE = new Stick();

    @Override
    public ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();
        collider.setAction(Field.Action.ADD);
        list.add(collider);
        return list;
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (collision == Bounce.INSTANCE) || (collision == Eaten.INSTANCE) || (collision == Stick.INSTANCE);
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return (collision == Eat.INSTANCE) || (collision == Push.INSTANCE);
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return false;
    }

    private Stick() {
    }
}