package nl.marayla.Xara;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.marayla.Xara.ElementCollisions.ElementCollisionData;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.Renderer.RenderData;

// Attributes of Field:
//  Contains 2D-array of cells
//  Contains physics (collision detection)
//      moving objects can collide
//      1. new placing determined by collision-resolver and element-collisions
//      2. effect determined by elements and placing
public final class Field {

    /*
     * PlacingAfterCollision
     */
    public interface PlacingAfterCollision {
        void execute();
    }

    /*
     * PlacingNone
     */
    public static class PlacingNone implements PlacingAfterCollision {
        @Override
        public final void execute() {
        }
    }

    /*
     * PlacingOne
     */
    public static class PlacingOne implements PlacingAfterCollision {
        protected static final class PlacingData {
            public PlacingData(final int index, final GameElement element, final ConstantDirection direction) {
                this.index = index;
                this.element = element;
                this.direction = direction;
            }

            public final int getIndex() {
                return index;
            }

            public final GameElement getElement() {
                return element;
            }

            public final ConstantDirection getDirection() {
                return direction;
            }

            private final int index;
            private final GameElement element;
            private final ConstantDirection direction;
        }

        public PlacingOne(
            final int placingIndex,
            final GameElement placingElement,
            final ConstantDirection placingDirection
        ) {
            placingData = new PlacingData(placingIndex, placingElement, placingDirection);
        }

        @Override
        public void execute() {
            assert getElement(placingData.getIndex()) == null;
            addElement(placingData.getIndex(), placingData.getElement(), placingData.getDirection());
        }

        private final PlacingData placingData;
    }

    /*
     * PlacingBoth
     */
    public static class PlacingBoth extends PlacingOne {
        public PlacingBoth(
            final int placingIndexOne,
            final GameElement placingElementOne,
            final ConstantDirection placingDirectionOne,
            final int placingIndexTwo,
            final GameElement placingElementTwo,
            final ConstantDirection placingDirectionTwo
        ) {
            super(placingIndexOne, placingElementOne, placingDirectionOne);

            placingData = new PlacingData(placingIndexTwo, placingElementTwo, placingDirectionTwo);
        }

        @Override
        public void execute() {
            super.execute();

            assert getElement(placingData.getIndex()) == null;
            addElement(placingData.getIndex(), placingData.getElement(), placingData.getDirection());
        }

        private final PlacingData placingData;
    }

    /*
     * ConstantSize
     */
    public interface ConstantSize {
        int getWidth();

        int getHeight();
    }

    /*
     * Size
     */
    public static class Size implements ConstantSize {
        public Size(final int width, final int height) {
            set(width, height);
        }

        public Size(final ConstantSize other) {
            set(other);
        }

        @Override
        public final int getWidth() {
            return width;
        }

        @Override
        public final int getHeight() {
            return height;
        }

        public final void set(final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        public final void set(final ConstantSize other) {
            width = other.getWidth();
            height = other.getHeight();
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ConstantSize)) {
                return false;
            }
            ConstantSize other = (ConstantSize) o;
            return ((width == other.getWidth()) && (height == other.getHeight()));
        }

        @Override
        public final int hashCode() {
            throw new UnsupportedOperationException();
        }

