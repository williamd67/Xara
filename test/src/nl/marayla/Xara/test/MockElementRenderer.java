package nl.marayla.Xara.test;

import java.util.ArrayList;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.ElementRenderers.ElementRenderer;

import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

public class MockElementRenderer implements ElementRenderer, GameElement {
    public MockElementRenderer() {
    }

    public MockElementRenderer(final GameElement element) {
        this.element = element;
    }

    @Override
    public final void render(final RenderData renderData, final Field.ConstantPosition position) {
        actualRenderPositions.add(new Field.Position(position));
    }

    @Override
    public final int ordinal() {
        if (element == null) {
            return 0;
        }
        return element.ordinal();
    }

    public final void addExpectedRenderPosition(final Field.ConstantPosition position) {
        expectedRenderPositions.add(new Field.Position(position));
    }

    public final void verify() {
        TestCase.assertEquals(
            output(),
            expectedRenderPositions.size(),
            actualRenderPositions.size()
        );
        for (int i = 0; i < expectedRenderPositions.size(); i++) {
            TestCase.assertEquals(
                output(),
                expectedRenderPositions.get(i),
                actualRenderPositions.get(i)
            );
        }
    }

    @NotNull
    private String output() {
        StringBuilder result = new StringBuilder();
        result.append("Element expected ");
        for (Field.ConstantPosition position : expectedRenderPositions) {
            result.append(position.toString());
            result.append(" ");
        }
        result.append("actual ");
        for (Field.ConstantPosition position : actualRenderPositions) {
            result.append(position.toString());
            result.append(" ");
        }
        return result.toString();
    }

    private GameElement element;
    private final ArrayList<Field.ConstantPosition> expectedRenderPositions = new ArrayList<>();
    private final ArrayList<Field.ConstantPosition> actualRenderPositions = new ArrayList<>();
}