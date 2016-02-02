package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;

public class ElementEffectTest extends BaseCollisionFieldTest {
    public final void testNoCollision() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.Direction.STATIC);
        MockElementRenderer element = new MockElementRenderer();
        addElement(element);

        Field.ConstantPosition initial = new Field.Position(5, 5);

        Field.Position position = new Field.Position(initial);
        for (int i = 0; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(position);
            position.set(position.getX(), position.getY() + 1);
        }
        Field.addMovingElement(element, initial, Field.Direction.DOWN);

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }
}