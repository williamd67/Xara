package nl.marayla.Xara.test;

import java.util.Random;

import nl.marayla.Xara.ElementCollisions.ElementCollisionData;
import nl.marayla.Xara.Field;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.Field.Direction;
import nl.marayla.Xara.GameElements.GameElement;

interface SetupFusionElement {
    void execute(GameElement fusion, ElementCollisionData collider, ElementCollisionData collideInto);
}

public class ElementCollisionTest extends BaseCollisionFieldTest implements SetupFusionElement {
    private void addExpectedRenderPositionsStatic(
        final MockElementRenderer element,
        final Field.ConstantPosition position,
        final int numberOfPositions
    ) {
        for (int i = 0; i < numberOfPositions; i++) {
            element.addExpectedRenderPosition(position);
        }
    }

    private Field.ConstantPosition addExpectedRenderPositionsDynamic(
        final MockElementRenderer element,
        final Field.ConstantPosition position,
        final Field.ConstantDirection direction,
        final int numberOfPositions
    ) {
        Field.Position current = new Field.Position(position);
        element.addExpectedRenderPosition(current);
        for (int i = 1; i < numberOfPositions; i++) {
            current.set(current.getX() + direction.getDeltaX(), current.getY() + direction.getDeltaY());
            element.addExpectedRenderPosition(current);
        }
        return current;
    }

