package nl.marayla.Xara.InputHandler;

import org.jetbrains.annotations.Contract;

public class MotionEvent implements ConstantMotionEvent {
    public MotionEvent() {
        set(MotionEventAction.ACTION_NONE);
    }

    public MotionEvent (final MotionEventAction action) {
        set(action);
    }

    public final void set(final ConstantMotionEvent other) {
        set(other.getAction());
    }

    public final void set(final MotionEventAction action) {
        this.action = action;
    }

    @Contract(pure = true)
    public final MotionEventAction getAction() { return action; }

    @Contract(pure = true)
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantMotionEvent)) {
            return false;
        }
        ConstantMotionEvent other = (ConstantMotionEvent) o;
        return (action == other.getAction());
    }

    @Contract(" -> fail")
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public final String toString() {
        return "MotionEvent (" + action + ")";
    }

    private MotionEventAction action = MotionEventAction.ACTION_NONE;
}