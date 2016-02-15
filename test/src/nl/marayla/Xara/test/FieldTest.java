package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

public class FieldTest extends BaseFieldTest {
    @Nullable
    @Contract(value = " -> null", pure = true)
    @Override
    protected final ElementCollisionResolver setupElementCollisionResolver() {
        return null;
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
        final DetermineDirectionMethod determineDirectionMethod,
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
                Field.addMovingElement(
                    element,
                    position,
                    determineDirectionMethod.execute(position)
                );

                for (int i = 0; i < numberOfRenderCalls; i++) {
                    if ((position.getX() < FIELD_SIZE) && (position.getY() < FIELD_SIZE)) {
                        // Do not expected render-position if outside visible field (in borders)
                        element.addExpectedRenderPosition(position);
                    }
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
        final int numberOfRenderCalls,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
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

                position.set(
                    Math.abs(initialValue.getX() - position.getX()),
                    Math.abs(initialValue.getY() - position.getY())
                );
                Field.ConstantDirection direction = determineDirectionForAllDirections(position);

                element.addExpectedRenderPosition(position);
                for (int i = 1; i < numberOfRenderCalls; i++) {
                    position.set(
                        position.getX() + direction.getDeltaX() + addition.getX(),
                        position.getY() + direction.getDeltaY() + addition.getY()
                    );
                    if (
                        (position.getX() >= 0)
                            && (position.getX() < FIELD_SIZE)
                            && (position.getY() >= 0)
                            && (position.getY() < FIELD_SIZE)
                        ) {
                        element.addExpectedRenderPosition(position);
                    }
                }
            }
        }
    }

    private void doTestStatic(
        final Field.ConstantDirection fieldDirection,
        final DetermineDirectionMethod determineDirectionMethod
    ) {
        setupExpectedValuesStatic(fieldDirection, determineDirectionMethod, NUMBER_OF_RENDER_CALLS);
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
        final Field.ConstantDirection fieldDirection,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        setupExpectedValuesAllDirections(
            fieldDirection,
            new DetermineDirectionMethodAllDirections(),
            NUMBER_OF_RENDER_CALLS,
            initialValue,
            addition
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    @Test
    public final void testTopLinePositionStatic() {
        doTestStatic(
            Field.Direction.DOWN,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testTopLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.DOWN,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testBottomLinePositionStatic() {
        doTestStatic(
            Field.Direction.UP,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testBottomLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.UP,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testLeftLinePositionStatic() {
        doTestStatic(
            Field.Direction.RIGHT,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testLeftLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.RIGHT,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testRightLinePositionStatic() {
        doTestStatic(
            Field.Direction.LEFT,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testRightLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.LEFT,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testNoneLinePositionStatic() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testNoneLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodStatic()
        );
    }

    @Test
    public final void testTopLinePositionDynamic() {
        doTestDynamic(Field.Direction.DOWN);
    }

    @Test
    public final void testBottomLinePositionDynamic() {
        doTestDynamic(Field.Direction.UP);
    }

    @Test
    public final void testLeftLinePositionDynamic() {
        doTestDynamic(Field.Direction.RIGHT);
    }

    @Test
    public final void testRightLinePositionDynamic() {
        doTestDynamic(Field.Direction.LEFT);
    }

    @Test
    public final void testNoneLinePositionDynamic() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodDynamic(Field.Direction.STATIC)
        );
    }

    @Test
    public final void testNoneLinePositionAllDirections() {
        doTestAllDirections(Field.Direction.STATIC, Field.Position.ORIGIN, Field.Position.ORIGIN);
    }

    @Test
    public final void testTopLinePositionAllDirections() {
        doTestAllDirections(Field.Direction.DOWN, Field.Position.ORIGIN, new Field.Position(0, 1));
    }

    @Test
    public final void testBottomLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.UP,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    @Test
    public final void testLeftLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.RIGHT,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    @Test
    public final void testRightLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.LEFT,
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }
}