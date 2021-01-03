package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.Bounce;
import nl.marayla.Xara.ElementCollisions.Eat;
import nl.marayla.Xara.ElementCollisions.Eaten;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.Push;
import nl.marayla.Xara.ElementCollisions.StandardElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.Neutral;
import nl.marayla.Xara.GameElements.GameElement;

public abstract class BaseCollisionFieldTest extends BaseFieldTest {
    public static boolean isFuseElement(GameElement element) {
        return (
                (element.ordinal() == LevelElements.FUSE_STATIC.ordinal()) ||
                (element.ordinal() == LevelElements.FUSE_DYNAMIC.ordinal())
        );
    }

    @Override
    protected final ElementCollisionResolver setupElementCollisionResolver() {
        ElementCollisionResolver collisionResolver = new StandardElementCollisionResolver(
            LevelElements.values().length
        );
        collisionResolver.addDefaultCollision(Neutral.INSTANCE);
        collisionResolver.addElementCollision(Eat.INSTANCE, LevelElements.EAT);
        collisionResolver.addElementCollision(Eaten.INSTANCE, LevelElements.EATEN);
        collisionResolver.addElementCollision(Bounce.REVERSE, LevelElements.BOUNCE);
        collisionResolver.addElementCollision(Push.INSTANCE, LevelElements.PUSH);
        doSetupElementCollisionResolver(collisionResolver);
        return collisionResolver;
    }

    protected void doSetupElementCollisionResolver(
        final ElementCollisionResolver collisionResolver
    ) {
    }

    protected enum LevelElements implements GameElement {
        STATIC,
        EAT,
        EATEN,
        BOUNCE,
        PUSH,
        NEUTRAL,
        FUSE_STATIC,
        FUSE_DYNAMIC
    }
}