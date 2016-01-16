package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.ElementCollisionData;
import nl.marayla.Xara.ElementCollisions.Fuse;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

class MockFuse extends Fuse {
    @Contract("_, _ -> !null")
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