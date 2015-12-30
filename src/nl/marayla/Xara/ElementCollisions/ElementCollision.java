package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public abstract class ElementCollision {
    protected interface ElementResult {
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

    public final int determineNextStaticCellIndex(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        try {
            return doDetermineNextStaticCellIndex(collider, collideInto);
        }
        catch (UnsupportedOperationException e) {
            return doDetermineNextStaticCellIndex(collideInto, collider);
        }
    }

    public abstract ElementCollisionData.List handleCollision(
            final ElementCollisionData collider,
            final ElementCollisionData colliderInto
    );

/*
    public final ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData colliderInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();

        final ElementCollision other = colliderInto.getCollision();
        try {
            doHandleCollision(list, collider, colliderInto);
        }
        catch (UnsupportedOperationException e) {
            doHandleCollision(list, colliderInto, collider);
        }

        return list;
    }
*/
    protected abstract boolean staticKeep(final ElementCollision collision);
    protected abstract boolean staticMoveStaticDirection(final ElementCollision collision);
    protected abstract boolean staticMoveDynamicDirection(final ElementCollision collision);
/*
    protected abstract boolean dynamicKeep(final ElementCollision collision);
*/
    private int doDetermineNextStaticCellIndex(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (staticKeep(other)) {
            return collideInto.getIndex();
        }
        else if (staticMoveStaticDirection(other)) {
            return Field.calculateIndex(collideInto.getIndex(), collideInto.getDirection());
        }
        else if (staticMoveDynamicDirection(other)) {
            return Field.calculateIndex(collideInto.getIndex(), collider.getDirection());
        }
        throw new UnsupportedOperationException();
    }
/*
    private void doHandleCollision(
            final ElementCollisionData.List list,
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        final ElementCollision other = collideInto.getCollision();
        if (dynamicKeep(other)) {
            executeDynamicKeep(list, collider);
        }
        else if (dynamicDestroy(other)) {
            executeDynamicDestroy(list, collider);
        }
        else if (dynamicMoveStaticDirection(other)) {
            executeMoveDirection(list, collider, colliderInto.getDirection());
        }
        else if (dynamicMoveDynamicDirection(other)) {
            executeMoveDirection(list, collider, collider.getDirection());
        }
        throw new UnsupportedOperationException();
    }

    private void executeDynamicKeep(final ElementCollisionData.List list, final ElementCollisionData collisionData) {
        collisionData.setAction(Field.Action.ADD);
        list.add(collisionData);
    }

    private void executeDynamicDestroy(final ElementCollisionData.List list, final ElementCollisionData collisionData) {
        // nothing to do - dynamic is already removed
    }

    private void executeDynamicMoveStaticDirection(
            final ElementCollisionData.List list,
            final ElementCollisionData collisionData
    ) {
     <<TODO>>
    }
    */
}