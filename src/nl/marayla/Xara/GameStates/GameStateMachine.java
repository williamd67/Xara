/*
 * Design:
 *   State contains:
 *       timer
 *       activeGameState
 */
package nl.marayla.Xara.GameStates;

import java.util.Timer;
import java.util.TimerTask;

public class GameStateMachine extends TimerTask {
    public GameStateMachine(final GameStateController controller) {
        this.controller = controller;
        timer = new Timer("XARA");
        timer.scheduleAtFixedRate(this, 0, FRAME_TIME);
    }

    public final void initialize() {
        active = nextGameState(null);
    }

    @Override
    public final void run() {
        if (active != null) {
            active.run();
            if (active.isFinished()) {
                active = nextGameState(active);
            }
        }
    }

    private GameState nextGameState(final GameState current) {
        GameState next = controller.next(current);
        if (next != null) {
            next.initialize();
        }
        return next;
    }

    private final Timer timer;
    private GameState active;
    private final GameStateController controller;

    private static final int FRAME_TIME = 150; // ms
}