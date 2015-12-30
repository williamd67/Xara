package nl.marayla.Xara.InputHandler;

public interface ConstantMotionEvent {
    enum MotionEventAction {
        ACTION_NONE,
        ACTION_DOWN,
        ACTION_LEFT,
        ACTION_RIGHT,
        ACTION_UP
    }

    MotionEventAction getAction();
}