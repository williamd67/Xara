package nl.marayla.Xara.test;

import nl.marayla.Xara.ElementCollisions.Eat;
import nl.marayla.Xara.ElementCollisions.StandardElementCollisionResolver;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class FieldTest extends BaseFieldTest {
    @Override
    protected final ElementCollisionResolver setupElementCollisionResolver() {
        ElementCollisionResolver collisionResolver = new StandardElementCollisionResolver(
            LevelElements.values().length
        );
        collisionResolver.addElementCollision(Eat.INSTANCE, LevelElements.EAT);
        return collisionResolver;
    }

    public static Field.ConstantDirection determineDirectionForAllDirections(
        final Field.ConstantPosition position
    ) {
        final int middleFieldSize = FIELD_SIZE / 2;
        final int positionX = position.getX();
        final int positionY = position.getY();
        /**
         * End-goal of directions is:
         *  -1,-1    0,-1    0,-1   0,-1    0,-1    0,-1    1,-1
         *  -1, 0   -1,-1    0,-1   0,-1    0,-1    1,-1    1, 0
         *  -1, 0   -1, 0   -1,-1   0,-1    1,-1    1, 0    1, 0
         *  -1, 0   -1, 0   -1, 0   0, 0    1, 0    1, 0    1, 0
         *  -1, 0   -1, 0   -1, 1   0, 1    1, 1    1, 0    1, 0
         *  -1, 0   -1, 1    0, 1   0, 1    0, 1    1, 1    1, 0
         *  -1, 1    0, 1    0, 1   0, 1    0, 1    0, 1    1, 1
         */
        if (positionX < middleFieldSize) {
            if (positionY < middleFieldSize) {
                if (positionX < positionY) {
                    return Field.Direction.LEFT;
                }
                else if (positionX == positionY) {
                    return Field.Direction.LEFT_UP;
                }
                else {
                    return Field.Direction.UP;
                }
            }
            else if (positionY == middleFieldSize) {
                return Field.Direction.LEFT;
            }
            else {
                int reverseY = FIELD_SIZE - positionY - 1;
                if (positionX < reverseY) {
                    return Field.Direction.LEFT;
                }
                else if (positionX == reverseY) {
                    return Field.Direction.LEFT_DOWN;
                }
                else {
                    return Field.Direction.DOWN;
                }
            }
        }
        else if (positionX == middleFieldSize) {
            if (positionY < middleFieldSize) {
                return Field.Direction.UP;
            }
            else if (positionY == middleFieldSize) {
                return Field.Direction.STATIC;
            }
            else {
                return Field.Direction.DOWN;
            }
        }
        else {
            if (positionY < middleFieldSize) {
                int reverseX = FIELD_SIZE - positionX - 1;
                if (reverseX < positionY) {
                    return Field.Direction.RIGHT;
                }
                else if (reverseX == positionY) {
                    return Field.Direction.RIGHT_UP;
                }
                else {
                    return Field.Direction.UP;
                }
            }
            else if (positionY == middleFieldSize) {
                return Field.Direction.RIGHT;
            }
            else {
                if (positionX < positionY) {
                    return Field.Direction.DOWN;
                }
                else if (positionX == positionY) {
                    return Field.Direction.RIGHT_DOWN;
                }
                else {
                    return Field.Direction.RIGHT;
                }
            }
        }
    }

    interface DetermineDirectionMethod {
        Field.ConstantDirection execute(Field.ConstantPosition position);
    }

    static class DetermineDirectionMethodStatic implements DetermineDirectionMethod {
        @Override
        public Field.ConstantDirection execute(final Field.ConstantPosition position) {
            return Field.Direction.STATIC;
        }
    }

    static class DetermineDirectionMethodDynamic implements DetermineDirectionMethod {
        public DetermineDirectionMethodDynamic(final Field.ConstantDirection fieldDirection) {
            this.fieldDirection = fieldDirection;
        }

        @Override
        public final Field.ConstantDirection execute(final Field.ConstantPosition position) {
            return fieldDirection.reverse();
        }

        private Field.ConstantDirection fieldDirection;
    }

    static class DetermineDirectionMethodAllDirections implements DetermineDirectionMethod {
        public final Field.ConstantDirection execute(final Field.ConstantPosition position) {
            return determineDirectionForAllDirections(position);
        }
    }

    private void setupExpectedValuesStatic(
        final Field.ConstantDirection fieldDirection,
        final int numberOfRenderCalls
    ) {
        final int FULL_SIZE = FIELD_SIZE + 2;

        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), fieldDirection);

        Field.Position position = new Field.Position(Field.Position.ORIGIN);
        for (int y = 0; y < FIELD_SIZE; y += 2) {
            for (int x = 0; x < FIELD_SIZE; x += 2) {
                position.set(x, y);
                MockElementRenderer element = new MockElementRenderer();
                addElement(element);
                Field.addStaticElement(element, position);

                for (int i = 0; i < numberOfRenderCalls; i++) {
                    if ((position.getX() >= FIELD_SIZE) || (position.getY() >= FIELD_SIZE)) {
                        // Do not add expected render-positions if outside visible field
                        break;
                    }

                    element.addExpectedRenderPosition(position);
                    position.add(fieldDirection);
                    if (position.getX() < 0) {
                        position.set(position.getX() + FULL_SIZE, position.getY());
                    }
                    if (position.getY() < 0) {
                        position.set(position.getX(), position.getY() + FULL_SIZE);
                    }
                    if (position.getX() >= FULL_SIZE) {
                        position.set(position.getX() - FULL_SIZE, position.getY());
                    }
                    if (position.getY() >= FULL_SIZE) {
                        position.set(position.getX(), position.getY() - FULL_SIZE);
                    }
                }
            }
        }
    }

    private void setupExpectedValuesDynamic(
        final Field.ConstantDirection fieldDirection,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls
    ) {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), fieldDirection);

        Field.Position position = new Field.Position(Field.Position.ORIGIN);
        for (int y = 0; y < FIELD_SIZE; y += 2) {
            for (int x = 0; x < FIELD_SIZE; x += 2) {
                position.set(x, y);
                MockElementRenderer element = new MockElementRenderer();
                addElement(element);
                Field.addMovingElement(
                    element,
                    position,
                    determineDirectionMethod.execute(position)
                );

                for (int i = 0; i < numberOfRenderCalls; i++) {
                    element.addExpectedRenderPosition(position);
                }
            }
        }
    }

    private void setupExpectedValuesAllDirections(
        final Field.ConstantDirection fieldDirection,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls
    ) {
        final int FULL_SIZE = FIELD_SIZE + 2;

        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), fieldDirection);
        // Four corners
        Field.addMovingElement(LevelElements.EAT, new Field.Position(-1, -1), fieldDirection.reverse());
        Field.addMovingElement(LevelElements.EAT, new Field.Position(-1, FIELD_SIZE), fieldDirection.reverse());
        Field.addMovingElement(LevelElements.EAT, new Field.Position(FIELD_SIZE, -1), fieldDirection.reverse());
        Field.addMovingElement(LevelElements.EAT, new Field.Position(FIELD_SIZE, FIELD_SIZE), fieldDirection.reverse());

        // Four sides
        for (int column = -1; column <= FIELD_SIZE; column++) {
            Field.addMovingElement(LevelElements.EAT, new Field.Position(column, -1), fieldDirection.reverse());
            Field.addMovingElement(LevelElements.EAT, new Field.Position(column, FIELD_SIZE), fieldDirection.reverse());
        }
        for (int row = -1; row <= FIELD_SIZE; row++) {
            Field.addMovingElement(LevelElements.EAT, new Field.Position(-1, row), fieldDirection.reverse());
            Field.addMovingElement(LevelElements.EAT, new Field.Position(FIELD_SIZE, row), fieldDirection.reverse());
        }

        Field.Position position = new Field.Position(Field.Position.ORIGIN);
        for (int y = 0; y < FIELD_SIZE; y += 2) {
            for (int x = 0; x < FIELD_SIZE; x += 2) {
                position.set(x, y);
                final Field.ConstantDirection direction = determineDirectionMethod.execute(position);

                MockElementRenderer element = new MockElementRenderer();
                addElement(element);
                Field.addMovingElement(element, position, direction);

                for (int i = 0; i < numberOfRenderCalls; i++) {
                    if ((position.getX() >= FIELD_SIZE) || (position.getY() >= FIELD_SIZE)) {
                        // Do not add expected render-position if outside visible field (in borders)
                        break;
                    }
                    element.addExpectedRenderPosition(position);
                    position.add(fieldDirection);
                    position.add(direction);
                    if (position.getX() < 0) {
                        position.set(position.getX() + FULL_SIZE, position.getY());
                    }
                    if (position.getY() < 0) {
                        position.set(position.getX(), position.getY() + FULL_SIZE);
                    }
                    if (position.getX() >= FULL_SIZE) {
                        position.set(position.getX() - FULL_SIZE, position.getY());
                    }
                    if (position.getY() >= FULL_SIZE) {
                        position.set(position.getX(), position.getY() - FULL_SIZE);
                    }
                }
            }
        }
    }

    private void doTestStatic(final Field.ConstantDirection fieldDirection) {
        setupExpectedValuesStatic(fieldDirection, NUMBER_OF_RENDER_CALLS);
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private void doTestDynamic(
        final Field.ConstantDirection fieldDirection
    ) {
        setupExpectedValuesDynamic(
            fieldDirection,
            new DetermineDirectionMethodDynamic(fieldDirection),
            NUMBER_OF_RENDER_CALLS
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private void doTestAllDirections(
        final Field.ConstantDirection fieldDirection
    ) {
        setupExpectedValuesAllDirections(
            fieldDirection,
            new DetermineDirectionMethodAllDirections(),
            NUMBER_OF_RENDER_CALLS
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    @Test
    public final void testFieldDirectionDownNoMove() {
        doTestStatic(Field.Direction.DOWN);
    }

    @Test
    public final void testFieldDirectionUpNoMove() {
        doTestStatic(Field.Direction.UP);
    }

    @Test
    public final void testFieldDirectionRightNoMove() {
        doTestStatic(Field.Direction.RIGHT);
    }

    @Test
    public final void testFieldDirectionLeftNoMove() {
        doTestStatic(Field.Direction.LEFT);
    }

    @Test
    public final void testFieldDirectionStaticNoMove() {
        doTestStatic(Field.Direction.STATIC);
    }

    @Test
    public final void testFieldDirectionDownMove() {
        doTestDynamic(Field.Direction.DOWN);
    }

    @Test
    public final void testFieldDirectionUpMove() {
        doTestDynamic(Field.Direction.UP);
    }

    @Test
    public final void testFieldDirectionRightMove() {
        doTestDynamic(Field.Direction.RIGHT);
    }

    @Test
    public final void testFieldDirectionLeftMove() {
        doTestDynamic(Field.Direction.LEFT);
    }

    @Test
    public final void testFieldDirectionStaticMove() {
        doTestStatic(Field.Direction.STATIC);
    }

    @Test
    public final void testFieldDirectionStaticMoveAllDirections() {
        doTestAllDirections(Field.Direction.STATIC);
    }

    @Test
    public final void testFieldDirectionDownMoveAllDirections() {
        doTestAllDirections(Field.Direction.DOWN);
    }

    @Test
    public final void testFieldDirectionUpMoveAllDirections() {
        doTestAllDirections(Field.Direction.UP);
    }

    @Test
    public final void testFieldDirectionRightMoveAllDirections() {
        doTestAllDirections(Field.Direction.RIGHT);
    }

    @Test
    public final void testFieldDirectionLeftMoveAllDirections() {
        doTestAllDirections(Field.Direction.LEFT);
    }

    protected enum LevelElements implements GameElement {
        STATIC,
        EAT
    }

}