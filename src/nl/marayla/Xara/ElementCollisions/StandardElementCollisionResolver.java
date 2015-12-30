package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.GameElements.GameElement;

public class StandardElementCollisionResolver extends ElementCollisionResolver {
    public StandardElementCollisionResolver(final int numberOfElements) {
        super(numberOfElements);
    }

    @Override
    public final ElementCollision getElementElementCollision(
        final GameElement element1,
        final GameElement element2
    ) {
        if (resolvers != null) {
            final StandardElementCollisionResolver resolver = resolvers[element1.ordinal()];
            if (resolver != null) {
                return resolver.getElementCollision(element2);
            }
        }
        return defaultCollision;
    }

    @Override
    protected final void doAddDefaultCollision(final ElementCollision elementCollision) {
        defaultCollision = elementCollision;
    }

    @Override
    protected final void doAddElementCollision(
        final ElementCollision elementCollision,
        final GameElement element
    ) {
        // Set all results for all element-pair (element, x)
        int elementIndex = element.ordinal();
        if (elementCollision != defaultCollision) {
            if (resolvers == null) {
                resolvers = new StandardElementCollisionResolver[getNumberOfElements()];
            }
            StandardElementCollisionResolver resolver = (
                new StandardElementCollisionResolver(getNumberOfElements())
            );
            resolver.addDefaultCollision(elementCollision);
            resolvers[elementIndex] = resolver;
        }
        else {
            // if elementCollision equals defaultCollision, earlier set collisions can be reset
            if (resolvers != null) {
                resolvers[elementIndex] = null;
            }
        }
    }

    @Override
    protected final void doAddElementElementCollision(
        final ElementCollision elementCollision,
        final GameElement element1,
        final GameElement element2
    ) {
        // Set result for element-pair (element1, element2)
        final int ordinal = element1.ordinal();
        if ((resolvers == null) || (resolvers[ordinal] == null)) {
            if (elementCollision != defaultCollision) {
                if (resolvers == null) {
                    resolvers = new StandardElementCollisionResolver[getNumberOfElements()];
                }
                StandardElementCollisionResolver resolver = (
                    new StandardElementCollisionResolver(getNumberOfElements())
                );
                resolver.addDefaultCollision(defaultCollision);
                resolver.addElementCollision(elementCollision, element2);
                resolvers[ordinal] = resolver;
            }
        }
        else {
            assert (resolvers[ordinal] != null);
            resolvers[ordinal].addElementCollision(elementCollision, element2);
        }
    }

    private ElementCollision getDefaultCollision() {
        return defaultCollision;
    }

    private ElementCollision getElementCollision(final GameElement element) {
        if (resolvers != null) {
            final StandardElementCollisionResolver resolver = resolvers[element.ordinal()];
            if (resolver != null) {
                return resolver.getDefaultCollision();
            }
        }
        return defaultCollision;
    }

    private ElementCollision defaultCollision = ElementCollisionResolver.DEFAULT;
    private StandardElementCollisionResolver[] resolvers;
}