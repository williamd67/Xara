package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Stick extends ElementCollision {
    public static final ElementCollision INSTANCE = new Stick();

    /*
    other   -      placing    element1                      element2
                              index     element   direction index     element   direction
    static  BOUNCE both       copy      element1  copy      copy      element2  copy
            EAT    one        -         -         -         copy      element2  copy
            EATEN  one        move      element1  copy      -         -         -
            PUSH   both       copy      element1  copy      copy      element2  copy
            STICK  both       copy      element1  copy      copy      element2  copy

    dynamic BOUNCE both       copy      element1  copy      copy      element2  reverse
            EAT    one        -         -         -         move      element2  copy
            EATEN  one        move      element1  copy      -         -         -
            PUSH   both       copy      element1  copy      copy      element2  copy
            STICK  both       copy      element1  copy      copy      element2  copy
    */
    @Override
    protected final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.getDynamic();

        final ElementCollision other = element2.getCollision();
        if ((other == Stick.INSTANCE) || (other == Push.INSTANCE)) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else if (other == Bounce.INSTANCE) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDynamic() ? element2.getDirection().reverse() : element2.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Field.PlacingOne(
                (element2.getDynamic() ?
                    Field.calculateIndex(element2.getIndex(), element2.getDirection()) :
                    element2.getIndex()
                ),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else if (other == Eaten.INSTANCE) {
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
        if ((other == Bounce.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Keep(element1.getElement(), element1.getDirection());
        }
        else if (other == Eat.INSTANCE) {
            return new Destroy(element1.getElement());
        }
        else if (other == Eaten.INSTANCE) {
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
        else if (other == Push.INSTANCE) {
            return new Move(
                element1.getElement(),
                element1.getDirection(),
                new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
            );
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementCollisionResult doDetermineElement2Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Stick.INSTANCE) {
            return new Keep(element2.getElement(), element2.getDirection());
        }
        else if (other == Bounce.INSTANCE) {
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
        throw new UnsupportedOperationException();
    }

    private Stick() {
    }
}