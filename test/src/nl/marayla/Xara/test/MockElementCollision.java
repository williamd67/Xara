package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.*;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;

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