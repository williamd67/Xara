package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public interface ElementCollision {
    Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    );

    Field.ConstantDirection moveOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    );

    Field.ConstantDirection isMovedByOtherElementDueToCollision(
        final ElementCollisionData thisData,
        final ElementCollisionData otherData
    );
}