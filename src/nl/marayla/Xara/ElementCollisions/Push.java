package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

// TODO Ensure that push does not push elements on top of each other
public final class Push extends StandardElementCollision {
    public static final ElementCollision INSTANCE = new Push();

    /*
    other   -       placing    element1                      element2
                               index     element   direction index     element   direction
    static  EAT	    one	      -         -         -         copy      element2	 copy
            EATEN   one        move      element1  copy	    -         -         -
            PUSH    both	      copy      element1  copy      copy      element2  copy
            NEUTRAL both       copy      element1  copy      copy      element2  copy

    dynamic EAT	    one        -         -         -         move      element2  copy
            EATEN   one        move      element1  copy      -         -         -
            PUSH    both       copy      element1  copy      copy      element2  copy
            NEUTRAL both       copy      element1  copy      copy      element2  copy
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

        final ElementCollision other = element2.getCollision();
        if ((other == Push.INSTANCE) || (other == Neutral.INSTANCE)) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Field.PlacingOne(
                element2.isColliding()
                    ? Field.calculateIndex(element2.getIndex(), element2.getDirection())
                    : element2.getIndex(),
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

    @Override
    public final Field.ConstantDirection moveOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    ) {
        assert thisData.isColliding();
        assert !otherData.isColliding();

        final ElementCollision other = otherData.getCollision();
        if ((other == Bounce.REVERSE) ||
            (other == Bounce.HORIZONTAL) ||
            (other == Bounce.VERTICAL) ||
            (other == Push.INSTANCE) ||
            (other == Neutral.INSTANCE)) {
            return thisData.getDirection();
        }
        else if ((other == Eat.INSTANCE) || (other == Eaten.INSTANCE)) {
            return Field.Direction.STATIC;
        }
        else {
            return other.isMovedByOtherElementDueToCollision(otherData, thisData);
        }
    }

    private Push() {
    }
}