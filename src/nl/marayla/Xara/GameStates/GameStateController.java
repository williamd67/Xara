package nl.marayla.Xara.GameStates;

public interface GameStateController {
    GameState next(GameState current);
}