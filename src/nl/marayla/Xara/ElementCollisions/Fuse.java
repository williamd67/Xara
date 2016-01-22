package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

public abstract class Fuse extends StandardElementCollision {
    protected abstract GameElement createFusionElement(final GameElement element1, final GameElement element2);

    /*
    FUSE dynamic
    other   -       placing    element1                      element2
                               index     element   direction index     element   direction
    static  BOUNCE  one        move      fuse      copy      -         -         -
            EAT     one        -         -         -         copy      element2  copy
            EATEN   one        move      element1  copy      -         -         -
            PUSH    one        move      fuse      copy      -         -         -
            NEUTRAL one        move      fuse      copy      -         -         -

    dynamic BOUNCE  one        move      fuse      copy      -         -         -
            EAT     one        -         -         -         move      element2  copy
            EATEN   one        move      element1  copy      -         -         -
            PUSH    one        move      fuse      copy      -         -         -
            NEUTRAL one        move      fuse      copy      -         -         -

    FUSE static
    dynamic BOUNCE  one        copy      fuse      copy      -         -         -
            EAT     one        -         -         -         move      element2  copy
            EATEN   one        copy      element1  copy      -         -         -
            PUSH    one        copy      fuse      copy      -         -         -
            NEUTRAL one        copy      fuse      copy      -         -         -
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.REVERSE) ||
            (other == Bounce.HORIZONTAL) ||
            (other == Bounce.VERTICAL) ||
            (other == Push.INSTANCE) ||
            (other == Neutral.INSTANCE)) {
            return new Field.PlacingOne(
                element1.isColliding()
                    ? Field.calculateIndex(element1.getIndex(), element1.getDirection())
                    : element1.getIndex(),
                createFusionElement(element1.getElement(), element2.getElement()),
                element1.getDirection()
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
                element1.isColliding()
                    ? Field.calculateIndex(element1.getIndex(), element1.getDirection())
                    : element1.getIndex(),
                element1.getElement(),
                element1.getDirection()
            );
        }
        throw new UnsupportedOperationException();
    }
}