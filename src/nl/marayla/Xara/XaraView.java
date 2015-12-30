package nl.marayla.Xara;

import nl.marayla.Xara.GameStates.GameStateMachine;
import nl.marayla.Xara.InputHandler.ConstantMotionEvent;
import nl.marayla.Xara.InputHandler.InputHandler;
import nl.marayla.Xara.Renderer.Canvas;
import nl.marayla.Xara.Renderer.Renderer;
import org.jetbrains.annotations.Contract;

public class XaraView {
    public XaraView(
        final Renderer renderer,
        final InputHandler inputHandler,
        final GameStateMachine gameStateMachine
    ) {
        assert (renderer != null);
        this.renderer = renderer;
        assert (inputHandler != null);
        this.inputHandler = inputHandler;
        assert (gameStateMachine != null);
        this.gameStateMachine = gameStateMachine;
        this.gameStateMachine.initialize();
    }

    protected void onDraw(final Canvas canvas) {
        renderer.renderFrame(canvas);
    }

    protected void onSizeChanged(final int width, final int height) {
        renderer.sizeChanged(width, height);
        gameStateMachine.initialize();
    }

    public void onMotionEvent(final ConstantMotionEvent event) {
        inputHandler.handleEvent(event);
    }

    private Renderer renderer = null;
    private InputHandler inputHandler = null;
    private GameStateMachine gameStateMachine = null;
}