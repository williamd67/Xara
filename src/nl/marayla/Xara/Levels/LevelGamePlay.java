package nl.marayla.Xara.Levels;

import nl.marayla.Xara.ElementCollisions.CollisionResult;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.ElementCollisions.ElementCollision;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.GameElements.GameElement;

public interface LevelGamePlay {
    void renderElement(GameElement element, RenderData renderData, Field.ConstantPosition position);
    ElementCollision determineElementCollision(
        GameElement dynamicElement,
        GameElement staticElement
    );
    ElementEffect determineElementEffect(
        CollisionResult collisionResult,
        GameElement dynamicElement,
        GameElement staticElement
    );
}