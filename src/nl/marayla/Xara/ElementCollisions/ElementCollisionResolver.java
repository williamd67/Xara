package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.GameElements.GameElement;

//ElementCollisionResolver has to be filled from generic to more specific:
//so first addDefaultCollision(collision)
//then addElementCollision(collision, element)
//and last addElementElementCollision(collision, element, element)
//This ensures that default-values will not be changed
public abstract class ElementCollisionResolver {
    @SuppressWarnings("serial")
    public static class InvalidOrderException extends RuntimeException { }

    public static final ElementCollision DEFAULT = Stick.INSTANCE;

    public ElementCollisionResolver(final int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public abstract ElementCollision getElementElementCollision(
        GameElement element1,
        GameElement element2
    );

    public final void addDefaultCollision(final ElementCollision elementCollision) {
        if (addState.ordinal() > AddState.DEFAULT.ordinal()) {
            throw new InvalidOrderException();
        }
        doAddDefaultCollision(elementCollision);
    }

    public final void addElementCollision(
        final ElementCollision elementCollision,
        final GameElement element
    ) {
        if (addState.ordinal() > AddState.ELEMENT.ordinal()) {
            throw new InvalidOrderException();
        }
        addState = AddState.ELEMENT;
        doAddElementCollision(elementCollision, element);
    }

    public final void addElementElementCollision(
        final ElementCollision elementCollision,
        final GameElement element1,
        final GameElement element2
    ) {
        addState = AddState.ELEMENT_ELEMENT;
        doAddElementElementCollision(elementCollision, element1, element2);

    }
    protected abstract void doAddDefaultCollision(ElementCollision elementCollision);
    protected abstract void doAddElementCollision(
        ElementCollision elementCollision,
        GameElement element
    );
    protected abstract void doAddElementElementCollision(
        ElementCollision elementCollision,
        GameElement element1,
        GameElement element2
    );
    protected final int getNumberOfElements() {
        return numberOfElements;
    }

    private enum AddState { DEFAULT, ELEMENT, ELEMENT_ELEMENT }
    private AddState addState = AddState.DEFAULT;
    private int numberOfElements;
}