package nl.marayla.Xara.test;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;

public class FieldTest extends BaseFieldTest {
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
        public DetermineDirectionMethodDynamic(final Field.TopLinePosition topLinePosition) {
            this.topLinePosition = topLinePosition;
        }

        public final Field.Direction execute(final Field.ConstantPosition position) {
            switch(topLinePosition) {
                case TOP:
                case BOTTOM:
                    return Field.Direction.UP;
                case LEFT:
                case RIGHT:
                    return Field.Direction.LEFT;
                case NONE:
                    return Field.Direction.DOWN;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        private Field.TopLinePosition topLinePosition;
    }

    static class DetermineDirectionMethodAllDirections implements DetermineDirectionMethod {
        public final Field.Direction execute(final Field.ConstantPosition position) {
            return determineDirectionForAllDirections(position);
        }
    }

    private void setupExpectedValuesStatic(
        final Field.TopLinePosition topLinePosition,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), topLinePosition);

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
        final Field.TopLinePosition topLinePosition,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls,
        final Field.ConstantPosition initialValue
    ) {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), topLinePosition);

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
        final Field.TopLinePosition topLinePosition,
        final DetermineDirectionMethod determineDirectionMethod,
        final int numberOfRenderCalls,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), topLinePosition);

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
        final Field.TopLinePosition topLinePosition,
        final DetermineDirectionMethod determineDirectionMethod,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        setupExpectedValuesStatic(
            topLinePosition,
            determineDirectionMethod,
            NUMBER_OF_RENDER_CALLS,
            initialValue,
            addition
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private void doTestDynamic(
        final Field.TopLinePosition topLinePosition,
        final Field.ConstantPosition initialValue
    ) {
        setupExpectedValuesDynamic(
            topLinePosition,
            new DetermineDirectionMethodDynamic(topLinePosition),
            NUMBER_OF_RENDER_CALLS,
            initialValue
        );
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private void doTestAllDirections(
        final Field.TopLinePosition topLinePosition,
        final Field.ConstantPosition initialValue,
        final Field.ConstantPosition addition
    ) {
        setupExpectedValuesAllDirections(
            topLinePosition,
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
            Field.TopLinePosition.TOP,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testTopLinePositionDynamicNoMove() {
        doTestStatic(
            Field.TopLinePosition.TOP,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testBottomLinePositionStatic() {
        doTestStatic(
            Field.TopLinePosition.BOTTOM,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testBottomLinePositionDynamicNoMove() {
        doTestStatic(
            Field.TopLinePosition.BOTTOM,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testLeftLinePositionStatic() {
        doTestStatic(
            Field.TopLinePosition.LEFT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testLeftLinePositionDynamicNoMove() {
        doTestStatic(
            Field.TopLinePosition.LEFT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testRightLinePositionStatic() {
        doTestStatic(
            Field.TopLinePosition.RIGHT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }

    public final void testRightLinePositionDynamicNoMove() {
        doTestStatic(
            Field.TopLinePosition.RIGHT,
            new DetermineDirectionMethodStatic(),
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }

    public final void testNoneLinePositionStatic() {
        doTestStatic(
            Field.TopLinePosition.NONE,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            Field.Position.ORIGIN
        );
    }
    public final void testNoneLinePositionDynamicNoMove() {
        doTestStatic(
            Field.TopLinePosition.NONE,
            new DetermineDirectionMethodStatic(),
            Field.Position.ORIGIN,
            Field.Position.ORIGIN
        );
    }

    public final void testTopLinePositionDynamic() {
        doTestDynamic(Field.TopLinePosition.TOP, Field.Position.ORIGIN);
    }

    public final void testBottomLinePositionDynamic() {
        doTestDynamic(Field.TopLinePosition.BOTTOM, new Field.Position(0, FIELD_SIZE - 1));
    }

    public final void testLeftLinePositionDynamic() {
        doTestDynamic(Field.TopLinePosition.LEFT, new Field.Position(0, FIELD_SIZE - 1));
    }

    public final void testRightLinePositionDynamic() {
        doTestDynamic(Field.TopLinePosition.RIGHT, new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1));
    }

    public final void testNoneLinePositionDynamic() {
        doTestStatic(
            Field.TopLinePosition.NONE,
            new DetermineDirectionMethodDynamic(Field.TopLinePosition.NONE),
            Field.Position.ORIGIN,
            new Field.Position(0, 1)
        );
    }

    public final void testNoneLinePositionAllDirections() {
        doTestAllDirections(Field.TopLinePosition.NONE, Field.Position.ORIGIN, Field.Position.ORIGIN);
    }

    public final void testTopLinePositionAllDirections() {
        doTestAllDirections(Field.TopLinePosition.TOP, Field.Position.ORIGIN, new Field.Position(0, 1));
    }

    public final void testBottomLinePositionAllDirections() {
        doTestAllDirections(
            Field.TopLinePosition.BOTTOM,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(0, -1)
        );
    }

    public final void testLeftLinePositionAllDirections() {
        doTestAllDirections(
            Field.TopLinePosition.LEFT,
            new Field.Position(0, FIELD_SIZE - 1),
            new Field.Position(1, 0)
        );
    }

    public final void testRightLinePositionAllDirections() {
        doTestAllDirections(
            Field.TopLinePosition.RIGHT,
            new Field.Position(FIELD_SIZE - 1, FIELD_SIZE - 1),
            new Field.Position(-1, 0)
        );
    }
}