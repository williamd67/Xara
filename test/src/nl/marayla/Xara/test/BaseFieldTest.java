package nl.marayla.Xara.test;

import java.util.ArrayList;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Renderer.Rectangle;
import nl.marayla.Xara.Renderer.RenderData;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;

import org.junit.Before;

public abstract class BaseFieldTest {
    @Before
    public final void setUp() {
        levelGamePlay = new MockLevelGamePlay(setupElementCollisionResolver());
        renderData = new RenderData(
            null, // Canvas is not used
            new Rectangle(0, 0, FIELD_SIZE, FIELD_SIZE)
        );
        elements = new ArrayList<>();
    }

    protected final void render(final int numberOfRenderCalls) {
        Field.render(levelGamePlay, renderData);
        for (int i = 1; i < numberOfRenderCalls; i++) {
            Field.nextFrame(levelGamePlay);
            Field.render(levelGamePlay, renderData);
        }
    }

    protected final void verify() {
        if (elements != null) {
            for (MockElementRenderer element : elements) {
                element.verify();
            }
        }
    }

    protected final MockLevelGamePlay getLevelGamePlay() {
        return levelGamePlay;
    }

    protected final void addElement(final MockElementRenderer element) {
        elements.add(element);
    }

    protected abstract ElementCollisionResolver setupElementCollisionResolver();
    private MockLevelGamePlay levelGamePlay;
    private RenderData renderData;
    private ArrayList<MockElementRenderer> elements;

    protected static final int FIELD_SIZE = 15;
    protected static final int NUMBER_OF_RENDER_CALLS = 5;
}