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
    public final void testNoneBorderCleanup() {
        initializeField(Field.Direction.STATIC);
        doTestBorderCleanup(0, FIELD_SIZE - 1);
    }

    @Test
    public final void testTopBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.DOWN);
    }

    @Test
    public final void testBottomBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.UP);
    }

    @Test
    public final void testLeftBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.RIGHT);
    }

    @Test
    public final void testRightBorderCleanup() {
        doTestDynamicBorderCleanup(Field.Direction.LEFT);
    }

    private void initializeField(final Field.ConstantDirection fieldDirection) {
        Field.initialize(new Field.Size(FIELD_SIZE - 2, FIELD_SIZE - 2), fieldDirection);
    }

    private void doTestDynamicBorderCleanup(final Field.ConstantDirection fieldDirection) {
        initializeField(fieldDirection);
        doTestBorderCleanup(FIELD_SIZE - 1, FIELD_SIZE - 2);
    }

    private void doTestBorderCleanup(final int firstRow, final int secondRow) {
       int lineSize = FIELD_SIZE;
       fillRow(firstRow);
       fillRow(secondRow);
       fillColumn(0);
       fillColumn(FIELD_SIZE - 1);
       Field.nextFrame(levelGamePlay);
       for (int cellIndex = 0; cellIndex < (lineSize * lineSize); cellIndex++) {
           assertEquals(
               "cell [" + cellIndex % lineSize + ", " + cellIndex / lineSize + "] is not empty",
               null,
               Field.getElement(cellIndex)
           );
       }
    }

    // First and last-column will be skipped (will be filled later by fillColumn)
    private void fillRow(final int row) {
        for (int column = 1; column < (FIELD_SIZE - 1); column++) {
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