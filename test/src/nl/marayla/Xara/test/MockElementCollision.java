package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.*;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

abstract class MockFuse extends Fuse {
    public MockFuse(final SetupFusionElement setupFusionElement) {
        this.setupFusionElement = setupFusionElement;
    }
    @Override
    protected final GameElement createFusionElement(
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return new MockElementRenderer(dynamicElement);
    }
    @Override
    protected final ElementCollisionData.List addFusion(
        final GameElement fusion,
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        setupFusionElement.execute(fusion, collider, collideInto);
        return doAddFusion(fusion, collider, collideInto);
    }
    protected abstract ElementCollisionData.List doAddFusion(
        final GameElement fusion,
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    );

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (
                (collision == Bounce.INSTANCE) ||
                (collision == Eat.INSTANCE) ||
                (collision == Eaten.INSTANCE) ||
                (collision == Push.INSTANCE) ||
                (collision == Stick.INSTANCE)
        );
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return false;
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return false;
    }

    protected final ElementResult doDetermineColliderResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        throw new UnsupportedOperationException();
    }

    protected final ElementResult doDetermineCollideIntoResult(
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    ) {
        throw new UnsupportedOperationException();
    }

    private SetupFusionElement setupFusionElement;
}
class MockStaticFuse extends MockFuse {
    public MockStaticFuse(final SetupFusionElement setupFusionElement) {
        super(setupFusionElement);
    }
    @Override
    protected final ElementCollisionData.List doAddFusion(
        final GameElement fusion,
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();
        ElementCollisionData data = ElementCollisionData.createInstance(
                Field.Action.ADD,
                collideInto.getIndex(),
                fusion,
                Field.Direction.STATIC,
                null
        );
        list.add(data);
        return list;
    }
}
class MockDynamicFuse extends MockFuse {
    public MockDynamicFuse(final SetupFusionElement setupFusionElement) {
        super(setupFusionElement);
    }
    @Override
    protected final ElementCollisionData.List doAddFusion(
        final GameElement fusion,
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();
        ElementCollisionData data = ElementCollisionData.createInstance(
                Field.Action.ADD,
                collideInto.getIndex(),
                fusion,
                collider.getDirection(),
                null
        );
        list.add(data);
        return list;
    }
}