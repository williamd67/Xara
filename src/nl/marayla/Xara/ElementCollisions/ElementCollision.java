package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;

public abstract class ElementCollision {
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