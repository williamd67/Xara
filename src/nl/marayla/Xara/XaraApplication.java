package nl.marayla.Xara;

import nl.marayla.Xara.Renderer.Renderer;
import nl.marayla.Xara.Platform.XaraLog;
import nl.marayla.Xara.GameStates.GameState;
import nl.marayla.Xara.GameStates.GameStateId;
import nl.marayla.Xara.GameStates.GameStateController;
import nl.marayla.Xara.GameStates.GameStateMachine;
import nl.marayla.Xara.Levels.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Xara class is main-class for Xara-game-environment.
 * <p>Design:
 * </p>
 * <p>TODO Bug-fixes
 *  </p><br>
 * <p>TODO Clean-up
 *  <li>Field: Check if we can get rid of instanceof operator and use polymorphism instead</li>
 *  <li>CheckStyle: Re-enable JavaDoc check</li>
 * </p><br>
 *  @see XaraView
 *  @see Player
 *  @see Renderer
 *  @see LevelManager
 *  @see GameStateMachine
 */
public class XaraApplication implements GameStateController {
    /**
     * Determines the next <code>GameState</code> dependent on the current <code>GameState</code>.
     * @param   current     current <code>GameState</code>
     *                      which is used to determine next <code>GameState</code>
     * @return              returns the next <code>GameState</code> following current
     * @see    GameState
     */
    @Override
    public final GameState next(@Nullable final GameState current) {
        if (current == null) {
            return new InitializeGame(XaraGameStateId.INITIALIZED);
        }
        else {
            switch ((XaraGameStateId) (current.getId())) {
                case INITIALIZED:
                    return new RunGame(XaraGameStateId.RUNNING);
                case RUNNING:
                    return new FinishGame(XaraGameStateId.FINISHED);
                case FINISHED:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Initializes all the main-parts of Xara-game-environment.
     */
    class InitializeGame extends GameState {
        /**
         * Constructs InitializeGame.
         * @param   id      is <code>GameStateId</code> and
         *                  uniquely identifies this <code>GameState</code>
         */
        public InitializeGame(final GameStateId id) {
            super(id);
        }

        /**
         * Initializes all main-parts of Xara-game-environment.
         */
        @Override
        public void run() {
            levelManager.initialize();
            level = levelManager.nextLevel();
            renderer.initialize(player, level);
        }

        /**
         * As InitializeGame only runs once isFinished is always <code>true</code>.
         * @return      Always returns <code>true</code>
         */
        @Override
        public boolean isFinished() {
            return true;
        }
    }

    class RunGame extends GameState {
        /**
         * Constructs RunGame.
         * @param   id      is <code>GameStateId</code> and
         *                  uniquely identifies this <code>GameState</code>
         */
        public RunGame(final GameStateId id) {
            super(id);
        }

        @Override
        public void run() {
            XaraLog.log.v(getClass().getName() + ".run", "nextFrame " + (frames++));
            level.nextFrame();
            if (level.succeeded()) {
                level = levelManager.nextLevel();
            }
        }

        @Override
        public boolean isFinished() {
            return (level.failed() || player.failed());
        }

        private int frames = 0;
    }

    class FinishGame extends GameState {
        public FinishGame(final GameStateId id) {
            super(id);
        }

        @Override
        public void run() {
            int score = player.getScore();
            if (score >= highScore) {
                highScore = score;
            }
        }

        @Override
        public boolean isFinished() {
            return true;
        }
    }

    private enum XaraGameStateId implements GameStateId {
        INITIALIZED, RUNNING, FINISHED
    }

    public XaraApplication() {
        player = new Player();
        levelManager = new LevelManager(player);

        renderer = new Renderer();
        view = new XaraView(renderer, levelManager.createInputHandler(), new GameStateMachine(this));
    }

    public final XaraView getView() {
        assert (view != null);
        return view;
    }

    private int highScore;

    private final XaraView view;
    private final Player player;
    private final LevelManager levelManager;
    private final Renderer renderer;

    /**
     * level is current level.
     */
    private Level level;
}