package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public abstract class StandardElementCollision implements ElementCollision {
    public abstract Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    );

    @Override
    public Field.ConstantDirection moveOtherElementDueToCollision(
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
}
