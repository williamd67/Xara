package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class FieldTest extends BaseFieldTest {
    @Nullable
    @Contract(value = " -> null", pure = true)
    @Override
    protected final ElementCollisionResolver setupElementCollisionResolver() {
        return null;
    }

    public static Field.Direction determineDirectionForAllDirections(
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
        Field.Direction execute(Field.ConstantPosition position);
    }

    static class DetermineDirectionMethodStatic implements DetermineDirectionMethod {
        public Field.Direction execute(final Field.ConstantPosition position) {
            return Field.Direction.STATIC;
        }
    }

    static class DetermineDirectionMethodDynamic implements DetermineDirectionMethod {
        public DetermineDirectionMethodDynamic(final Field.ConstantDirection fieldDirection) {
            this.fieldDirection = fieldDirection;
        }

        public final Field.Direction execute(final Field.ConstantPosition position) {
            if (fieldDirection.getDeltaY() != 0) {
                return Field.Direction.UP;
            }
            else if (fieldDirection.getDeltaX() != 0) {
                return Field.Direction.LEFT;
            }
            else {
                return Field.Direction.DOWN;
            }
        }

        private Field.ConstantDirection fieldDirection;
    }

    static class DetermineDirectionMethodAllDirections implements DetermineDirectionMethod {
        public final Field.Direction execute(final Field.ConstantPosition position) {
            return determineDirectionForAllDirections(position);
        }
    }

    private void setupExpectedValuesStatic(
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

                Field.Position positionToAdd = new Field.Position(
                        Math.abs(initialValue.getX() - position.getX()),
                        Math.abs(initialValue.getY() - position.getY())
                );
                element.addExpectedRenderPosition(positionToAdd);
                for (int i = 1; i < numberOfRenderCalls; i++) {
                    position.set(
                            position.getX() + Math.abs(addition.getX()),
                            position.getY() + Math.abs(addition.getY())
                    );
                    if ((position.getY() < FIELD_SIZE) && (position.getX() < FIELD_SIZE)) {
                        positionToAdd.set(
                                positionToAdd.getX() + addition.getX(),
                                positionToAdd.getY() + addition.getY()
                        );
                        element.addExpectedRenderPosition(positionToAdd);
                    }
                }
            }
        }
    }

    private void setupExpectedValuesDynamic(
        final Field.ConstantDirection fieldDirection,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls,
        final Field.ConstantPosition initialValue
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
                Field.Direction direction = determineDirectionForAllDirections(position);

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
        final DetermineDirectionMethod determineDirectionMethod,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        setupExpectedValuesStatic(
            fieldDirection,
            determineDirectionMethod,
            NUMBER_OF_RENDER_CALLS,
            initialValue,
            addition
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private void doTestDynamic(
        final Field.ConstantDirection fieldDirection,
        final Field.ConstantPosition initialValue
    ) {
        setupExpectedValuesDynamic(
            fieldDirection,
            new DetermineDirectionMethodDynamic(fieldDirection),
            NUMBER_OF_RENDER_CALLS,
            initialValue
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

    public final void testTopLinePositionStatic() {
        doTestStatic(
            Field.Direction.DOWN,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testTopLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.DOWN,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testBottomLinePositionStatic() {
        doTestStatic(
            Field.Direction.UP,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testBottomLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.UP,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testLeftLinePositionStatic() {
        doTestStatic(
            Field.Direction.RIGHT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testLeftLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.RIGHT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testRightLinePositionStatic() {
        doTestStatic(
            Field.Direction.LEFT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }

    public final void testRightLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.LEFT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }

    public final void testNoneLinePositionStatic() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            Field.Position.ORIGIN
        );
    }
    public final void testNoneLinePositionDynamicNoMove() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            Field.Position.ORIGIN
        );
    }

    public final void testTopLinePositionDynamic() {
        doTestDynamic(Field.Direction.DOWN, Field.Position.ORIGIN);
    }

    public final void testBottomLinePositionDynamic() {
        doTestDynamic(Field.Direction.UP, new Field.Position(0, FIELD_SIZE - 1));
    }

    public final void testLeftLinePositionDynamic() {
        doTestDynamic(Field.Direction.RIGHT, new Field.Position(0, FIELD_SIZE - 1));
    }

    public final void testRightLinePositionDynamic() {
        doTestDynamic(Field.Direction.LEFT, new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1));
    }

    public final void testNoneLinePositionDynamic() {
        doTestStatic(
            Field.Direction.STATIC,
            new DetermineDirectionMethodDynamic(Field.Direction.STATIC),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testNoneLinePositionAllDirections() {
        doTestAllDirections(Field.Direction.STATIC, Field.Position.ORIGIN, Field.Position.ORIGIN);
    }

    public final void testTopLinePositionAllDirections() {
        doTestAllDirections(Field.Direction.DOWN, Field.Position.ORIGIN, new Field.Position(0, 1));
    }

    public final void testBottomLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.UP,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testLeftLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.RIGHT,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testRightLinePositionAllDirections() {
        doTestAllDirections(
            Field.Direction.LEFT,
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }
}