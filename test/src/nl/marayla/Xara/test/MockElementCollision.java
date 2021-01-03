package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.Fuse;
import nl.marayla.Xara.GameElements.GameElement;

class MockFuse extends Fuse {
    @Override
    protected final GameElement createFusionElement(final GameElement element1, final GameElement element2) {
        if (BaseCollisionFieldTest.isFuseElement(element1)) {
            return element1;
        }
        else if (BaseCollisionFieldTest.isFuseElement(element2)) {
            return element2;
        }
        throw new UnsupportedOperationException();
    }
}