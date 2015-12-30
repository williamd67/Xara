package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

// TODO Ensure that push does not push elements on top of each other
/*
 *  PUSH: <code>dynamic</code> pushes <code>static</code>
 *      PUSH will only be executed if field that <code>static</code> will be pushed into, is empty
 *      <code>static</code> moves in direction of <code>dynamic</code>
 *      <code>dynamic</code> moves to position of <code>static</code>
 */
public final class Push extends ElementCollision {
    public static final ElementCollision INSTANCE = new Push();

    @Override
    public ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();

        collideInto.setAction(Field.Action.MOVE);
        collideInto.setDirection(collider.getDirection());
        list.add(collideInto);

        ElementCollisionData data = ElementCollisionData.createInstance(
                Field.Action.ADD,
                collider.getIndex(),
                collider.getElement(),
                collider.getDirection(),
                collider.getCollision()
        );
        list.add(data);

        collider.setAction(Field.Action.MOVE);
        list.add(collider);

        return list;
/*
        int elementNextIndex = Field.calculateIndex(staticIndex, collider.direction);
        if (Field.getElement(elementNextIndex) == null) {
            Field.moveElement(staticIndex, elementNextIndex);
            Field.addElement(staticIndex, collider.element, collider.direction);
        }
        else {
            Field.addElement(collider.index, collider.element, collider.direction);
        }
        */
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (collision == Eat.INSTANCE) || (collision == Eaten.INSTANCE) || (collision == Push.INSTANCE);
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return false;
    }

    @Contract(pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return (collision == Bounce.INSTANCE) || (collision == Stick.INSTANCE);
    }

    protected final ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (other == Push.INSTANCE) {
            return new Keep(collider.getDirection());
        }
        else if ((other == Bounce.INSTANCE) || (other == Eaten.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Move(
                    collider.getDirection(),
                    new Field.Position(collider.getDirection().getDeltaX(), collider.getDirection().getDeltaY())
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy();
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (other == Push.INSTANCE) {
            return new Keep(collideInto.getDirection());
        }
        else if (other == Bounce.INSTANCE) {
            return new Move(
                    collideInto.getDirection().reverse(),
                    new Field.Position(collider.getDirection().getDeltaX(), collider.getDirection().getDeltaY())
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Move(
                    collideInto.getDirection(),
                    new Field.Position(collideInto.getDirection().getDeltaX(), collideInto.getDirection().getDeltaY())
            );
        }
        else if (other == Eaten.INSTANCE) {
            return new Destroy();
        }
        else if (other == Stick.INSTANCE) {
            return new Move(
                    collideInto.getDirection(),
                    new Field.Position(collider.getDirection().getDeltaX(), collider.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    private Push() {
    }
}