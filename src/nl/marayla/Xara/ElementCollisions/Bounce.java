package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Bounce extends ElementCollision {
    public static final ElementCollision INSTANCE = new Bounce();

    /*
    other   -      placing    element1                      element2
                              index     element   direction index     element   direction
    static  BOUNCE both       copy      element1  reverse	copy      element2  copy
            EAT    one        -         -         -         copy      element2  copy
            EATEN  one        copy      element1  reverse   -         -         -
            PUSH   both       copy      element1  reverse   copy      element2  copy
            STICK  both       copy      element1  reverse   copy      element2  copy

    dynamic	BOUNCE both       copy      element1  reverse   copy      element2  reverse
            EAT    one        -         -         -         move      element2  copy
            EATEN  one        copy      element1  reverse   -         -         -
            PUSH   both       copy      element1  reverse   copy      element2  copy
            STICK  both       copy      element1  reverse   copy      element2  copy
    */
    @Override
    protected final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.getDynamic();

        final ElementCollision other = element2.getCollision();
        if (other == Bounce.INSTANCE) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection().reverse(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDynamic() ? element2.getDirection().reverse() : element2.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Field.PlacingOne(
                (
                    element2.getDynamic() ?
                        Field.calculateIndex(element2.getIndex(), element2.getDirection()) :
                        element2.getIndex()
                ),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else if (other == Eaten.INSTANCE) {
            return new Field.PlacingOne(element1.getIndex(), element1.getElement(), element1.getDirection().reverse());
        }
        else if ((other == Push.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection().reverse(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected final ElementCollisionResult doDetermineElement1Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.INSTANCE) || (other == Eaten.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Keep(element1.getElement(), element1.getDirection().reverse());
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy(element1.getElement());
        }
        else if (other == Push.INSTANCE) {
            return new Move(
                element1.getElement(),
                element1.getDirection().reverse(),
                new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected final ElementCollisionResult doDetermineElement2Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Bounce.INSTANCE) {
            return new Keep(element2.getElement(), element2.getDirection().reverse());
        }
        else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE)) {
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
            return new Keep(element2.getElement(), element2.getDirection());
        }
        throw new UnsupportedOperationException();
    }

    private Bounce() {
    }
}