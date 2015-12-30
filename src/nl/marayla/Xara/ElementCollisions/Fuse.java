package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;

/*
 *  FUSE: Element A and Element B fuses to a new Element C
 *      Element A is destroyed
 *      Element B is destroyed
 *      Element C is created and placed on position of Element B
 */
public abstract class Fuse extends ElementCollision {
    @Override
    public final ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();

        GameElement fusion = createFusionElement(collider.getElement(), collideInto.getElement());
        collideInto.setAction(Field.Action.REMOVE);
        list.add(collideInto);

        ElementCollisionData.List fusionList = addFusion(fusion, collider, collideInto);
        list.addAll(fusionList);
        ElementCollisionData.List.releaseInstance(fusionList);

        return list;
    }

    protected abstract GameElement createFusionElement(
            final GameElement dynamicElement,
            final GameElement staticElement
    );
    protected abstract ElementCollisionData.List addFusion(
            final GameElement fusion,
            final ElementCollisionData collider,
            final ElementCollisionData collideInto
    );
}