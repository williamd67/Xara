package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Eat extends ElementCollision {
    public static final ElementCollision INSTANCE = new Eat();

    protected final ElementCollisionResult doDetermineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Eat.INSTANCE) {
            return new Destroy();
        }
        else if (
                (other == Bounce.INSTANCE) ||
                (other == Eaten.INSTANCE) ||
                (other == Push.INSTANCE) ||
                (other == Stick.INSTANCE)
        ) {
            if (element1.getDynamic()) {
                return new Move(
                        element1.getDirection(),
                        new Field.Position(element1.getDirection().getDeltaX(), element1.getDirection().getDeltaY())
                );
            }
            else {
                return new Keep(element1.getDirection());
            }
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementCollisionResult doDetermineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
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