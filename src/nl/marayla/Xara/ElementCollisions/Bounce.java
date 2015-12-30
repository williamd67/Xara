package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

/*
 *  BOUNCE: <code>dynamic</code> bounces against <code>static</code>
 *      Direction of <code>dynamic</code> is reversed
 *      Position of <code>dynamic</code> does not change
 *      Position of <code>static</code> does not change
 */
public final class Bounce extends ElementCollision {
    public static final ElementCollision INSTANCE = new Bounce();

    @Override
    public ElementCollisionData.List handleCollision(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();
        collider.setAction(Field.Action.ADD);
        collider.setDirection(collider.getDirection().reverse());
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

    @Override
    protected final ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if ((other == Bounce.INSTANCE) || (other == Eaten.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Keep(collider.getDirection().reverse());
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy();
        }
        else if (other == Push.INSTANCE) {
            return new Move(
                    collider.getDirection().reverse(),
                    new Field.Position(collideInto.getDirection().getDeltaX(), collideInto.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected final ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (other == Bounce.INSTANCE) {
            return new Keep(collideInto.getDirection().reverse());
        }
        else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE)) {
            return new Move(
                    collideInto.getDirection(),
                    new Field.Position(collideInto.getDirection().getDeltaX(), collideInto.getDirection().getDeltaY())
            );
        }
        else if (other == Eaten.INSTANCE) {
            return new Destroy();
        }
        else if (other == Stick.INSTANCE) {
            return new Keep(collideInto.getDirection());
        }
        throw new UnsupportedOperationException();
    }

    private Bounce() {
    }
}