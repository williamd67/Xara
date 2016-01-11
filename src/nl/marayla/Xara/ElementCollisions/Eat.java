package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Eat extends ElementCollision {
    public static final ElementCollision INSTANCE = new Eat();

    /*
    other   -      placing    element1                      element2
                              index     element   direction index     element   direction
    static  BOUNCE one        move      element1  copy      -         -         -
            EAT    none       -         -         -         -         -         -
            EATEN  one        move      element1  copy      -         -         -
            PUSH   one        move      element1  copy      -         -         -
            STICK  one        move      element1  copy      -         -         -

    dynamic	BOUNCE one        move      element1  copy      -         -         -
            EAT    none       -         -         -         -         -         -
            EATEN  one        move      element1  copy      -         -         -
            PUSH   one        move      element1  copy      -         -         -
            STICK  one        move      element1  copy      -         -         -
    */
    @Override
    protected final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.getDynamic();

        final ElementCollision other = element2.getCollision();
        if (other == Eat.INSTANCE) {
            return new Field.PlacingNone();
        }
        else if ((other == Bounce.INSTANCE) ||
            (other == Eaten.INSTANCE) ||
            (other == Push.INSTANCE) ||
            (other == Stick.INSTANCE)) {
            return new Field.PlacingOne(
                Field.calculateIndex(element1.getIndex(), element1.getDirection()),
                element1.getElement(),
                element1.getDirection()
            );
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    protected final ElementCollisionResult doDetermineElement1Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Eat.INSTANCE) {
            return new Destroy(element1.getElement());
        }
        else if ((other == Bounce.INSTANCE) ||
            (other == Eaten.INSTANCE) ||
            (other == Push.INSTANCE) ||
            (other == Stick.INSTANCE)) {
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
        throw new UnsupportedOperationException();
    }

    protected final ElementCollisionResult doDetermineElement2Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.INSTANCE) ||
            (other == Eat.INSTANCE) ||
            (other == Eaten.INSTANCE) ||
            (other == Push.INSTANCE) ||
            (other == Stick.INSTANCE)) {
            return new Destroy(element2.getElement());
        }
        throw new UnsupportedOperationException();
    }

    private Eat() {
    }
}