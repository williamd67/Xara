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

    protected final ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if ((other == Bounce.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Keep(collider.getDirection());
        } else if (other == Eat.INSTANCE) {
            return new Destroy();
        } else if (other == Eaten.INSTANCE) {
            return new Move(
                    collider.getDirection(),
                    new Field.Position(collider.getDirection().getDeltaX(), collider.getDirection().getDeltaY())
            );
        } else if (other == Push.INSTANCE) {
            return new Move(
                    collider.getDirection(),
                    new Field.Position(collideInto.getDirection().getDeltaX(), collideInto.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (other == Stick.INSTANCE) {
            return new Keep(collideInto.getDirection());
        }
        else if (other == Bounce.INSTANCE) {
            return new Keep(collideInto.getDirection().reverse());
        } else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE)) {
            return new Move(
                    collideInto.getDirection(),
                    new Field.Position(collideInto.getDirection().getDeltaX(), collideInto.getDirection().getDeltaY())
            );
        } else if (other == Eaten.INSTANCE) {
            return new Destroy();
        }
        throw new UnsupportedOperationException();
    }

    private Stick() {
    }
}