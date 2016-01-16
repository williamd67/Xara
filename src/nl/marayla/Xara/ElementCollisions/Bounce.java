package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public final class Bounce extends StandardElementCollision {
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
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

        final ElementCollision other = element2.getCollision();
        if (other == Bounce.INSTANCE) {
            return new Field.PlacingBoth(
                element1.getIndex(),
                element1.getElement(),
                element1.getDirection().reverse(),
                element2.getIndex(),
                element2.getElement(),
                element2.isColliding() ? element2.getDirection().reverse() : element2.getDirection()
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

    private Bounce() {
    }
}