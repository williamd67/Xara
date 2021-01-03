package nl.marayla.Xara.InputHandler;

import nl.marayla.Xara.FigureControl;

public class InputHandler {
    public InputHandler(final FigureControl figureControl) {
        this.figureControl = figureControl;
    }

    public void handleEvent(final ConstantMotionEvent event) {
        this.figureControl.handleMotionEvent(event);
    }

    private final FigureControl figureControl;
 }