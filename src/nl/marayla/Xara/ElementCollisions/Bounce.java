package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Bounce extends ElementCollision {
    public static final ElementCollision INSTANCE = new Bounce();

    @Override
    protected final ElementResult doDetermineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.INSTANCE) || (other == Eaten.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Keep(element1.getDirection().reverse());
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy();
        }
        else if (other == Push.INSTANCE) {
            return new Move(
                    element1.getDirection().reverse(),
                    new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected final ElementResult doDetermineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Bounce.INSTANCE) {
            return new Keep(element2.getDirection().reverse());
        }
        else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE)) {
            if (element2.getDynamic()) {
                return new Move(
                        element2.getDirection(),
                        new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
                );
            }
            else {
                return new Keep(element2.getDirection());
            }
        }
        else if (other == Eaten.INSTANCE) {
            return new Destroy();
        }
        else if (other == Stick.INSTANCE) {
            return new Keep(element2.getDirection());
        }
        throw new UnsupportedOperationException();
    }

    private Bounce() {
    }
}