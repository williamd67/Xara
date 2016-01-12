package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.ElementCollisions.ElementCollision;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;
import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.GameElements.GameElement;

class MockElementEffect implements ElementEffect {
    public final void execute() {
    }
}

class MockLevelGamePlay implements LevelGamePlay {
    public MockLevelGamePlay(final ElementCollisionResolver collisionResolver) {
        this.collisionResolver = collisionResolver;
    }

    @Override
    public ElementCollision determineElementCollision(
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return collisionResolver.getElementElementCollision(dynamicElement, staticElement);
    }

    @Override
    public ElementEffect determineElementEffect(
        final Field.PlacingAfterCollision placing,
        final GameElement dynamicElement,
        final GameElement staticElement
    ) {
        return new MockElementEffect(); // TODO Implement properly
    }

    @Override
    public final void renderElement(
            final GameElement element,
            final RenderData renderData,
            final Field.ConstantPosition position
    ) {
        if (element instanceof ElementRenderer) {
            ((ElementRenderer) element).render(renderData, position);
        }
    }

    private ElementCollisionResolver collisionResolver;
}