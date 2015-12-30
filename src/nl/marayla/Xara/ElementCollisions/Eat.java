package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;


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

    protected final ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (other == Eat.INSTANCE) {
            return new Destroy();
        }
        else if (
                (other == Bounce.INSTANCE) ||
                (other == Eaten.INSTANCE) ||
                (other == Push.INSTANCE) ||
                (other == Stick.INSTANCE)
        ) {
            return new Move(
                    collider.getDirection(),
                    new Field.Position(collider.getDirection().getDeltaX(), collider.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (
                (other == Bounce.INSTANCE) ||
                (other == Eat.INSTANCE) ||
                (other == Eaten.INSTANCE) ||
                (other == Push.INSTANCE) ||
                (other == Stick.INSTANCE)
        ) {
            return new Destroy();
        }
        throw new UnsupportedOperationException();
    }

    private Eat() {
    }
}