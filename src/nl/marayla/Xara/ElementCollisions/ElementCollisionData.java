package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.Platform.XaraLog;

import java.util.ArrayList;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

public class ElementCollisionData {
    public final static class List extends ArrayList<ElementCollisionData> {
        // TODO introduce pool
        @Contract(" -> !null")
        public static List getInstance() {
            return new List();
        }

        public static void releaseInstance(final List list) {
            // for now do nothing; in the future move to pool
        }
    }

    public static ElementCollisionData createInstance() {
            return getInstance();
        }

    public static ElementCollisionData createInstance(
        final int index,
        final GameElement element,
        final Field.ConstantMovingCell movingCell,
        final boolean isColliding
    )
    {
        return getInstance().set(index, element, movingCell, null, isColliding);
    }

    public static ElementCollisionData createInstance(final ElementCollisionData other) {
        return getInstance().set(other);
    }

    public static void releaseInstance(final ElementCollisionData data) {
        if (poolFree < INITIAL_POOL_SIZE) {
            XaraLog.log.d("Data.releaseInstance", "poolFree " + poolFree + " data " + data);
            data.reset();
            pool[poolFree++] = data;
        }
        // else do nothing; garbage collector will clean-up
    }

    public final int getIndex() {
        return index;
    }

    public final GameElement getElement() {
        return element;
    }

    public final Field.ConstantDirection getDirection() {
        return movingCell != null ? movingCell.getDirection() : Field.Direction.STATIC;
    }

    public final int[] getConnections() {
        return movingCell != null ? movingCell.getConnections() : NO_CONNECTIONS;
    }

    public final ElementCollision getCollision() {
        return collision;
    }

    public final boolean isColliding() {
        return this.isColliding;
    }

    public final void determineCollision(final LevelGamePlay levelGamePlay, GameElement staticElement) {
        collision = levelGamePlay.determineElementCollision(element, staticElement);
    }

    public final int calculateNextIndex() {
        return Field.calculateIndex(index, getDirection());
    }

    private static ElementCollisionData getInstance() {
        if (poolFree > 0) {
            XaraLog.log.d("Data.getInstance", "poolFree " + poolFree + " pool " + pool[poolFree-1]);
            return pool[--poolFree];
        }
        return new ElementCollisionData();
    }

    private ElementCollisionData set(final ElementCollisionData other) {
        return set(
                other.getIndex(),
                other.getElement(),
            other.getMovingCell(),
                other.getCollision(),
            other.isColliding()
        );
    }

    private ElementCollisionData reset() {
        return set(0, null, null, null, false);
    }

    private ElementCollisionData set(
        final int index,
        final GameElement element,
        final Field.ConstantMovingCell movingCell,
        final ElementCollision collision,
        final boolean isColliding
    ) {
        this.index = index;
        this.element = element;
        this.movingCell = movingCell;
        this.collision = collision;
        this.isColliding = isColliding;

        return this;
    }

    private Field.ConstantMovingCell getMovingCell() {
        return movingCell;
    }

    private int index;
    private GameElement element;
    private Field.ConstantMovingCell movingCell;
    private ElementCollision collision;
    private boolean isColliding;

    private ElementCollisionData() {
        reset();
    }

    private static final int[] NO_CONNECTIONS = {};

    private static final int INITIAL_POOL_SIZE = 10;
    private static int poolFree = INITIAL_POOL_SIZE;
    private static final ElementCollisionData[] pool;
    static {
        pool = new ElementCollisionData[INITIAL_POOL_SIZE];
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool[i] = new ElementCollisionData();
        }
    }
}