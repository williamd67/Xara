package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.GameElements.GameElement;

/*
 *  FUSE: Element A and Element B fuses to a new Element C
 *      Element A is destroyed
 *      Element B is destroyed
 *      Element C is created and placed on position of Element B
 */
public abstract class Fuse extends ElementCollision {

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