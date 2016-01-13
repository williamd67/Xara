package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public final class Eaten implements ElementCollision {
    public static final ElementCollision INSTANCE = new Eaten();

    /*
    other   -      placing    element1                      element2
                              index     element   direction index     element   direction
    static  BOUNCE one        -         -         -         copy      element2  copy
            EAT    one        -         -         -         copy      element2  copy
            EATEN  none       -         -         -         -         -         -
            PUSH   one        -         -         -         copy      element2  copy
            STICK  one        -         -         -         copy      element2  copy

    dynamic BOUNCE one        -         -         -         copy      element2  reverse
            EAT    one        -         -         -         move      element2  copy
            EATEN  none       -         -         -         -         -         -
            PUSH   one        -         -         -         move      element2  copy
            STICK  one        -         -         -         move      element2  copy
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.getDynamic();

        final ElementCollision other = element2.getCollision();
        if (other == Eaten.INSTANCE) {
            return new Field.PlacingNone();
        }
        else if (other == Bounce.INSTANCE) {
            return new Field.PlacingOne(
                element2.getIndex(),
                element2.getElement(),
                element2.getDynamic() ? element2.getDirection().reverse() : element2.getDirection()
            );
        }
        else if ((other == Eat.INSTANCE) || (other == Push.INSTANCE) || (other == Stick.INSTANCE)) {
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
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public final Field.ConstantDirection moveOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    ) {
        return Field.Direction.STATIC;
    }

    @Override
    public final Field.ConstantDirection isMovedByOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    ) {
        throw new UnsupportedOperationException();
    }

    private Eaten() {
    }
}