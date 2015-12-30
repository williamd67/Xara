package nl.marayla.Xara;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import nl.marayla.Xara.ElementCollisions.ElementCollision;
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
//      dynamic objects do collide
//      1. against static object ->
//          effect determined by static object,
//          executed on dynamic object by LevelGamePlay
//      2. against dynamic object ->
//          ???
//  Contains 2D-array of cells
public final class Field {
    /*
     * Action
     */
    public enum Action {
        NONE {
            @Override
            public void execute(final ElementCollisionData data) {
            }
        },
        ADD {
            @Override
            public void execute(final ElementCollisionData data) {
                addElement(data.getIndex(), data.getElement(), data.getDirection());
            }
        },
        REMOVE {
            @Override
            public void execute(final ElementCollisionData data) {
                removeElement(data.getIndex());
            }
        },
        MOVE {
            @Override
            public void execute(final ElementCollisionData data) {
                int nextIndex = calculateIndex(data.getIndex(), data.getDirection());
                if (getElement(nextIndex) == null) {
                    moveElement(data.getIndex(), nextIndex);
                }
            }
        };

        public abstract void execute(final ElementCollisionData data);
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
        private static Direction determineDirectionBasedOnDeltaXandDeltaY(final int deltaX, final int deltaY)
        {
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

        public final void update (final ConstantDirection direction)
        {
            this.direction = direction;
        }

        private ConstantDirection direction = Direction.STATIC;
    }

    /*
     * TopLinePosition
     */
    public enum TopLinePosition {
        NONE, TOP, BOTTOM, LEFT, RIGHT
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
     * @param index is the element-index that determines which element to remove
     */
    private static void removeElement(final int index) {
        if (cells[index] != null) {
            cells[index] = null;
            dynamicCells.remove(index);
        }
    }

    private static void moveElement(final int index, final int nextIndex) {
        assert (cells[index] != null);
        assert (cells[nextIndex] == null);

        GameElement element = cells[index];
        cells[index] = null;
        cells[nextIndex] = element;
        if (dynamicCells.containsKey(index)) {
            DynamicCellContent dynamicCell = dynamicCells.remove(index);
            dynamicCells.put(nextIndex, dynamicCell);
        }
    }

