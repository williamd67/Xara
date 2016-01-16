package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public final class Stick extends StandardElementCollision {
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
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert element1.isColliding();

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
                element2.isColliding() ? element2.getDirection().reverse() : element2.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Field.PlacingOne(
                (element2.isColliding() ?
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

    private Stick() {
    }
}