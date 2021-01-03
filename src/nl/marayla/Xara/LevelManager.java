package nl.marayla.Xara;

import nl.marayla.Xara.InputHandler.InputHandler;
import nl.marayla.Xara.LevelPlugins.*;
import nl.marayla.Xara.Levels.Level;

final class LevelManager {
    public LevelManager(final Player player) {
        figure = new Figure(player);
    }

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
                active = new BrickLevel(figure);
//                active = new SimpleLevel1(figure);
//                active = new BouncingBallLevel(figure);
//                active = new FileBasedLevel(figure, "level1.bmp");
//                active = new TestLevel(figure);
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
    private final Figure figure;
}