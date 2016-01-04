package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

// TODO Ensure that push does not push elements on top of each other
public final class Push extends ElementCollision {
    public static final ElementCollision INSTANCE = new Push();

    protected final ElementCollisionResult doDetermineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Push.INSTANCE) {
            return new Keep(element1.getElement(), element1.getDirection());
        }
        else if ((other == Bounce.INSTANCE) || (other == Eaten.INSTANCE) || (other == Stick.INSTANCE)) {
            if (element1.getDynamic()) {
                return new Move(
                        element1.getElement(),
                        element1.getDirection(),
                        new Field.Position(element1.getDirection().getDeltaX(), element1.getDirection().getDeltaY())
                );
            }
            else {
                return new Keep(element1.getElement(), element1.getDirection());
            }
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy(element1.getElement());
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementCollisionResult doDetermineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Push.INSTANCE) {
            return new Keep(element2.getElement(), element2.getDirection());
        }
        else if (other == Bounce.INSTANCE) {
            return new Move(
                    element2.getElement(),
                    element2.getDirection().reverse(),
                    new Field.Position(element1.getDirection().getDeltaX(), element1.getDirection().getDeltaY())
            );
        }
        else if (other == Eat.INSTANCE) {
            if (element2.getDynamic()) {
                return new Move(
                        element2.getElement(),
                        element2.getDirection(),
                        new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
                );
            }
            else {
                return new Keep(element2.getElement(), element2.getDirection());
            }
        }
        else if (other == Eaten.INSTANCE) {
            return new Destroy(element2.getElement());
        }
        else if (other == Stick.INSTANCE) {
            return new Move(
                    element2.getElement(),
                    element2.getDirection(),
                    new Field.Position(element1.getDirection().getDeltaX(), element1.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    private Push() {
    }
}