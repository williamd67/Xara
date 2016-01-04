package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

public abstract class ElementCollision {
    public interface ElementCollisionResult {
        GameElement getNextElement();
        Field.ConstantPosition getRelativePosition();
        Field.ConstantDirection getNextDirection();
    }

    private abstract class ElementCollisionResultWithNextElement implements ElementCollisionResult {
        public ElementCollisionResultWithNextElement(final GameElement nextElement) {
            this.nextElement = nextElement;
        }

        @Contract(pure = true)
        public final GameElement getNextElement() {
            return nextElement;
        }

        public abstract Field.ConstantPosition getRelativePosition();
        public abstract Field.ConstantDirection getNextDirection();

        private GameElement nextElement;
    }

    public final class Destroy extends ElementCollisionResultWithNextElement {
        public Destroy(final GameElement nextElement) {
            super(nextElement);
        }

        @Contract(pure = true)
        public final Field.ConstantPosition getRelativePosition() {
            return Field.Position.ORIGIN;
        }

        @Contract(pure = true)
        public final Field.ConstantDirection getNextDirection() {
            return Field.Direction.STATIC;
        }
    }

    private abstract class ChangedDirection extends ElementCollisionResultWithNextElement {
        public ChangedDirection(final GameElement nextElement, final Field.ConstantDirection nextDirection) {
            super(nextElement);
            this.nextDirection = nextDirection;
        }

        public abstract Field.ConstantPosition getRelativePosition();

        public final Field.ConstantDirection getNextDirection() {
            return nextDirection;
        }

        private Field.ConstantDirection nextDirection;
    }

    public final class Keep extends ChangedDirection {
        public Keep(final GameElement nextElement, final Field.ConstantDirection nextDirection) {
            super(nextElement, nextDirection);
        }

        @Contract(pure = true)
        public final Field.ConstantPosition getRelativePosition() {
            return Field.Position.ORIGIN;
        }
    }

    public final class Move extends ChangedDirection {
        public Move(
                final GameElement nextElement,
                final Field.ConstantDirection nextDirection,
                final Field.ConstantPosition relativePosition
        ) {
            super(nextElement, nextDirection);
            this.relativePosition = relativePosition;
        }

        public final Field.ConstantPosition getRelativePosition() {
            return relativePosition;
        }

        private Field.ConstantPosition relativePosition;
    }

    public final class CollisionResult {
        public CollisionResult(final ElementCollisionResult element1Result, final ElementCollisionResult element2Result) {
            this.element1Result = element1Result;
            this.element2Result = element2Result;
        }

        public final ElementCollisionResult getElement1Result() {
            return element1Result;
        }

        public final ElementCollisionResult getElement2Result() {
            return element2Result;
        }

        private ElementCollisionResult element1Result;
        private ElementCollisionResult element2Result;
    }

    @Contract("_, _ -> !null")
    public final CollisionResult determineCollisionResult(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        return new CollisionResult(
                element1.getCollision().determineElement1Result(element1, element2),
                element1.getCollision().determineElement2Result(element1, element2)
        );
    }

    protected final ElementCollisionResult determineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        try {
            return element1.getCollision().doDetermineElement1Result(element1, element2);
        }
        catch (UnsupportedOperationException e) {
            return element2.getCollision().doDetermineElement2Result(element2, element1);
        }
    }

    protected final ElementCollisionResult determineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    ) {
        try {
            return element1.getCollision().doDetermineElement2Result(element1, element2);
        }
        catch (UnsupportedOperationException e) {
            return element2.getCollision().doDetermineElement1Result(element2, element1);
        }
    }

    protected abstract ElementCollisionResult doDetermineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    );

    protected abstract ElementCollisionResult doDetermineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    );
}