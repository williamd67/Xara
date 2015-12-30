package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Platform.XaraLog;

import java.util.ArrayList;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import org.jetbrains.annotations.Contract;

public class ElementCollisionResult {
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
            final Field.Action action,
            final int index,
            final GameElement element,
            final Field.ConstantDirection direction,
            final ElementCollision collision
    )
    {
        return getInstance().set(action, index, element, direction, collision);
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
        // else do nothing; carbage collector will clean-up
    }

    public final Field.Action getAction() {
        return action;
    }

    public final int getIndex() {
        return index;
    }

    public final GameElement getElement() {
        return element;
    }

    public final Field.ConstantDirection getDirection() {
        return direction;
    }

    public final ElementCollision getCollision() {
        return collision;
    }

    public final void setAction(final Field.Action action) {
        this.action = action;
    }

    public final void setIndex(final int index) {
        this.index = index;
    }

    public final void setDirection(final Field.ConstantDirection direction) {
        this.direction = direction;
    }

    private static ElementCollisionData getInstance() {
        if (poolFree > 0) {
            XaraLog.log.d("Data.getInstance", "poolFree " + poolFree + " pool " + pool[poolFree-1]);
            return pool[--poolFree];
        }
        return new ElementCollisionData();
    }

    private ElementCollisionData set(final ElementCollisionData other) {
        return set(other.getAction(), other.getIndex(), other.getElement(), other.getDirection(), other.getCollision());
    }

    private ElementCollisionData reset() {
        return set(Field.Action.NONE, 0, null, Field.Direction.STATIC, null);
    }

    private ElementCollisionData set(
            final Field.Action action,
            final int index,
            final GameElement element,
            final Field.ConstantDirection direction,
            final ElementCollision collision
    ) {
        this.action = action;
        this.index = index;
        this.element = element;
        this.direction = direction;
        this.collision = collision;

        return this;
    }

    private Field.Action action;
    private int index;
    private GameElement element;
    private Field.ConstantDirection direction;
    private ElementCollision collision;

    private ElementCollisionData() {
        reset();
    }

    private static final int INITIAL_POOL_SIZE = 10;
    private static int poolFree = INITIAL_POOL_SIZE;
    private static ElementCollisionData[] pool;
    static {
        pool = new ElementCollisionData[INITIAL_POOL_SIZE];
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool[i] = new ElementCollisionData();
        }
    }
}