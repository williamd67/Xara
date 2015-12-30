package nl.marayla.Xara.ElementCollisions;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

// TODO Ensure that push does not push elements on top of each other
/*
 *  PUSH: <code>dynamic</code> pushes <code>static</code>
 *      PUSH will only be executed if field that <code>static</code> will be pushed into, is empty
 *      <code>static</code> moves in direction of <code>dynamic</code>
 *      <code>dynamic</code> moves to position of <code>static</code>
 */
public final class Push extends ElementCollision {
    public static final ElementCollision INSTANCE = new Push();

    @Override
    public ElementCollisionData.List handleCollision(
        final ElementCollisionData collider,
        final ElementCollisionData collideInto
    ) {
        ElementCollisionData.List list = ElementCollisionData.List.getInstance();

        collideInto.setAction(Field.Action.MOVE);
        collideInto.setDirection(collider.getDirection());
        list.add(collideInto);

        ElementCollisionData data = ElementCollisionData.createInstance(
                Field.Action.ADD,
                collider.getIndex(),
                collider.getElement(),
                collider.getDirection(),
                collider.getCollision()
        );
        list.add(data);

        collider.setAction(Field.Action.MOVE);
        list.add(collider);

        return list;
/*
        int elementNextIndex = Field.calculateIndex(staticIndex, collider.direction);
        if (Field.getElement(elementNextIndex) == null) {
            Field.moveElement(staticIndex, elementNextIndex);
            Field.addElement(staticIndex, collider.element, collider.direction);
        }
        else {
            Field.addElement(collider.index, collider.element, collider.direction);
        }
        */
    }

    @Contract(pure = true)
    @Override
    protected final boolean staticKeep(final ElementCollision collision) {
        return (collision == Eat.INSTANCE) || (collision == Eaten.INSTANCE) || (collision == Push.INSTANCE);
    }

    @Contract(value = "_ -> false", pure = true)
    @Override
    protected final boolean staticMoveStaticDirection(final ElementCollision collision) {
        return false;
    }

    @Contract(pure = true)
    @Override
    protected  final boolean staticMoveDynamicDirection(final ElementCollision collision) {
        return (collision == Bounce.INSTANCE) || (collision == Stick.INSTANCE);
    }

    private Push() {
    }
}