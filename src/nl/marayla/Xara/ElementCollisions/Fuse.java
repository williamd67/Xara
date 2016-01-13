package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;

public abstract class Fuse implements ElementCollision {
    protected abstract GameElement createFusionElement(final GameElement element1, final GameElement element2);

    /*
    FUSE dynamic
    other   -      placing    element1                      element2
                              index     element   direction index     element   direction
    static  BOUNCE one        move      fuse      copy      -         -         -
            EAT    one        -         -         -         copy      element2  copy
            EATEN  one        move      element1  copy      -         -         -
            PUSH   one        move      fuse      copy      -         -         -
            STICK  one        move      fuse      copy      -         -         -

    dynamic BOUNCE one        move      fuse      copy      -         -         -
            EAT    one        -         -         -         move      element2  copy
            EATEN  one        move      element1  copy      -         -         -
            PUSH   one        move      fuse      copy      -         -         -
            STICK  one        move      fuse      copy      -         -         -

    FUSE static
    dynamic BOUNCE one        copy      fuse      copy      -         -         -
            EAT    one        -         -         -         move      element2  copy
            EATEN  one        copy      element1  copy      -         -         -
            PUSH   one        copy      fuse      copy      -         -         -
            STICK  one        copy      fuse      copy      -         -         -
    */
    @Override
    public final Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if ((other == Bounce.INSTANCE) || (other == Push.INSTANCE) || (other == Stick.INSTANCE)) {
            return new Field.PlacingOne(
                element1.getDynamic()
                    ? Field.calculateIndex(element1.getIndex(), element1.getDirection())
                    : element1.getIndex(),
                createFusionElement(element1.getElement(), element2.getElement()),
                element1.getDirection()
            );
        }
        else if (other == Eat.INSTANCE) {
            return new Field.PlacingOne(
                element2.getDynamic()
                    ? Field.calculateIndex(element2.getIndex(), element2.getDirection())
                    : element2.getIndex(),
                element2.getElement(),
                element2.getDirection()
            );
        }
        else if (other == Eaten.INSTANCE) {
            return new Field.PlacingOne(
                element1.getDynamic()
                    ? Field.calculateIndex(element1.getIndex(), element1.getDirection())
                    : element1.getIndex(),
                element1.getElement(),
                element1.getDirection()
            );
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public final Field.ConstantDirection moveOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    ) {
        return Field.Direction.STATIC;
    }
}