package nl.marayla.Xara;

import nl.marayla.Xara.InputHandler.InputHandler;
import nl.marayla.Xara.Levels.Level;

import nl.marayla.Xara.LevelPlugins.SimpleLevel1;
import org.jetbrains.annotations.Contract;

final class LevelManager {
    public LevelManager(final Player player) {
        figure = new Figure(player);
    }

    @Contract(" -> !null")
    public InputHandler createInputHandler() {
        return new InputHandler(figure);
    }

    public void initialize() {
        difficulty = 0;
    }

    public Level nextLevel() {
        Level active;
        switch (difficulty) {
            case 0:
                active = new SimpleLevel1(figure);
//              active = new BouncingBallLevel(field, figure);
                break;
            case 1:
                active = new SimpleLevel1(figure);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        active.initialize();
        difficulty++;
        return active;
    }
    private int difficulty;
    private Figure figure;
}