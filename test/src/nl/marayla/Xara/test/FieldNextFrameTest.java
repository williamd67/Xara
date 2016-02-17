package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldNextFrameTest {
    @Before
    public final void setUp() {
        levelGamePlay = new MockLevelGamePlay(null);
    }

    @Test
    public final void testFieldDirectionStaticBorderCleanup() {
        initializeField(Field.Direction.STATIC);
        // Four corners
        Field.addStaticElement(LevelElements.BLOCK, new Field.Position(-1, -1));
        Field.addStaticElement(LevelElements.BLOCK, new Field.Position(-1, FIELD_SIZE));
        Field.addStaticElement(LevelElements.BLOCK, new Field.Position(FIELD_SIZE, -1));
        Field.addStaticElement(LevelElements.BLOCK, new Field.Position(FIELD_SIZE, FIELD_SIZE));

        // Four sides
        fillRow(-1);
        fillRow(FIELD_SIZE);
        fillColumn(-1);
        fillColumn(FIELD_SIZE);

        Field.nextFrame(levelGamePlay);
        Field.nextFrame(levelGamePlay);
        Field.nextFrame(levelGamePlay);

        final int FULL_SIZE = FIELD_SIZE + 2;
        for (int x = 0; x < FULL_SIZE; x++) {
            assertEquals(
                "cell [" + x + "] is empty",
                LevelElements.BLOCK,
                Field.getElement(x)
            );
        }
        for (int x = 0; x < FULL_SIZE; x++) {
            assertEquals(
                "cell [" + (x + (FULL_SIZE - 1) * FULL_SIZE) + "] is empty",
                LevelElements.BLOCK,
                Field.getElement(x + (FULL_SIZE - 1) * FULL_SIZE)
            );
        }
        for (int y = 0; y < FULL_SIZE; y++) {
            assertEquals(
                "cell [" + (y * FULL_SIZE) + "] is empty",
                LevelElements.BLOCK,
                Field.getElement(y * FULL_SIZE)
            );
        }
        for (int y = 0; y < FULL_SIZE; y++) {
            assertEquals(
                "cell [" + ((FULL_SIZE - 1) + (y * FULL_SIZE)) + "] is empty",
                LevelElements.BLOCK,
                Field.getElement((FULL_SIZE - 1) + y * FULL_SIZE)
            );
        }
        for (int y = 1; y < FIELD_SIZE; y++) {
            for (int x = 1; x < FIELD_SIZE; x++) {
                assertEquals(
                    "cell [" + (x + (y * FULL_SIZE)) + "] is empty",
                    null,
                    Field.getElement(x + y * FULL_SIZE)
                );
            }
        }
    }

    @Test
    public final void testFieldDirectionDownBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.DOWN);
    }

    @Test
    public final void testFieldDirectionUpBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.UP);
    }

    @Test
    public final void testFieldDirectionRightBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.RIGHT);
    }

    @Test
    public final void testFieldDirectionLeftBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.LEFT);
    }

    private void initializeField(final Field.ConstantDirection fieldDirection) {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), fieldDirection);
    }

    private void doTestDynamicBorderCleanup(final Field.ConstantDirection fieldDirection) {
        initializeField(fieldDirection);
        if (fieldDirection == Field.Direction.DOWN) {
            fillRow(FIELD_SIZE - 1);
        }
        else if (fieldDirection == Field.Direction.UP) {
            fillRow(0);
        }
        else if (fieldDirection == Field.Direction.LEFT) {
            fillColumn(0);
        }
        else if (fieldDirection == Field.Direction.RIGHT) {
            fillColumn(FIELD_SIZE - 1);
        }

        Field.nextFrame(levelGamePlay);
        Field.nextFrame(levelGamePlay);
        final int FULL_SIZE = FIELD_SIZE + 2;
        for (int cellIndex = 0; cellIndex < (FULL_SIZE * FULL_SIZE); cellIndex++) {
            assertEquals(
                "cell [" + cellIndex % FULL_SIZE + ", " + cellIndex / FULL_SIZE + "] is not empty",
                null,
                Field.getElement(cellIndex)
            );
        }
    }

    private void fillRow(final int row) {
        for (int column = 0; column < FIELD_SIZE; column++) {
            Field.addStaticElement(LevelElements.BLOCK, new Field.Position(column, row));
        }
    }

    private void fillColumn(final int column) {
        for (int row = 0; row < FIELD_SIZE; row++) {
            Field.addStaticElement(LevelElements.BLOCK, new Field.Position(column, row));
        }
    }

    private enum LevelElements implements GameElement {
        BLOCK
    }

    private MockLevelGamePlay levelGamePlay;
    private static final int FIELD_SIZE = 9;
}