    private void addExpectedRenderPositionsStatic(
        final LevelElements collisionType,
        final MockElementRenderer element,
        final Field.ConstantPosition position,
        final Field.ConstantDirection direction,
        final int numberOfRenderCalls,
        final int numberOfRenderCallsBeforeCollision
    ) {
        int numberOfPositions;
        switch(collisionType) {
            case EATEN:
            case BOUNCE:
            case STICK:
                numberOfPositions = numberOfRenderCalls;
                break;
            case EAT:
            case PUSH:
            case FUSE_STATIC:
            case FUSE_DYNAMIC:
                numberOfPositions = numberOfRenderCallsBeforeCollision;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        addExpectedRenderPositionsStatic(element, position, numberOfPositions);
        if (collisionType == LevelElements.PUSH) {
            addExpectedRenderPositionsDynamic(
                element,
                new Field.Position(
                    position.getX() + direction.getDeltaX(),
                    position.getY() + direction.getDeltaY()
                ),
                direction,
                numberOfRenderCalls - numberOfRenderCallsBeforeCollision
            );
        }
    }

    private void addExpectedRenderPositionsDynamic(
        final LevelElements collisionType,
        final MockElementRenderer element,
        final Field.ConstantPosition position,
        final Field.ConstantDirection direction,
        final int numberOfRenderCalls,
        final int numberOfRenderCallsBeforeCollision
    ) {
        int numberOfDynamicPositions;
        switch(collisionType) {
            case EAT:
            case PUSH:
                numberOfDynamicPositions = numberOfRenderCalls;
                break;
            case EATEN:
            case BOUNCE:
            case STICK:
            case FUSE_STATIC:
            case FUSE_DYNAMIC:
                numberOfDynamicPositions = numberOfRenderCallsBeforeCollision;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        Field.ConstantPosition current = addExpectedRenderPositionsDynamic(
            element,
            position,
            direction,
            numberOfDynamicPositions
        );
        if (collisionType == LevelElements.BOUNCE) {
            addExpectedRenderPositionsDynamic(
                element,
                current,
                direction.reverse(),
                numberOfRenderCalls - numberOfRenderCallsBeforeCollision
            );
        }
        else if (collisionType == LevelElements.STICK) {
            addExpectedRenderPositionsStatic(
                element,
                current,
                numberOfRenderCalls - numberOfRenderCallsBeforeCollision
            );
        }
    }

    private void setupStaticElement(
        final Field.ConstantPosition position,
        final LevelElements collisionType,
        final Field.ConstantDirection direction,
        final int numberOfRenderCalls,
        final int numberOfRenderCallsBeforeCollision
    ) {
        MockElementRenderer element = new MockElementRenderer();
        addElement(element);
        addExpectedRenderPositionsStatic(
            collisionType,
            element,
            position,
            direction,
            numberOfRenderCalls,
            numberOfRenderCallsBeforeCollision
        );
        Field.addStaticElement(element, position);
    }

    private void setupDynamicElement(
        final Field.ConstantPosition position,
        final LevelElements collisionType,
        final Field.ConstantDirection direction,
        final int numberOfRenderCalls,
        final int numberOfRenderCallsBeforeCollision
    ) {
        Field.Position start = new Field.Position(position);
        start.set(
                start.getX() - (numberOfRenderCallsBeforeCollision * direction.getDeltaX()),
                start.getY() - (numberOfRenderCallsBeforeCollision * direction.getDeltaY())
        );
        MockElementRenderer element = new MockElementRenderer(collisionType);
        addElement(element);
        addExpectedRenderPositionsDynamic(
            collisionType,
            element,
            start,
            direction,
            numberOfRenderCalls,
            numberOfRenderCallsBeforeCollision
        );
        Field.addDynamicElement(element, start, direction);
    }

    private void setupGameElements(final LevelElements collisionType) {
        final int numberOfCallsAfterCollision = (NUMBER_OF_RENDER_CALLS - NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION);
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);
        for (Field.Direction direction : Field.Direction.values()) {
            if (direction == Field.Direction.STATIC) {
                continue;
            }
            int offsetX = direction.getDeltaX() * (numberOfCallsAfterCollision + 1);
            int offsetY = direction.getDeltaY() * (numberOfCallsAfterCollision + 1);
            Field.Position start = new Field.Position(INITIAL_POINT.getX() - offsetX, INITIAL_POINT.getY() - offsetY);
            setupStaticElement(
                start,
                collisionType,
                direction,
                NUMBER_OF_RENDER_CALLS,
                NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
            );
            setupDynamicElement(
                start,
                collisionType,
                direction,
                NUMBER_OF_RENDER_CALLS,
                NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
            );
        }
    }

    private void doTestCollision(final LevelElements collisionType) {
        setupGameElements(collisionType);
        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    @Override
    protected final void doSetupElementCollisionResolver(final ElementCollisionResolver collisionResolver) {
        collisionResolver.addElementCollision(new MockStaticFuse(this), LevelElements.FUSE_STATIC);
        collisionResolver.addElementCollision(
            new MockDynamicFuse(this),
            LevelElements.FUSE_DYNAMIC
        );
    }

    @Override
    public final void execute(
        final GameElement fusion,
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        if (fusion instanceof MockElementRenderer) {
            MockElementRenderer element = (MockElementRenderer) fusion;
            addElement(element);
            int numberOfPositions = NUMBER_OF_RENDER_CALLS - NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION;
            Field.Position position = new Field.Position(
                    (collideInto.getIndex() % (FIELD_SIZE + 2)) - 1,
                    (collideInto.getIndex() / (FIELD_SIZE + 2)) - 1
            );
            for (int i = 0; i < numberOfPositions; i++) {
                if (element.ordinal() == LevelElements.FUSE_STATIC.ordinal()) {
                    element.addExpectedRenderPosition(position);
                }
                else if (element.ordinal() == LevelElements.FUSE_DYNAMIC.ordinal()) {
                    element.addExpectedRenderPosition(position);
                    position.set(
                            position.getX() + collider.getDirection().getDeltaX(),
                            position.getY() + collider.getDirection().getDeltaY()
                    );
                }
            }
        }
    }

    public final void testNoCollision() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        MockElementRenderer element = new MockElementRenderer();
        addElement(element);

        Field.ConstantDirection direction = Direction.DOWN;

        Field.Position position = new Field.Position(INITIAL_POINT);
        for (int i = 0; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(position);
            position.set(position.getX() + direction.getDeltaX(), position.getY() + direction.getDeltaY());
        }
        Field.addDynamicElement(element, INITIAL_POINT, direction);

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    public final void testEatCollision() {
        doTestCollision(LevelElements.EAT);
    }

    public final void testEatenCollision() {
        doTestCollision(LevelElements.EATEN);
    }

    public final void testBounceCollision() {
        doTestCollision(LevelElements.BOUNCE);
    }

    public final void testPushCollision() {
        doTestCollision(LevelElements.PUSH);
    }

    public final void testStickCollision() {
        doTestCollision(LevelElements.STICK);
    }

    public final void testFuseStaticCollision() {
        doTestCollision(LevelElements.FUSE_STATIC);
    }

    public final void testFuseDynamicCollision() {
        doTestCollision(LevelElements.FUSE_DYNAMIC);
    }

    public final void testPushCollisionIntoOther() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);
        Field.ConstantDirection direction = Field.Direction.RIGHT;

        // static that will be pushed
        MockElementRenderer element = new MockElementRenderer();
        addElement(element);

        for (int i = 0; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(INITIAL_POINT);
        }
        Field.addStaticElement(element, INITIAL_POINT);

        // static that will block pushing
        Field.ConstantPosition staticPosition = new Field.Position(
                INITIAL_POINT.getX() + direction.getDeltaX(),
                INITIAL_POINT.getY() + direction.getDeltaY()
        );

        element = new MockElementRenderer();
        addElement(element);

        for (int i = 0; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(staticPosition);
        }
        Field.addStaticElement(element, staticPosition);

        // dynamic that will cause collision
        Field.Position start = new Field.Position(
                INITIAL_POINT.getX() - (direction.getDeltaX() * NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION),
                INITIAL_POINT.getY() - (direction.getDeltaY() * NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION)
        );

        element = new MockElementRenderer(LevelElements.PUSH);
        addElement(element);

        Field.Position dynamicPosition = new Field.Position(start);
        for (int i = 0; i < (NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION - 1); i++) {
            element.addExpectedRenderPosition(dynamicPosition);
            dynamicPosition.set(
                    dynamicPosition.getX() + direction.getDeltaX(),
                    dynamicPosition.getY() + direction.getDeltaY()
            );
        }
        for (
            int i = (NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION - 1);
            i < NUMBER_OF_RENDER_CALLS;
            i++
        ) {
            element.addExpectedRenderPosition(dynamicPosition);
        }
        Field.addDynamicElement(element, start, direction);

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private MockElementRenderer setupStaticElement(
        final Field.ConstantPosition initial,
        final int numberOfPositions
    ) {
        MockElementRenderer element = new MockElementRenderer();
        addElement(element);
        for (int i = 0; i < numberOfPositions; i++) {
            element.addExpectedRenderPosition(initial);
        }
        Field.addStaticElement(element, initial);
        return element;
    }

    private MockElementRenderer setupDynamicElement(
        final Field.ConstantPosition initial,
        final Field.ConstantDirection direction,
        final GameElement gameElement,
        final int numberOfPositions
    ) {
        Field.ConstantPosition start = new Field.Position(
                initial.getX() - (direction.getDeltaX() * NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION),
                initial.getY() - (direction.getDeltaY() * NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION)
        );

        MockElementRenderer element = new MockElementRenderer(gameElement);
        addElement(element);

        Field.Position position = new Field.Position(start);
        for (int i = 0; i < numberOfPositions; i++) {
            element.addExpectedRenderPosition(position);
            position.set(position.getX() + direction.getDeltaX(), position.getY() + direction.getDeltaY());
        }
        Field.addDynamicElement(element, start, direction);
        return element;
    }

    /**
     * testDynamicElementCircle
     * Just before collision it will look like this,
     *  so that next frame they all seem to collide but they do not as all move at once
     *              LEFT_DOWN
     *      RIGHT   UP(INITIAL_POINT)
     */
    public final void testDynamicElementCircle() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        // dynamic with Field.Direction.RIGHT
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX() - 1, INITIAL_POINT.getY()),
            Field.Direction.RIGHT,
            LevelElements.EAT,
            NUMBER_OF_RENDER_CALLS
        );

        // dynamic with Field.Direction.UP
        setupDynamicElement(
            INITIAL_POINT,
            Field.Direction.UP,
            LevelElements.EAT,
            NUMBER_OF_RENDER_CALLS
        );

        // dynamic with Field.Direction.LEFT_DOWN
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX(), INITIAL_POINT.getY() - 1),
            Field.Direction.LEFT_DOWN,
            LevelElements.EAT,
            NUMBER_OF_RENDER_CALLS
        );

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * testDynamicDynamicStaticCollisionWhereSecondIsEaten
     * Just before collision it will look like this,
     *  so that next frame RIGHT will be eaten and LEFT_DOWN will continue
     *              LEFT_DOWN
     *      RIGHT   STATIC(INITIAL_POINT)
     */
    public final void testDynamicDynamicStaticCollisionWhereSecondIsEaten() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        // dynamic with Field.Direction.RIGHT
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX() - 1, INITIAL_POINT.getY()),
            Field.Direction.RIGHT,
            LevelElements.EATEN,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION + 1
        );

        // static
        setupStaticElement(INITIAL_POINT, NUMBER_OF_RENDER_CALLS);

        // dynamic with Field.Direction.LEFT_DOWN
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX(), INITIAL_POINT.getY() - 1),
            Field.Direction.LEFT_DOWN,
            LevelElements.EAT,
            NUMBER_OF_RENDER_CALLS
        );

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * testDynamicDynamicStaticCollisionWhereSecondStopsMoving
     * Just before collision it will look like this,
     *  so that next frame RIGHT will stick to static and LEFT_DOWN will be eaten
     *              LEFT_DOWN
     *      RIGHT   STATIC(INITIAL_POINT)
     */
    public final void testDynamicDynamicStaticCollisionWhereSecondStopsMoving() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        // dynamic with Field.Direction.RIGHT
        Field.Position initial = new Field.Position(INITIAL_POINT.getX() - 1, INITIAL_POINT.getY());
        MockElementRenderer element = setupDynamicElement(
            initial,
            Field.Direction.RIGHT,
            LevelElements.STICK,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );
        for (int i = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(initial);
        }

        // static
        setupStaticElement(INITIAL_POINT, NUMBER_OF_RENDER_CALLS);

        // dynamic with Field.Direction.LEFT_DOWN
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX(), INITIAL_POINT.getY() - 1),
            Field.Direction.LEFT_DOWN,
            LevelElements.EATEN,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION + 1
        );

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * testDynamicDynamicStaticCollisionWhereAllStopsMoving
     * Just before collision it will look like this,
     *  so that next frame RIGHT will stick to static and LEFT_DOWN will stick to RIGHT
     *              LEFT_DOWN
     *      RIGHT   STATIC(INITIAL_POINT)
     */
    public final void testDynamicDynamicStaticCollisionWhereAllStopsMoving() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        // dynamic with Field.Direction.RIGHT
        Field.Position initial = new Field.Position(INITIAL_POINT.getX() - 1, INITIAL_POINT.getY());
        MockElementRenderer element = setupDynamicElement(
            initial,
            Field.Direction.RIGHT,
            LevelElements.STICK,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );
        for (int i = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(initial);
        }

        // static
        setupStaticElement(INITIAL_POINT, NUMBER_OF_RENDER_CALLS);

        // dynamic with Field.Direction.LEFT_DOWN
        initial.set(INITIAL_POINT.getX(), INITIAL_POINT.getY() - 1);
        element = setupDynamicElement(
            initial,
            Field.Direction.LEFT_DOWN,
            LevelElements.STICK,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );
        for (int i = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(initial);
        }

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * testStaticPushedIntoDynamic
     * Just before collision it will look like this,
     *  so that next frame RIGHT will push static into UP,
     *  but as UP moves up it should have no influence
     *      RIGHT   STATIC(INITIAL_POINT)   UP
     */
    public final void testStaticPushedIntoDynamic() {
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        // dynamic with Field.Direction.RIGHT
        Field.ConstantDirection direction = Direction.RIGHT;
        Field.Position initial = new Field.Position(INITIAL_POINT.getX() - 1, INITIAL_POINT.getY());
        setupDynamicElement(initial, direction, LevelElements.PUSH, NUMBER_OF_RENDER_CALLS);

        // static
        MockElementRenderer element = setupStaticElement(
            INITIAL_POINT,
            NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );
        initial.set(INITIAL_POINT);
        for (int i = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION; i < NUMBER_OF_RENDER_CALLS; i++) {
            element.addExpectedRenderPosition(initial);
            initial.set(initial.getX() + direction.getDeltaX(), initial.getY() + direction.getDeltaY());
        }

        // dynamic with Field.Direction.UP
        setupDynamicElement(
            new Field.Position(INITIAL_POINT.getX() + 1, INITIAL_POINT.getY()),
            Field.Direction.UP,
            LevelElements.EAT,
            NUMBER_OF_RENDER_CALLS
        );

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * doTestDynamicDynamicCollision
     */
    public final void testDynamicDynamicCollisionEat() {
        final int numberOfCallsAfterCollision = (
            NUMBER_OF_RENDER_CALLS - NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );

        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        Field.Direction [] directions = { Direction.LEFT, Direction.RIGHT };
        for (Field.Direction direction : directions) { // Field.Direction.values()) {
            if (direction == Field.Direction.STATIC) {
                continue;
            }

            int offsetX = direction.getDeltaX() * (numberOfCallsAfterCollision + 1);
            int offsetY = direction.getDeltaY() * (numberOfCallsAfterCollision + 1);
            Field.Position start = new Field.Position(
                INITIAL_POINT.getX() - offsetX,
                INITIAL_POINT.getY() - offsetY
            );
            LevelElements inside;
            int insideNumberOfPositions;
            LevelElements outside;
            int outsideNumberOfPositions;
            switch (direction) {
                case LEFT:
                case LEFT_UP:
                case UP:
                case RIGHT_UP:
                    do {
                        // everything except EAT, FUSE_STATIC and FUSE_DYNAMIC // TODO: remove after proper implementation of FUSE_xxxx
                        inside = LevelElements.values()[random.nextInt(LevelElements.values().length)];
                    } while ((inside == LevelElements.EAT) || (inside == LevelElements.FUSE_STATIC) || (inside == LevelElements.FUSE_DYNAMIC));
                    insideNumberOfPositions = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION;
                    outside = LevelElements.EAT;
                    outsideNumberOfPositions = NUMBER_OF_RENDER_CALLS;
                    break;
                case RIGHT:
                case RIGHT_DOWN:
                case DOWN:
                case LEFT_DOWN:
                    inside  = LevelElements.EAT;
                    insideNumberOfPositions = NUMBER_OF_RENDER_CALLS;
                    do {
                        // everything except EAT, FUSE_STATIC and FUSE_DYNAMIC // TODO: remove after proper implementation of FUSE_xxxx
                        outside = LevelElements.values()[random.nextInt(LevelElements.values().length)];
                    } while ((outside == LevelElements.EAT) || (outside == LevelElements.FUSE_STATIC) || (outside == LevelElements.FUSE_DYNAMIC));
                    outsideNumberOfPositions = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION;
                    break;
                case STATIC:
                default:
                    throw new UnsupportedOperationException();
            }
            // inside with direction
            setupDynamicElement(
                start,
                direction,
                inside,
                insideNumberOfPositions
            );
            // outside with direction.reverse
            start.set(start.getX() - direction.getDeltaX(), start.getY() - direction.getDeltaY());
            setupDynamicElement(
                start,
                direction.reverse(),
                outside,
                outsideNumberOfPositions
            );
        }

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    /**
     * doTestDynamicDynamicCollision
     */
    public final void testDynamicDynamicCollisionEaten() {
        final int numberOfCallsAfterCollision = (
            NUMBER_OF_RENDER_CALLS - NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION
        );
        Field.initialize(new Field.Size(FIELD_SIZE, FIELD_SIZE), Field.TopLinePosition.NONE);

        for (Field.Direction direction : Field.Direction.values()) {
            if (direction == Field.Direction.STATIC) {
                continue;
            }
            int offsetX = direction.getDeltaX() * (numberOfCallsAfterCollision + 1);
            int offsetY = direction.getDeltaY() * (numberOfCallsAfterCollision + 1);
            Field.Position start = new Field.Position(
                INITIAL_POINT.getX() - offsetX,
                INITIAL_POINT.getY() - offsetY
            );
            LevelElements inside;
            int insideNumberOfPositions;
            LevelElements outside;
            int outsideNumberOfPositions;
            switch (direction) {
                case LEFT:
                case LEFT_UP:
                case UP:
                case RIGHT_UP:
                    do {
                        // everything except EATEN, FUSE_STATIC and FUSE_DYNAMIC // TODO: remove after proper implementation of FUSE_xxxx
                        inside = LevelElements.values()[random.nextInt(LevelElements.values().length)];
                    } while ((inside == LevelElements.EATEN) || (inside == LevelElements.FUSE_STATIC) || (inside == LevelElements.FUSE_DYNAMIC));
                    insideNumberOfPositions = NUMBER_OF_RENDER_CALLS;
                    outside = LevelElements.EATEN;
                    outsideNumberOfPositions = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION;
                    break;
                case RIGHT:
                case RIGHT_DOWN:
                case DOWN:
                case LEFT_DOWN:
                    inside  = LevelElements.EATEN;
                    insideNumberOfPositions = NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION;
                    do {
                        // everything except EATEN, FUSE_STATIC and FUSE_DYNAMIC // TODO: remove after proper implementation of FUSE_xxxx
                        outside = LevelElements.values()[random.nextInt(LevelElements.values().length)];
                    } while ((outside == LevelElements.EATEN) || (outside == LevelElements.FUSE_STATIC) || (outside == LevelElements.FUSE_DYNAMIC));
                    outsideNumberOfPositions = NUMBER_OF_RENDER_CALLS;
                    break;
                case STATIC:
                default:
                    throw new UnsupportedOperationException();
            }
            // inside with direction
            setupDynamicElement(
                start,
                direction,
                inside,
                insideNumberOfPositions
            );
            // outside with direction.reverse
            start.set(start.getX() - direction.getDeltaX(), start.getY() - direction.getDeltaY());
            setupDynamicElement(
                start,
                direction.reverse(),
                outside,
                outsideNumberOfPositions
            );
        }

        render(NUMBER_OF_RENDER_CALLS);
        verify();
    }

    private static final Field.ConstantPosition INITIAL_POINT = new Field.Position(FIELD_SIZE / 2, FIELD_SIZE / 2);
    private static final int NUMBER_OF_RENDER_CALLS_BEFORE_COLLISION = NUMBER_OF_RENDER_CALLS / 2;
    private final Random random = new Random(43);
}