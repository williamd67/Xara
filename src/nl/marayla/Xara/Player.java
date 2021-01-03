package nl.marayla.Xara;

public class Player {
    public final void initialize() {
        xaras = START_XARAS;
        score = 0;
    }
    public final void increaseScore(final int increase) {
        score += increase;
    }
    public final int getScore() {
        return score;
    }
    public final void addXara() {
        if (xaras < MAXIMUM_XARAS) {
            xaras++;
        }
    }
    public final void removeXara() {
        if (xaras > 0) {
            xaras--;
        }
    }
    public final int getXaras() {
        return xaras;
    }

    public final boolean failed() {
        return xaras <= 0;
    }

    private int score;
    private int xaras;

    private static final int START_XARAS = 2;
    private static final int MAXIMUM_XARAS = 9;
}