    // TODO make private
    public static int calculateIndex(final int index, final Field.ConstantDirection direction) {
        int result;
        switch (topLinePosition) {
            case NONE:
            case TOP:
            case BOTTOM:
                result = index + direction.getDeltaX() + (direction.getDeltaY() * topLineSize);
                break;
            case LEFT:
            case RIGHT:
                result = index + direction.getDeltaY() + (direction.getDeltaX() * topLineSize);
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

    public static void addDynamicElement(
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
        assert (!dynamicCells.containsKey(index));
        if (direction != Direction.STATIC) {
            dynamicCells.put(index, new DynamicCellContent(direction));
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
        LinkedList<Integer> collisions = new LinkedList<>(dynamicCells.keySet());
        while (!collisions.isEmpty()) {
            int cellIndex = collisions.removeFirst();
            assert (cells[cellIndex] != null);
            assert (dynamicCells.get(cellIndex) != null);
            assert (dynamicCells.get(cellIndex).direction != Direction.STATIC);
            doHandleCollision(collisions, cellIndex, levelGamePlay);
        }
    }

    /**
     * DONE Issue1 : Watch out for GameElement that does not move (Direction.STATIC)
     *      -> ignore collision handling
     * DONE Issue2 : DynamicElements can collide in circle -> move all and no collision handling
     * TODO Issue3 : DynamicElement can point at each other and in that case pass each other
     *      without collision -> not handled right now
     * TODO Issue4 : DynamicElement can end-up in same cell (started from different places)
     *      -> most difficult to solve -> not handled right now
     *
     * @param collisions contains list of all collisions that still have to be handled
     * @param dynamicCellIndex is index of cell that is moving and which possible collision is handled
     * @param levelGamePlay gameplay of this level
     */
    private static void doHandleCollision(
        final LinkedList<Integer> collisions,
        final int dynamicCellIndex,
        final LevelGamePlay levelGamePlay
    ) {
        final DynamicCellContent dynamicCell = dynamicCells.remove(dynamicCellIndex);
        assert (dynamicCell != null);
        final GameElement dynamicElement = cells[dynamicCellIndex];
        assert (dynamicElement != null);
        final ConstantDirection dynamicDirection = dynamicCell.direction;
        cells[dynamicCellIndex] = null;

        final int staticCellIndex = calculateIndex(dynamicCellIndex, dynamicDirection);
        GameElement staticElement = cells[staticCellIndex];
        if (staticElement == null) { // No collision or direction == STATIC
            doHandleNoCollision(dynamicCell, dynamicElement, staticCellIndex);
            return;
        }

        // Check if it collides with another dynamic element (remove it from collisions-list in case it does)
        ConstantDirection staticDirection = Direction.STATIC;
        if (collisions.remove((Integer) staticCellIndex)) {
            // Check if staticElement collides with dynamicElement (directions are opposite)
            final DynamicCellContent staticCell = dynamicCells.get(staticCellIndex);
            staticDirection = staticCell.direction;
            if(staticDirection.reverse() != dynamicDirection) {
                doHandleCollision(collisions, staticCellIndex, levelGamePlay);

                // element can be moved by handling its collision, so determine element again
                staticElement = cells[staticCellIndex];
                if (staticElement == null) { // Collision element moved so no collision
                    doHandleNoCollision(dynamicCell, dynamicElement, staticCellIndex);
                    return;
                }
            }
        }

        // Determine collision type
        final ElementCollision dynamicCollision = levelGamePlay.determineElementCollision(dynamicElement, staticElement);
        final ElementCollision staticCollision = levelGamePlay.determineElementCollision(staticElement, dynamicElement);

        // Store information for dynamic-element
        ElementCollisionData dynamicElementCollisionData = ElementCollisionData.createInstance(
                Action.NONE,
                dynamicCellIndex,
                dynamicElement,
                dynamicDirection,
                dynamicCollision
        );

        // Store information for collision-element
        ElementCollisionData staticElementCollisionData = ElementCollisionData.createInstance(
                Action.NONE,
                staticCellIndex,
                staticElement,
                staticDirection,
                staticCollision
        );

        // Determine if static will be moved by collision
        final int nextStaticCellIndex = dynamicCollision.determineNextStaticCellIndex(
                dynamicElementCollisionData,
                staticElementCollisionData
        );

        // Check if static moves
        if (nextStaticCellIndex != staticCellIndex) {
            // Static will move =>
            //   check if it moves to a cell that contains a dynamic-element
            if (collisions.remove((Integer) nextStaticCellIndex)) {
                // TODO: Issue5: what happens if the dynamic element did not move?
                doHandleCollision(collisions, nextStaticCellIndex, levelGamePlay);
            }
        }

        final ElementEffect effect = levelGamePlay.determineElementEffect(dynamicCollision, dynamicElement, staticElement);

        // Handle collision
        ElementCollisionData.List list = dynamicCollision.handleCollision(
                dynamicElementCollisionData,
                staticElementCollisionData
        );
        for (ElementCollisionData data : list) {
            data.getAction().execute(data);
            ElementCollisionData.releaseInstance(data);
        }
        ElementCollisionData.List.releaseInstance(list);

        effect.execute();
    }

    private static void doHandleNoCollision(
        final DynamicCellContent dynamicCell,
        final GameElement dynamicElement,
        final int nextDynamicCellIndex
    ) {
        cells[nextDynamicCellIndex] = dynamicElement;
        dynamicCells.put(nextDynamicCellIndex, dynamicCell);
    }

    private static void resize(final ConstantSize externalSize) {
        arraySize.set(externalSize.getWidth() + 2, externalSize.getHeight() + 2);
        createCells();
    }

    private static void createCells() {
        cells = new GameElement[arraySize.getWidth() * arraySize.getHeight()];
        dynamicCells.clear();
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

    private static class DynamicCellContent {
        public DynamicCellContent(final ConstantDirection direction) {
            this.direction = direction;
        }
        public Field.ConstantDirection direction;
    }

    private static GameElement[] cells;
    private static Map<Integer, DynamicCellContent> dynamicCells = new TreeMap<>();
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