package nl.marayla.Xara.GameStates;

public abstract class GameState {
    public GameState(final GameStateId id) {
        this.id = id;
    }

    public void initialize() {
    }

    public final GameStateId getId() {
        return id;
    }

    public abstract void run();
    public abstract boolean isFinished();

    private GameStateId id;
}