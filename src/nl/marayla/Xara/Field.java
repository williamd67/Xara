package nl.marayla.Xara;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import nl.marayla.Xara.ElementCollisions.ElementCollisionData;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.Platform.XaraLog;
import nl.marayla.Xara.Renderer.CellRenderer.RenderCellsBottom;
import nl.marayla.Xara.Renderer.CellRenderer.RenderCellsTop;
import nl.marayla.Xara.Renderer.CellRenderer.RenderCellsLeft;
import nl.marayla.Xara.Renderer.CellRenderer.RenderCellsRight;
import nl.marayla.Xara.Renderer.CellRenderer.RenderCells;
import nl.marayla.Xara.Renderer.RenderData;
import org.jetbrains.annotations.Contract;

// Attributes of Field:
//  Contains physics (collision detection)
//      static objects cannot collide
//      moving objects do collide
//      1. against static object ->
//          effect determined by static object,
//          executed on moving object by LevelGamePlay
//      2. against moving object ->
//          ???
//  Contains 2D-array of cells
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
        protected final class PlacingData {
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

            private int index;
            private GameElement element;
            private ConstantDirection direction;
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

        private PlacingData placingData;
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

        private PlacingData placingData;
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

        @Contract(pure = true)
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

        @Contract(" -> fail")
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

        ConstantDirection combine(final ConstantDirection direction);

        ConstantDirection extract(final ConstantDirection direction);
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

        @Contract(pure = true)
        @Override
        public final ConstantDirection reverse() {
            return determineDirectionBasedOnDeltaXandDeltaY(-deltaX, -deltaY);
        }

        @Override
        public final ConstantDirection combine(final ConstantDirection direction) {
            return determineDirectionBasedOnDeltaXandDeltaY(
                deltaX + direction.getDeltaX(),
                deltaY + direction.getDeltaY()
            );
        }

        @Override
        public final ConstantDirection extract(final ConstantDirection direction) {
            return determineDirectionBasedOnDeltaXandDeltaY(
                (deltaX == direction.getDeltaX()) ? 0 : deltaX, // if x-direction equal extract else do not change
                (deltaY == direction.getDeltaY()) ? 0 : deltaY  // if y-direction equal extract else do not change
            );
        }

        Direction(final int deltaX, final int deltaY) {
            assert (deltaX >= -1) && (deltaX <= 1);
            assert (deltaY >= -1) && (deltaY <= 1);

            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        @Contract(pure = true)
        private static Direction determineDirectionBasedOnDeltaXandDeltaY(final int deltaX, final int deltaY) {
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
     * UpdateableDirection
     */
    public static class UpdateableDirection implements ConstantDirection {
        public UpdateableDirection(ConstantDirection direction) {
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
        public final ConstantDirection combine(final ConstantDirection direction) {
            return this.direction.combine(direction);
        }

        @Override
        public final ConstantDirection extract(final ConstantDirection direction) {
            return this.direction.extract(direction);
        }

        public final void update(final ConstantDirection direction) {
            this.direction = direction;
        }

        private ConstantDirection direction = Direction.STATIC;
    }

    /*
     * TopLinePosition
     */
    public enum TopLinePosition {
        NONE,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    /*
     * FieldRenderer interface
     */
    public static void render(final LevelGamePlay levelGamePlay, final RenderData renderData) {
        if (renderCells != null) {
            renderCells.render(levelGamePlay, cells, arraySize, topLine, renderData);
        }
    }

    /**
     * FieldCells interface
     */
    @Contract(pure = true)
    public static GameElement getElement(final int index) {
        return cells[index];
    }

    private static ConstantDirection getDirection(final int index) {
        final MovingCellContent content = movingCells.get(index);
        return content != null ? content.direction : Direction.STATIC;
    }

    private static void addElement(
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
    private static void removeElement(final int index) {
        if (cells[index] != null) {
            cells[index] = null;
            movingCells.remove(index);
        }
    }

    private static void moveElement(final int index, final int nextIndex) {
        assert (cells[index] != null);
        assert (cells[nextIndex] == null);

        GameElement element = cells[index];
        cells[index] = null;
        cells[nextIndex] = element;
        if (movingCells.containsKey(index)) {
            MovingCellContent movingCellContent = movingCells.remove(index);
            movingCells.put(nextIndex, movingCellContent);
        }
    }

    private static int calculateIndex(final int index, final ConstantPosition relativePosition) {
        int result;
        switch (topLinePosition) {
            case NONE:
            case TOP:
            case BOTTOM:
                result = index + relativePosition.getX() + (relativePosition.getY() * topLineSize);
                break;
            case LEFT:
            case RIGHT:
                result = index + relativePosition.getY() + (relativePosition.getX() * topLineSize);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (result < 0) {
            result += cells.length;
        }
        else if (result >= cells.length) {
            result -= cells.length;
        }
        return result;
    }

    // TODO: determine if this method should be static
    public static int calculateIndex(final int index, final ConstantDirection direction) {
        return calculateIndex(index, new Position(direction.getDeltaX(), direction.getDeltaY()));
    }

    public static void initialize(
        final ConstantSize externalSize,
        final TopLinePosition topLinePosition
    ) {
        setTopLinePosition(topLinePosition);
        resize(externalSize);
        switch (topLinePosition) {
            case NONE:
                topLineSize = arraySize.getWidth();
                maxTopLine = arraySize.getHeight();
                break;
            case TOP:
            case BOTTOM:
                topLineSize = arraySize.getWidth();
                maxTopLine = arraySize.getHeight();
                break;
            case LEFT:
            case RIGHT:
                topLineSize = arraySize.getHeight();
                maxTopLine = arraySize.getWidth();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        topLine = 0;
        frameCounter = 0;
    }

    public static void addElementTopLine(final GameElement element, final int externalPosition) {
        int internalPosition = externalToInternalPosition(externalPosition);
        assert (internalPosition < topLineSize);
        int index = (topLine * topLineSize) + internalPosition;
        removeElement(index);
        addElement(index, element, Direction.STATIC);
    }

    public static void addStaticElement(
        final GameElement element,
        final ConstantPosition externalPosition
    ) {
        doAddElement(element, externalPosition);
    }

    public static void addMovingElement(
        final GameElement element,
        final ConstantPosition externalPosition,
        final ConstantDirection direction
    ) {
        int index = doAddElement(element, externalPosition);
        addDirection(index, direction);
    }

    public static void nextFrame(final LevelGamePlay levelGamePlay) {
        if (XaraLog.DEBUG) {
            System.out.println("hello debug");
            XaraLog.log.d("Some tag", "Some message");
        }
        else {
            System.out.println("hello no debug - error");
            XaraLog.log.e("Some error", "Some error-message");
        }
        frameCounter++;
        handleCollisions(levelGamePlay);
        // TODO Determine when exactly to update topLine
        if (topLinePosition != Field.TopLinePosition.NONE) {
            if (topLine == 0) {
                topLine = maxTopLine;
            }
            topLine--;
        }
        // Remove first row
        int line = topLine * topLineSize;
        for (int cell = 1; cell < (topLineSize - 1); cell++) {
            removeElement(line + cell);
        }
        // remove last row
        line -= topLineSize;
        if (line < 0) {
            line += cells.length;
        }
        for (int cell = 1; cell < (topLineSize - 1); cell++) {
            removeElement(line + cell);
        }
        // Remove all elements in the first and last column
        int cellIndex = -1;
        for (int rows = 0; rows < maxTopLine; rows++) {
            cellIndex++;
            removeElement(cellIndex);
            cellIndex += (topLineSize - 1);
            removeElement(cellIndex);
        }
    }

    @Contract(pure = true)
    private static int externalToInternalPosition(final int externalPosition) {
        return externalPosition + 1;
    }

    @Contract("_ -> !null")
    private static ConstantPosition externalToInternalPosition(final ConstantPosition externalPosition) {
        return new Position(externalPosition.getX() + 1, externalPosition.getY() + 1);
    }

    private static void addDirection(final int index, final Field.ConstantDirection direction) {
        assert (!movingCells.containsKey(index));
        if (direction != Direction.STATIC) {
            movingCells.put(index, new MovingCellContent(direction));
        }
    }

    private static int doAddElement(final GameElement element, final ConstantPosition externalPosition) {
        int index;
        final ConstantPosition internalPosition = externalToInternalPosition(externalPosition);
        switch (topLinePosition) {
            case NONE:
            case TOP:
            case BOTTOM:
                index = internalPosition.getX() + (internalPosition.getY() * topLineSize);
                break;
            case LEFT:
            case RIGHT:
                index = internalPosition.getY() + (internalPosition.getX() * topLineSize);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if (cells[index] != null) {
            removeElement(index);
        }
        addElement(index, element, Direction.STATIC);
        return index;
    }

    // Collisions
    private static void handleCollisions(final LevelGamePlay levelGamePlay) {
        LinkedList<Integer> collisions = new LinkedList<>(movingCells.keySet());
        while (!collisions.isEmpty()) {
            int cellIndex = collisions.removeFirst();
            assert (cells[cellIndex] != null);
            assert (movingCells.get(cellIndex) != null);
            assert (movingCells.get(cellIndex).direction != Direction.STATIC);
            doHandleCollision(collisions, cellIndex, levelGamePlay);
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
    private static void doHandleCollision(
        final LinkedList<Integer> collisions,
        final int cellIndex,
        final LevelGamePlay levelGamePlay
    ) {
        final ConstantDirection direction = getDirection(cellIndex);
        assert (direction != Direction.STATIC);
        final GameElement element = cells[cellIndex];
        assert (element != null);
        removeElement(cellIndex);

        // Store information for main-element
        final ElementCollisionData elementCollisionData = ElementCollisionData.createInstance(
            cellIndex,
            element,
            direction,
            true
        );
        try {
            executeCollision(collisions, levelGamePlay, elementCollisionData, true);
        }
        finally {
            ElementCollisionData.releaseInstance(elementCollisionData);
        }
    }

    private static void executeCollision(
        final LinkedList<Integer> collisions,
        final LevelGamePlay levelGamePlay,
        final ElementCollisionData mainElementCollisionData,
        final boolean recursion
    ) {
        // Store information for other-element
        final ElementCollisionData otherElementCollisionData = createOtherElementCollisionData(mainElementCollisionData);
        try {
            if (otherElementCollisionData.getElement() == null) { // No collision
                new PlacingOne(
                    mainElementCollisionData.calculateNextIndex(),
                    mainElementCollisionData.getElement(),
                    mainElementCollisionData.getDirection()
                ).execute();
                return;
            }

            // Check if it collides with another moving element (remove it from collisions-list in case it does)
            if (collisions.remove((Integer) otherElementCollisionData.getIndex())) {
                // Check if otherElement does not collide with mainElement (directions are not opposite)
                if (!otherElementCollisionData.isMoving()) {
                    doHandleCollision(collisions, otherElementCollisionData.getIndex(), levelGamePlay);
                    executeCollision(collisions, levelGamePlay, mainElementCollisionData, true);
                    return;
                }
            }

            // Determine collision-elements
            mainElementCollisionData.determineCollision(levelGamePlay, otherElementCollisionData.getElement());
            otherElementCollisionData.determineCollision(levelGamePlay, mainElementCollisionData.getElement());

            // Check if staticElement will be moved outside collision-area by collision
            if (!otherElementCollisionData.isMoving() && recursion) {
                final ConstantDirection otherElementCollisionDirection = determineDirectionOfStaticElementDueToCollision(
                    mainElementCollisionData,
                    otherElementCollisionData
                );
                if (otherElementCollisionDirection != Direction.STATIC) {
                    removeElement(otherElementCollisionData.getIndex());
                    addElement(
                        otherElementCollisionData.getIndex(),
                        otherElementCollisionData.getElement(),
                        otherElementCollisionDirection
                    );
                    doHandleCollision(collisions, otherElementCollisionData.getIndex(), levelGamePlay);
                    executeCollision(collisions, levelGamePlay, mainElementCollisionData, false);
                    return;
                }
            }

            final PlacingAfterCollision placing = determinePlacing(mainElementCollisionData, otherElementCollisionData);

            final ElementEffect effect = levelGamePlay.determineElementEffect(
                placing,
                mainElementCollisionData.getElement(),
                otherElementCollisionData.getElement()
            );

            // Handle placing
            // main is already removed so no need to remove again
            removeElement(otherElementCollisionData.getIndex());
            placing.execute();

            effect.execute();
        }
        finally {
            ElementCollisionData.releaseInstance(otherElementCollisionData);
        }
    }

    private static ElementCollisionData createOtherElementCollisionData(final ElementCollisionData elementCollisionData) {
        final int nextCellIndex = elementCollisionData.calculateNextIndex();
        final ConstantDirection staticDirection = getDirection(nextCellIndex);
        return ElementCollisionData.createInstance(
            nextCellIndex,
            cells[nextCellIndex],
            staticDirection,
            (staticDirection.reverse() == elementCollisionData.getDirection())
        );
    }

    private static ConstantDirection determineDirectionOfStaticElementDueToCollision(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        if (element2.isMoving()) {
            return element2.getDirection();
        }
        else {
            return element1.getCollision().moveOtherElementDueToCollision(element1, element2);
        }
    }

    private static PlacingAfterCollision determinePlacing(
        final ElementCollisionData element1,
        final ElementCollisionData element2
    ) {
        assert (element1.isMoving() || element2.isMoving());

        try {
            return element1.getCollision().determinePlacing(element1, element2);
        }
        catch (UnsupportedOperationException e) {
            return element2.getCollision().determinePlacing(element2, element1);
        }
    }

    private static void resize(final ConstantSize externalSize) {
        arraySize.set(externalSize.getWidth() + 2, externalSize.getHeight() + 2);
        createCells();
    }

    private static void createCells() {
        cells = new GameElement[arraySize.getWidth() * arraySize.getHeight()];
        movingCells.clear();
    }

    private static void setTopLinePosition(final TopLinePosition inputTopLinePosition) {
        topLinePosition = inputTopLinePosition;
        switch (topLinePosition) {
            case NONE:
                renderCells = new RenderCellsTop();
                break;
            case TOP:
                renderCells = new RenderCellsTop();
                break;
            case BOTTOM:
                renderCells = new RenderCellsBottom();
                break;
            case LEFT:
                renderCells = new RenderCellsLeft();
                break;
            case RIGHT:
                renderCells = new RenderCellsRight();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static class MovingCellContent {
        public MovingCellContent(final ConstantDirection direction) {
            this.direction = direction;
        }

        public Field.ConstantDirection direction;
    }

    private static GameElement[] cells;
    private static Map<Integer, MovingCellContent> movingCells = new TreeMap<>();
    private static Size arraySize = new Size(0, 0);
    private static RenderCells renderCells;
    private static TopLinePosition topLinePosition;
    private static int topLine;
    private static int topLineSize;
    private static int maxTopLine;
    private static int frameCounter;

    // Hide constructor
    private Field() {
    }
}