        private int width = 0;
        private int height = 0;
    }

    /*
     * ConstantPosition
     */
    public interface ConstantPosition {
        int getX();

        int getY();
    }

    /*
     * Position
     */
    public static class Position implements ConstantPosition {
        public static final ConstantPosition ORIGIN = new Field.Position(0, 0);

        public Position(final int x, final int y) {
            set(x, y);
        }

        public Position(final ConstantPosition other) {
            set(other);
        }

        @Override
        public final int getX() {
            return x;
        }

        @Override
        public final int getY() {
            return y;
        }

        public final void add(final ConstantDirection direction) {
            x += direction.getDeltaX();
            y += direction.getDeltaY();
        }

        public final void set(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public final void set(final ConstantPosition other) {
            x = other.getX();
            y = other.getY();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || (getClass() != o.getClass())) {
                return false;
            }

            Position position = (Position) o;
            return (x == position.x) && (y == position.y);
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }

        public final String toString() {
            return "Field.Position (" + x + ", " + y + ")";
        }

        private int x = 0;
        private int y = 0;
    }

    /*
     * ConstantDirection
     */
    public interface ConstantDirection {
        int getDeltaX();

        int getDeltaY();

        ConstantDirection reverse();

        ConstantDirection reverseX();

        ConstantDirection reverseY();

        ConstantDirection combine(final ConstantDirection direction);

        ConstantDirection extract(final ConstantDirection direction);

        ConstantDirection perpendicular();
    }

    /*
     * Direction
     */
    public enum Direction implements ConstantDirection {
        STATIC(0, 0),
        LEFT_UP(-1, -1),
        UP(0, -1),
        RIGHT_UP(1, -1),
        RIGHT(1, 0),
        RIGHT_DOWN(1, 1),
        DOWN(0, 1),
        LEFT_DOWN(-1, 1),
        LEFT(-1, 0);

        @Override
        public final int getDeltaX() {
            return deltaX;
        }

        @Override
        public final int getDeltaY() {
            return deltaY;
        }

        @Override
        public final ConstantDirection reverse() {
            return determineDirectionBasedOnDeltaXAndDeltaY(-deltaX, -deltaY);
        }

        @Override
        public final ConstantDirection reverseX() {
            return determineDirectionBasedOnDeltaXAndDeltaY(-deltaX, deltaY);
        }

        @Override
        public final ConstantDirection reverseY() {
            return determineDirectionBasedOnDeltaXAndDeltaY(deltaX, -deltaY);
        }

        @Override
        public final ConstantDirection combine(final ConstantDirection direction) {
            return determineDirectionBasedOnDeltaXAndDeltaY(
                deltaX + direction.getDeltaX(),
                deltaY + direction.getDeltaY()
            );
        }

        @Override
        public final ConstantDirection extract(final ConstantDirection direction) {
            return determineDirectionBasedOnDeltaXAndDeltaY(
                (deltaX == direction.getDeltaX()) ? 0 : deltaX, // if x-direction equal extract else do not change
                (deltaY == direction.getDeltaY()) ? 0 : deltaY  // if y-direction equal extract else do not change
            );
        }

        @Override
        public final ConstantDirection perpendicular() {
            return determineDirectionBasedOnDeltaXAndDeltaY(deltaY, -deltaX);
        }

        Direction(final int deltaX, final int deltaY) {
            assert (deltaX >= -1) && (deltaX <= 1);
            assert (deltaY >= -1) && (deltaY <= 1);

            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        private static Direction determineDirectionBasedOnDeltaXAndDeltaY(final int deltaX, final int deltaY) {
            if (deltaX < 0) { // x => LEFT
                if (deltaY < 0) { // y  => UP
                    return LEFT_UP;
                }
                else if (deltaY > 0) { // y => DOWN
                    return LEFT_DOWN;
                }
                else { // y => STATIC
                    return LEFT;
                }
            }
            else if (deltaX > 0) { // x => RIGHT
                if (deltaY < 0) { // y => UP
                    return RIGHT_UP;
                }
                else if (deltaY > 0) { // y => DOWN
                    return RIGHT_DOWN;
                }
                else { // y=> STATIC
                    return RIGHT;
                }
            }
            else { // x => STATIC
                if (deltaY < 0) { // y => UP
                    return UP;
                }
                else if (deltaY > 0) { // y => DOWN
                    return DOWN;
                }
                else { // y => STATIC
                    return STATIC;
                }
            }
        }

        private final int deltaX;
        private final int deltaY;
    }

    /*
     * UpdatableDirection
     */
    public static class UpdatableDirection implements ConstantDirection {
        public UpdatableDirection(ConstantDirection direction) {
            this.direction = direction;
        }

        @Override
        public final int getDeltaX() {
            return direction.getDeltaX();
        }

        @Override
        public final int getDeltaY() {
            return direction.getDeltaY();
        }

        @Override
        public final ConstantDirection reverse() {
            return direction.reverse();
        }

        @Override
        public final ConstantDirection reverseX() {
            return direction.reverseX();
        }

        @Override
        public final ConstantDirection reverseY() {
            return direction.reverseY();
        }

        @Override
        public final ConstantDirection combine(final ConstantDirection direction) {
            return this.direction.combine(direction);
        }

        @Override
        public final ConstantDirection extract(final ConstantDirection direction) {
            return this.direction.extract(direction);
        }

        @Override
        public final ConstantDirection perpendicular() {
            return this.direction.perpendicular();
        }

        public final void update(final ConstantDirection direction) {
            this.direction = direction;
        }

        private ConstantDirection direction;
    }

    public interface ColorToElement {
        GameElement transform(final int rgb);
    }

    public interface ColorToDirection {
        ConstantDirection transform(final int rgb);
    }

    /*
     * FieldRenderer interface
     */
    public static void render(final LevelGamePlay levelGamePlay, final RenderData renderData) {
        final int index = calculateIndex(startPositionVisibleArea);
        final int cellLineWidth = size.getWidth();
        final int startColumnIndex = index % cellLineWidth;
        int rowIndex = (index / cellLineWidth) * cellLineWidth;

        Field.Position position = new Field.Position(0, 0);
        for (int y = 0; y < size.getHeight() - 2; y++) {
            int columnIndex = startColumnIndex;

            for (int x = 0; x < size.getWidth() - 2; x++) {
                final GameElement element = cells[rowIndex + columnIndex];
                if (element != null) {
                    position.set(x, y);
                    levelGamePlay.renderElement(element, renderData, position);
                }

                columnIndex++;
                if (columnIndex >= cellLineWidth) {
                    columnIndex = 0;
                }
            }
            rowIndex += cellLineWidth;
            if (rowIndex >= cells.length) {
                rowIndex = 0;
            }
        }
    }

    /**
     * FieldCells interface
     */
    public static GameElement getElement(final int index) {
        return cells[index];
    }

    public static ConstantSize getSize() {
        return size;
    }

    protected static ConstantMovingCell getMovingCell(final int index) {
        return movingCells.get(index);
    }

    protected static void addElement(
        final int index,
        final GameElement element,
        final ConstantDirection direction
    ) {
        assert (cells[index] == null) : "cells[" + index + "] = " + cells[index];
        cells[index] = element;
        addDirection(index, direction);
    }

    /**
     * removeElement will remove an element in field.
     * It is allowed to point at an empty (= null) element.
     *
     * @param index is the element-index that determines which element to remove
     */
    protected static void removeElement(final int index) {
        if (cells[index] != null) {
            cells[index] = null;
            movingCells.remove(index);
        }
    }

    public static void addElementInjectionLine(final GameElement element, final int visibleOffset) {
        doAddElement(element, new Position(
            injectionLineDirection.getDeltaX() * visibleOffset + injectionLinePosition.getX(),
            injectionLineDirection.getDeltaY() * visibleOffset + injectionLinePosition.getY()
        ));
    }

    public static void addStaticElement(
        final GameElement element,
        final ConstantPosition visiblePosition
    ) {
        doAddElement(element, visiblePosition);
    }

    public static void addMovingElement(
        final GameElement element,
        final ConstantPosition visiblePosition,
        final ConstantDirection direction
    ) {
        final int index = doAddElement(element, visiblePosition);
        addDirection(index, direction);
    }

    private static int doAddElement(final GameElement element, final ConstantPosition visiblePosition) {
        final int index = calculateIndex(
            moveX(startPositionVisibleArea.getX(), visiblePosition.getX()),
            moveY(startPositionVisibleArea.getY(), visiblePosition.getY())
        );
        removeElement(index);
        addElement(index, element, Direction.STATIC);
        return index;
    }

    private static int doCalculateIndex(final int current, final int column, final int row) {
        final int rowLength = size.getWidth();
        return moveX(current % rowLength, column) + moveY(current / rowLength, row) * rowLength;
    }

    private static int calculateIndex(final int column, final int row) {
        return doCalculateIndex(0, column, row);
    }

    private static int calculateIndex(final ConstantPosition position) {
        return doCalculateIndex(0, position.getX(), position.getY());
    }

    // TODO: determine if this method should be static
    public static int calculateIndex(final int current, final ConstantDirection direction) {
        return doCalculateIndex(current, direction.getDeltaX(), direction.getDeltaY());
    }

    public static void initialize(final ConstantSize visibleSize, final ConstantDirection direction) {
        setDirection(direction);
        resize(new Size(visibleSize.getWidth() + 2, visibleSize.getHeight() + 2));
        startPositionVisibleArea = new Position(1, 1);
        frameCounter = 0;
    }

    public static void initializeFromImage(
        final BufferedImage image,
        final ColorToElement colorToElement,
        final ColorToDirection colorToDirection,
        final ConstantDirection direction
    ) {
        initialize(new Size(image.getWidth() - 2, image.getHeight() - 2), direction);

        final int[] colorArray = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        Field.Position point = new Field.Position(0, 0);
        int index = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int color = (colorArray[index++] & 0x00FFFFFF);
                if (color == 0) {
                    continue;
                }
                point.set(x, y);
                addElement(
                    calculateIndex(point),
                    colorToElement.transform(color),
                    colorToDirection.transform(color)
                );
            }
        }
    }

    public static void nextFrame(final LevelGamePlay levelGamePlay) {
        frameCounter++;
        CollisionHandler.handleAllCollisions(levelGamePlay, movingCells.keySet());
        move();
        cleanInjectionLine();
    }

    private static void move() {
        startPositionVisibleArea = new Position(
            moveX(startPositionVisibleArea.getX(), -direction.getDeltaX()),
            moveY(startPositionVisibleArea.getY(), -direction.getDeltaY())
        );
    }

    private static void cleanInjectionLine() {
        final int increment = injectionLineDirection.getDeltaX() + injectionLineDirection.getDeltaY() * size.getWidth();

        int index = calculateIndex(
            moveX(startPositionVisibleArea.getX(), injectionLinePosition.getX()),
            moveY(startPositionVisibleArea.getY(), injectionLinePosition.getY())
        );
        for (int i = 0; i < injectionLineLength; i++) {
            removeElement(index);
            index += increment;
            if (index >= cells.length) {
                index -= cells.length;
            }
        }
    }

    private static int moveX(final int start, final int distance) {
        return doMove(start, distance, size.getWidth());
    }

    private static int moveY(final int start, final int distance) {
        return doMove(start, distance, size.getHeight());
    }

    private static int doMove(final int start, final int distance, final int border) {
        int result = start + distance;
        if (result < 0) {
            result += border;
        }
        else if (result >= border) {
            result -= border;
        }
        return result;
    }

    private static void addDirection(final int index, final Field.ConstantDirection direction) {
        assert (!movingCells.containsKey(index));
        if (direction != Direction.STATIC) {
            movingCells.put(index, new NotConnectedMovingCell(direction));
        }
    }

    private static void resize(final ConstantSize size) {
        Field.size.set(size.getWidth(), size.getHeight());
        createCells();
        setInjectionLineLength();
    }

    private static void createCells() {
        cells = new GameElement[size.getWidth() * size.getHeight()];
        movingCells.clear();
    }

    private static void setDirection(final ConstantDirection constantDirection) {
        direction = constantDirection;

        ConstantDirection moveRelativeDirection;
        int moveRelativeDistance;
        if ((direction == Direction.STATIC) || (direction == Direction.DOWN) || (direction == Direction.UP)) {
            injectionLineDirection = Direction.RIGHT;
            moveRelativeDirection = Direction.UP;
            moveRelativeDistance = (direction == Direction.UP) ? 2 : 1; // 2 to reach end of field
        }
        else if ((direction == Direction.LEFT) || (direction == Direction.RIGHT)) {
            injectionLineDirection = Direction.DOWN;
            moveRelativeDirection = Direction.LEFT;
            moveRelativeDistance = (direction == Direction.LEFT) ? 2 : 1; // 2 to reach end of field
        }
        else {
            throw new UnsupportedOperationException();
        }
        injectionLinePosition = new Position(
            moveRelativeDirection.getDeltaX() * moveRelativeDistance,
            moveRelativeDirection.getDeltaY() * moveRelativeDistance
        );
        setInjectionLineLength();
    }

    private static void setInjectionLineLength() {
        if ((direction == Direction.STATIC) || (direction == Direction.DOWN) || (direction == Direction.UP)) {
            injectionLineLength = (direction == Direction.STATIC) ? 0 : (size.getWidth() - 2); // only visible part
        }
        else if ((direction == Direction.LEFT) || (direction == Direction.RIGHT)) {
            injectionLineLength = (size.getHeight() - 2); // only visible part
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public interface ConstantMovingCell {
        Field.ConstantDirection getDirection();

        int[] getConnections();
    }

    private static abstract class MovingCell implements ConstantMovingCell {
        public MovingCell(final ConstantDirection direction) {
            this.direction = direction;
        }

        @Override
        public Field.ConstantDirection getDirection() {
            return direction;
        }

        private final Field.ConstantDirection direction;
    }

    private static class NotConnectedMovingCell extends MovingCell {
        public NotConnectedMovingCell(final ConstantDirection direction) {
            super(direction);
        }

        @Override
        public int[] getConnections() {
            return null;
        }
    }

    private static class ConnectedMovingCell extends MovingCell {
        public ConnectedMovingCell(final ConstantDirection direction, final int[] connections) {
            super(direction);

            assert connections.length != 0;
            this.connections = connections;
        }

        @Override
        public final int[] getConnections() {
            return connections;
        }

        private final int[] connections;
    }

    private static GameElement[] cells;
    private static final Map<Integer, ConstantMovingCell> movingCells = new TreeMap<>();
    private static final Size size = new Size(0, 0);
    private static ConstantDirection direction;
    private static ConstantPosition startPositionVisibleArea;
    private static ConstantDirection injectionLineDirection;
    private static ConstantPosition injectionLinePosition;
    private static int injectionLineLength;
    private static int frameCounter;

    // Hide constructor
    private Field() {
    }
}

/*
 * CollisionHandler
 */
class CollisionHandler {
    public static void handleAllCollisions(final LevelGamePlay levelGamePlay, final Set<Integer> movingCells) {
        LinkedList<Integer> collisions = new LinkedList<>(movingCells);
        while (!collisions.isEmpty()) {
            int cellIndex = collisions.removeFirst();
            assert (Field.getElement(cellIndex) != null);
            assert (Field.getMovingCell(cellIndex).getDirection() != Field.Direction.STATIC);
            handleSingleCollision(collisions, cellIndex, levelGamePlay);
        }
    }

    /**
     * DONE Issue1 : Watch out for GameElement that does not move (Direction.STATIC)
     * -> ignore collision handling
     * DONE Issue2 : moving elements can collide in circle -> move all and no collision handling
     * DONE Issue3 : moving elements can point at each other and in that case pass each other
     * without collision -> not handled right now
     * DONE Issue4 : moving elements can end-up in same cell (started from different places)
     * -> most difficult to solve -> not handled right now
     *
     * @param collisions    contains list of all collisions that still have to be handled
     * @param cellIndex     is index of cell that is moving and which possible collision is handled
     * @param levelGamePlay gameplay of this level
     */
    private static void handleSingleCollision(
        final LinkedList<Integer> collisions,
        final int cellIndex,
        final LevelGamePlay levelGamePlay
    ) {
        final ElementCollisionData elementCollisionData = createMainElementCollisionData(cellIndex);
        try {
            Field.removeElement(cellIndex);
            executeMovesBeforeCollision(collisions, levelGamePlay, elementCollisionData);
        }
        finally {
            ElementCollisionData.releaseInstance(elementCollisionData);
        }
    }

    private static void executeHasConnections(
        final LinkedList<Integer> collisions,
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData
    ) {
        if (hasConnection(mainElementCollisionData)) {
            final int[] connections = mainElementCollisionData.getConnections();
            for (int i = 0; i < connections.length; i++) {
// TODO:
            }
        }
        executeMovesBeforeCollision(collisions, levelGamePlay, mainElementCollisionData);
    }

    private static void executeMovesBeforeCollision(
        final LinkedList<Integer> collisions,
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData
    ) {
        // Store information for other-element
        final ElementCollisionData otherElementCollisionData = createOtherElementCollisionData(
            mainElementCollisionData
        );
        try {
            if (causeCollision(otherElementCollisionData)) {
                doExecuteMovesBeforeCollision(
                    collisions,
                    levelGamePlay,
                    mainElementCollisionData,
                    otherElementCollisionData
                );
            }
            else {
                executeNoCollision(mainElementCollisionData);
            }
        }
        finally {
            ElementCollisionData.releaseInstance(otherElementCollisionData);
        }
    }

    private static void doExecuteMovesBeforeCollision(
        LinkedList<Integer> collisions,
        LevelGamePlay levelGamePlay,
        ElementCollisionData mainElementCollisionData,
        ElementCollisionData otherElementCollisionData
    ) {
        if (movesBeforeCollision(collisions, otherElementCollisionData)) {
            handleSingleCollision(collisions, otherElementCollisionData.getIndex(), levelGamePlay);
            executeMovesByCollision(collisions, levelGamePlay, mainElementCollisionData);
        }
        else {
            doExecuteMovesByCollision(
                collisions,
                levelGamePlay,
                mainElementCollisionData,
                otherElementCollisionData
            );
        }
    }

    private static void executeMovesByCollision(
        final LinkedList<Integer> collisions,
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData
    ) {
        final ElementCollisionData otherElementCollisionData = createOtherElementCollisionData(
            mainElementCollisionData
        );
        try {
            if (causeCollision(otherElementCollisionData)) {
                doExecuteMovesByCollision(
                    collisions,
                    levelGamePlay,
                    mainElementCollisionData,
                    otherElementCollisionData
                );
            }
            else {
                executeNoCollision(mainElementCollisionData);
            }
        }
        finally {
            ElementCollisionData.releaseInstance(otherElementCollisionData);
        }
    }

    private static void doExecuteMovesByCollision(
        final LinkedList<Integer> collisions,
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData,
        final ElementCollisionData otherElementCollisionData
    ) {
        determineCollisions(levelGamePlay, mainElementCollisionData, otherElementCollisionData);

        // Check if staticElement will be moved outside collision-area by collision
        final Field.ConstantDirection otherElementCollisionDirection = determineDirectionOfElementDueToCollision(
            mainElementCollisionData,
            otherElementCollisionData
        );
        if (otherElementCollisionDirection != Field.Direction.STATIC) {
            changeToMovingElement(otherElementCollisionData, otherElementCollisionDirection);
            handleSingleCollision(collisions, otherElementCollisionData.getIndex(), levelGamePlay);
            executePlacingAndEffect(levelGamePlay, mainElementCollisionData);
        }
        else {
            doExecutePlacingAndEffect(
                levelGamePlay,
                mainElementCollisionData,
                otherElementCollisionData
            );
        }
    }

    private static void executePlacingAndEffect(
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData
    ) {
        final ElementCollisionData otherElementCollisionData = createOtherElementCollisionData(
            mainElementCollisionData
        );
        try {
            if (causeCollision(otherElementCollisionData)) {
                determineCollisions(levelGamePlay, mainElementCollisionData, otherElementCollisionData);
                doExecutePlacingAndEffect(levelGamePlay, mainElementCollisionData, otherElementCollisionData);
            }
            else {
                executeNoCollision(mainElementCollisionData);
            }
        }
        finally {
            ElementCollisionData.releaseInstance(otherElementCollisionData);
        }
    }

    private static void doExecutePlacingAndEffect(
        LevelGamePlay levelGamePlay,
        ElementCollisionData mainElementCollisionData,
        ElementCollisionData otherElementCollisionData
    ) {
        final Field.PlacingAfterCollision placing = determinePlacing(
            mainElementCollisionData,
            otherElementCollisionData
        );

        final ElementEffect effect = levelGamePlay.determineElementEffect(
            placing,
            mainElementCollisionData.getElement(),
            otherElementCollisionData.getElement()
        );

        // main is already removed so no need to remove again
        Field.removeElement(otherElementCollisionData.getIndex());

        placing.execute();
        effect.execute();
    }

    private static void executeNoCollision(final ElementCollisionData elementCollisionData) {
        new Field.PlacingOne(
            elementCollisionData.calculateNextIndex(),
            elementCollisionData.getElement(),
            elementCollisionData.getDirection()
        ).execute();
    }

    private static void determineCollisions(
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData,
        final ElementCollisionData otherElementCollisionData
    ) {
        mainElementCollisionData.determineCollision(levelGamePlay, otherElementCollisionData.getElement());
        otherElementCollisionData.determineCollision(levelGamePlay, mainElementCollisionData.getElement());
    }

    private static Field.ConstantDirection determineDirectionOfElementDueToCollision(
        final ElementCollisionData mainElement,
        final ElementCollisionData otherElement
    ) {
        if (otherElement.isColliding()) {
            return Field.Direction.STATIC;
        }
        else {
            return mainElement.getCollision().moveOtherElementDueToCollision(mainElement, otherElement);
        }
    }

    private static Field.PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert (element1.isColliding() || element2.isColliding());

        try {
            return element1.getCollision().determinePlacing(element1, element2);
        }
        catch (UnsupportedOperationException e) {
            return element2.getCollision().determinePlacing(element2, element1);
        }
    }

    private static void changeToMovingElement(
        final ElementCollisionData elementCollisionData,
        final Field.ConstantDirection direction
    ) {
        Field.removeElement(elementCollisionData.getIndex());
        Field.addElement(elementCollisionData.getIndex(), elementCollisionData.getElement(), direction);
    }

    private static boolean hasConnection(final ElementCollisionData elementCollisionData) {
        return elementCollisionData.getConnections().length > 0;
    }

    private static boolean movesBeforeCollision(
        final LinkedList<Integer> collisions,
        final ElementCollisionData elementCollisionData
    ) {
        // element is part of collisions and is not colliding with current element
        return (collisions.remove((Integer) elementCollisionData.getIndex()) &&
            !elementCollisionData.isColliding());
    }

    private static boolean causeCollision(final ElementCollisionData elementCollisionData) {
        return (elementCollisionData.getElement() != null);
    }

    private static ElementCollisionData createMainElementCollisionData(final int cellIndex) {
        assert (Field.getMovingCell(cellIndex).getDirection() != Field.Direction.STATIC);
        assert (Field.getElement(cellIndex) != null);

        return ElementCollisionData.createInstance(
            cellIndex,
            Field.getElement(cellIndex),
            Field.getMovingCell(cellIndex),
            true
        );
    }

    private static ElementCollisionData createOtherElementCollisionData(final ElementCollisionData elementCollisionData) {
        final int nextCellIndex = elementCollisionData.calculateNextIndex();
        final Field.ConstantMovingCell movingCell = Field.getMovingCell(nextCellIndex);
        return ElementCollisionData.createInstance(
            nextCellIndex,
            Field.getElement(nextCellIndex),
            movingCell,
            (movingCell != null) && (movingCell.getDirection().reverse() == elementCollisionData.getDirection())
        );
    }
}
