package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public abstract class ElementCollision {
    public abstract Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    );
}