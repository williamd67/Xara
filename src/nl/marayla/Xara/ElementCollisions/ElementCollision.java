package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public abstract class ElementCollision {
    public interface ElementResult {
        Field.ConstantPosition getRelativePosition();
        Field.ConstantDirection getNextDirection();
    }

    protected final class Destroy implements ElementResult {
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

    protected final class Keep extends ChangedDirection {
        public Keep(final Field.ConstantDirection nextDirection) {
            super(nextDirection);
        }

        @Contract(pure = true)
        public final Field.ConstantPosition getRelativePosition() {
            return Field.Position.ORIGIN;
        }
    }

    protected final class Move extends ChangedDirection {
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
        public CollisionResult(final ElementResult colliderResult, final ElementResult collideIntoResult) {
            this.colliderResult = colliderResult;
            this.collideIntoResult = collideIntoResult;
        }

        public final ElementResult getColliderResult() {
            return colliderResult;
        }

        public final ElementResult getCollideIntoResult() {
            return collideIntoResult;
        }

        private ElementResult colliderResult;
        private ElementResult collideIntoResult;
    }

    @Contract("_, _ -> !null")
    public final CollisionResult determineCollisionResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        return new CollisionResult(
                collider.getCollision().determineColliderResult(collider, collideInto),
                collider.getCollision().determineCollideIntoResult(collider, collideInto)
        );
    }

    protected final ElementResult determineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        try {
            return doDetermineColliderResult(collider, collideInto);
        }
        catch (UnsupportedOperationException e) {
            return doDetermineCollideIntoResult(collideInto, collider);
        }
    }

    protected final ElementResult determineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        try {
            return doDetermineCollideIntoResult(collider, collideInto);
        }
        catch (UnsupportedOperationException e) {
            return doDetermineColliderResult(collideInto, collider);
        }
    }

    protected abstract ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    );

    protected abstract ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    );

    public abstract ElementCollisionData.List handleCollision(
            final ElementCollisionData collider,
            final ElementCollisionData colliderInto
    );
}