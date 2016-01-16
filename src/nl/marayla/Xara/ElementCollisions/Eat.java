package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public final class Eat extends StandardElementCollision {
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
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

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

    private Eat() {
    }
}