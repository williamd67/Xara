package nl.marayla.Xara;

import nl.marayla.Xara.GameElements.FigureGameElement;
import nl.marayla.Xara.ElementEffects.ElementEffect;
import nl.marayla.Xara.InputHandler.ConstantMotionEvent;

public class Figure implements FigureInfo, FigureControl {
    public class IncreaseScore implements ElementEffect {
        public IncreaseScore(final int value) {
            this.value = value;
        }

        @Override
        public final void execute() {
            player.increaseScore(value);
        }

        private final int value;
    }

    public class IncreaseLife implements ElementEffect {
        @Override
        public final void execute() {
            player.addXara();
        }
    }

    public class DecreaseLife implements ElementEffect {
        @Override
        public final void execute() {
            player.removeXara();
            invulnerableFrames = INVULNERABLE_FRAMES;
        }
    }

    public Figure(final Player player) {
        this.player = player;
    }

    public final void initialize(final Field.ConstantPosition minArea, final Field.ConstantPosition maxArea) {
        player.initialize();

        this.minArea.set(minArea);
        this.maxArea.set(maxArea);
        decreaseFrames = DECREASE_FRAMES;
    }

    @Override
    public final boolean isInvulnerable() {
        return (invulnerableFrames > 0);
    }

    @Override
    public final boolean willDecreaseSoon() {
        return (decreaseFrames < WILL_DECREASE_SOON_FRAMES);
    }

    public final FigureGameElement getFigureGameElement() {
        return figureGameElement;
    }

    public final void setFigureGameElement(final FigureGameElement figureGameElement) {
        this.figureGameElement = figureGameElement;
    }

    /*
     * Implement FigureControl
     */
    @Override
    public final void handleMotionEvent(final ConstantMotionEvent event) {
        switch(event.getAction()) {
            case ACTION_NONE:
            case ACTION_DOWN:
            case ACTION_UP:
            default:
                // do nothing
                break;
            case ACTION_LEFT:
                moveState = MoveState.MOVE_LEFT;
                break;
            case ACTION_RIGHT:
                moveState = MoveState.MOVE_RIGHT;
                break;
        }
    }

    private enum MoveState {
        MOVE_LEFT, MOVE_RIGHT, INACTIVE
    }
    private MoveState moveState = MoveState.INACTIVE;

    private void move() {
        switch(moveState) {
            case MOVE_LEFT:
                figureGameElement.moveLeft();
                break;
            case MOVE_RIGHT:
                figureGameElement.moveRight();
                break;
            case INACTIVE:
            default:
                figureGameElement.stopMoving();
                break;
        }
        moveState = MoveState.INACTIVE;
    }

    public final void nextFrame() {
        move();
        decreaseFrames--;
        if (decreaseFrames == 0) {
            decreaseFrames = DECREASE_FRAMES; // TODO Decrease frames should be part of GameElement
        }
        if (invulnerableFrames > 0) {
            invulnerableFrames--;
        }
    }

    private final Field.Position minArea = new Field.Position(0, 0); // TODO Reintroduce these values
    private final Field.Position maxArea = new Field.Position(0, 0); // TODO Reintroduce these values
    private final Player player;
    private FigureGameElement figureGameElement; // TODO Create array of figureGameElements
    private int decreaseFrames;
    private int invulnerableFrames;
    private static final int DECREASE_FRAMES = 50;
    private static final int WILL_DECREASE_SOON_FRAMES = 10;
    private static final int INVULNERABLE_FRAMES = 10;
}