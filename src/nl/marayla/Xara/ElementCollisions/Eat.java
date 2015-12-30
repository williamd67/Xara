package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;


/*
 * EAT: Element <code>dynamic</code> eats <code>static</code>
 *      <code>static</code> is destroyed
 *      <code>dynamic</code> moves to position of <code>static</code>
 */
public final class Eat extends ElementCollision {
    public static final ElementCollision INSTANCE = new Eat();
    @Override
    public ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();

        ElementCollisionData data = ElementCollisionData.createInstance(
                Field.Action.REMOVE,
                collideInto.getIndex(),
                null,
                Field.Direction.STATIC,
                null
        );
        list.add(data);

        collider.setAction(Field.Action.ADD);
        collider.setIndex(collideInto.getIndex());
        list.add(collider);

        return list;
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (
                (collision == Bounce.INSTANCE) ||
                (collision == Eat.INSTANCE) ||
                (collision == Eaten.INSTANCE) ||
                (collision == Push.INSTANCE) ||
                (collision == Stick.INSTANCE)
        );
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return false;
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return false;
    }

    private Eat() {
    }
}