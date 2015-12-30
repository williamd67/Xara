package nl.marayla.Xara.ElementCollisions;

import org.jetbrains.annotations.Contract;

/*
 *  EATEN: <code>static</code> eats <code>dynamic</code>
 *      <code>dynamic</code> is destroyed
 *      Position of <code>static</code> does not change
 */
public final class Eaten extends ElementCollision {
    public static final ElementCollision INSTANCE = new Eaten();
    @Contract("_, _ -> !null")
    @Override
    public ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData colliderInto
    ) {
        // Nothing to do as dynamicElement already removed
        return ElementCollisionData.List.getInstance();
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (collision == Bounce.INSTANCE);
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return (
                (collision == Eat.INSTANCE) ||
                (collision == Eaten.INSTANCE) ||
                (collision == Push.INSTANCE) ||
                (collision == Stick.INSTANCE)
        );
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return false;
    }

    private Eaten() {
    }
}