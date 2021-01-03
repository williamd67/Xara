package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Eaten extends StandardElementCollision {
    public static final ElementCollision INSTANCE = new Eaten();

    /*
    other   -       placing    element1                      element2
                               index     element   direction index     element   direction
    static  EAT     one        -         -         -         copy      element2  copy
            EATEN   none       -         -         -         -         -         -
            PUSH    one        -         -         -         copy      element2  copy
            NEUTRAL one        -         -         -         copy      element2  copy

    dynamic EAT     one        -         -         -         move      element2  copy
            EATEN   none       -         -         -         -         -         -
            PUSH    one        -         -         -         move      element2  copy
            NEUTRAL one        -         -         -         move      element2  copy
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

        final ElementCollision other = element2.getCollision();
        if (other == Eaten.INSTANCE) {
            return new Field.PlacingNone();
        }
        else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE) || (other == Neutral.INSTANCE)) {
            return new Field.PlacingOne(
                (
                    element2.isColliding() ?
                        Field.calculateIndex(element2.getIndex(), element2.getDirection()) :
                        element2.getIndex()
                ),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private Eaten() {
    }
}