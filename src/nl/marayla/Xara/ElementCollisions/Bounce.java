package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public class Bounce extends StandardElementCollision {
    public static final Bounce REVERSE = new Bounce(Field.ConstantDirection::reverse);
    public static final Bounce HORIZONTAL = new Bounce(Field.ConstantDirection::reverseY);
    public static final Bounce VERTICAL = new Bounce(Field.ConstantDirection::reverseX);

    @FunctionalInterface
    private interface BounceDirection {
        Field.ConstantDirection get(final Field.ConstantDirection direction);
    }

    public final Field.ConstantDirection getBounceDirection(final Field.ConstantDirection direction) {
        return bounceDirection.get(direction);
    }

    /*
    BOUNCE dynamic
    other   -       placing    element1                      element2
                               index     element   direction index     element   direction
    static  BOUNCE  both       copy      element1  reverse	copy      element2  copy
            EAT     one        -         -         -         copy      element2  copy
            EATEN   one        copy      element1  reverse   -         -         -
            PUSH    both       copy      element1  reverse   copy      element2  copy
            NEUTRAL both       copy      element1  reverse   copy      element2  copy

    dynamic	BOUNCE  both       copy      element1  reverse   copy      element2  reverse
            EAT     one        -         -         -         move      element2  copy
            EATEN   one        copy      element1  reverse   -         -         -
            PUSH    both       copy      element1  reverse   copy      element2  copy
            NEUTRAL both       copy      element1  reverse   copy      element2  copy

    BOUNCE static
    dynamic	BOUNCE  both       copy      element1  copy      copy      element2  reverse
            EAT     one        -         -         -         move      element2  copy
            EATEN   one        copy      element1  copy      -         -         -
            PUSH    both       copy      element1  reverse   copy      element2  copy
            NEUTRAL both       copy      element1  reverse   copy      element2  copy
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.REVERSE) || (other == Bounce.HORIZONTAL) || (other == Bounce.VERTICAL)) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.isColliding() ? getBounceDirection(element1.getDirection()) : element1.getDirection(),
                element2.getIndex(),
                element2.getElement(),
                element2.isColliding()
                    ? ((Bounce) other).getBounceDirection(element2.getDirection())
                    : element2.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
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
        else if (other == Eaten.INSTANCE) {
            return new Field.PlacingOne(
                element1.getIndex(),
                element1.getElement(),
                element1.isColliding() ? getBounceDirection(element1.getDirection()) : element1.getDirection()
            );
        }
        else if ((other == Push.INSTANCE) || (other == Neutral.INSTANCE)) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.isColliding() ? getBounceDirection(element1.getDirection()) : element1.getDirection(),
                element2.getIndex(),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private Bounce(final BounceDirection bounceDirection) {
        this.bounceDirection = bounceDirection;
    }

    private BounceDirection bounceDirection;
}