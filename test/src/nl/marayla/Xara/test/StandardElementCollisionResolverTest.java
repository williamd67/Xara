package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.StandardElementCollisionResolver;
import org.jetbrains.annotations.Contract;

public class StandardElementCollisionResolverTest extends ElementCollisionResolverTest {
    @Contract("_ -> !null")
    @Override
    protected final ElementCollisionResolver createCollisionResolver(final int numberOfElements) {
        return new StandardElementCollisionResolver(numberOfElements);
    }
}