package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public abstract class ElementCollision {
    public interface ElementResult {
        Field.ConstantPosition getRelativePosition();
        Field.ConstantDirection getNextDirection();
    }

    public final class Destroy implements ElementResult {
        @Contract(pure = true)
        public final Field.ConstantPosition getRelativePosition() {
            return Field.Position.ORIGIN;
        }

        @Contract(pure = true)
        public final Field.ConstantDirection getNextDirection() {
            return Field.Direction.STATIC;
        }
    }

    private abstract class ChangedDirection implements ElementResult {
        public ChangedDirection(final Field.ConstantDirection nextDirection) {
            this.nextDirection = nextDirection;
        }

        public abstract Field.ConstantPosition getRelativePosition();

        public final Field.ConstantDirection getNextDirection() {
            return nextDirection;
        }

        private Field.ConstantDirection nextDirection;
    }

    public final class Keep extends ChangedDirection {
        public Keep(final Field.ConstantDirection nextDirection) {
            super(nextDirection);
        }

        @Contract(pure = true)
        public final Field.ConstantPosition getRelativePosition() {
            return Field.Position.ORIGIN;
        }
    }

    public final class Move extends ChangedDirection {
        public Move(final Field.ConstantDirection nextDirection, final Field.ConstantPosition relativePosition) {
            super(nextDirection);
            this.relativePosition = relativePosition;
        }

        public final Field.ConstantPosition getRelativePosition() {
            return relativePosition;
        }

        private Field.ConstantPosition relativePosition;
    }

    public final class CollisionResult {
        public CollisionResult(final ElementResult element1Result, final ElementResult element2Result) {
            this.element1Result = element1Result;
            this.element2Result = element2Result;
        }

        public final ElementResult getElement1Result() {
            return element1Result;
        }

        public final ElementResult getElement2Result() {
            return element2Result;
        }

        private ElementResult element1Result;
        private ElementResult element2Result;
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

    protected final ElementResult determineElement1Result(
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

    protected final ElementResult determineElement2Result(
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

    protected abstract ElementResult doDetermineElement1Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    );

    protected abstract ElementResult doDetermineElement2Result(
            final ElementCollisionData element1,
            final ElementCollisionData element2
    );
}