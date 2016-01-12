package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;

public abstract class Fuse extends ElementCollision {
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
    protected final Field.PlacingAfterCollision determinePlacing(
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

    protected final ElementCollisionResult doDetermineElement1Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Eat.INSTANCE) {
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
        else if (
            (other == Bounce.INSTANCE) ||
                (other == Push.INSTANCE) ||
                (other == Stick.INSTANCE)
            ) {
            if (element1.getDynamic()) {
                return new Move(
                    createFusionElement(element1.getElement(), element2.getElement()),
                    element1.getDirection(),
                    new Field.Position(element1.getDirection().getDeltaX(), element1.getDirection().getDeltaY())
                );
            }
            else {
                return new Destroy(element1.getElement());
            }
        }
        throw new UnsupportedOperationException();
    }

    protected final ElementCollisionResult doDetermineElement2Result(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        final ElementCollision other = element2.getCollision();
        if (other == Eat.INSTANCE) {
            return new Move(
                element2.getElement(),
                element2.getDirection(),
                new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
            );
        }
        else if (other == Eaten.INSTANCE) {
            return new Destroy(element2.getElement());
        }
        else if (
            (other == Bounce.INSTANCE) ||
                (other == Push.INSTANCE) ||
                (other == Stick.INSTANCE)
            ) {
            if (element2.getDynamic()) {
                return new Move(
                    createFusionElement(element1.getElement(), element2.getElement()),
                    (element1.getDirection() != Field.Direction.STATIC)
                        ? element1.getDirection()
                        : element2.getDirection(),
                    new Field.Position(element2.getDirection().getDeltaX(), element2.getDirection().getDeltaY())
                );
            }
            else {
                return new Destroy(element2.getElement());
            }
        }
        throw new UnsupportedOperationException();
    }
}