package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.StandardElementCollisionResolver;

public class StandardElementCollisionResolverTest extends ElementCollisionResolverTest {
    @Override
    protected final ElementCollisionResolver createCollisionResolver(final int numberOfElements) {
        return new StandardElementCollisionResolver(numberOfElements);
    }
}