package nl.marayla.Xara;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.canvas.*;

import nl.marayla.Xara.InputHandler.ConstantMotionEvent;
import nl.marayla.Xara.InputHandler.MotionEvent;
import nl.marayla.Xara.Renderer.*;
import nl.marayla.Xara.Renderer.Paint;

import java.util.EnumSet;
import java.util.Set;

class MultiplePressedKeysEventHandler implements EventHandler<KeyEvent> {

    public MultiplePressedKeysEventHandler(final MultiKeyEventHandler handler) {
        this.multiKeyEventHandler = handler;
    }

    public void handle(final KeyEvent event) {
        final KeyCode code = event.getCode();

        if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
            buffer.add(code);
            multiKeyEventHandler.handle(multiKeyEvent);
        } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
            buffer.remove(code);
        }
        event.consume();
    }

    @FunctionalInterface
    public interface MultiKeyEventHandler {
        void handle(final MultiKeyEvent event);
    }

    public class MultiKeyEvent {
        public boolean isPressed(final KeyCode key) {
            return buffer.contains(key);
        }
    }

    private final Set<KeyCode> buffer = EnumSet.noneOf(KeyCode.class);
    private final MultiKeyEvent multiKeyEvent = new MultiKeyEvent();

    private final MultiKeyEventHandler multiKeyEventHandler;
}

public class XaraFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    class MyCanvas extends javafx.scene.canvas.Canvas implements nl.marayla.Xara.Renderer.Canvas {
        public MyCanvas(double width, double height) {
            super(width, height);
        }

        public void onDraw() {
            if (xaraView != null) {
                xaraView.onDraw(this);
            }

        }
        public void onSizeChanged() {
            if (xaraView != null) {
                xaraView.onSizeChanged((int) getWidth(), (int) getHeight());
            }
        }

        public void setXaraView(XaraView xaraView) {
            assert (xaraView != null);
            this.xaraView = xaraView;
            onSizeChanged();
        }

        @Override
        public void drawRectangle(ConstantRectangle rectangle, Paint paint) {
            GraphicsContext gc = this.getGraphicsContext2D();
            if(paint.getStyle() == Paint.Style.FILL) {
                gc.setFill(transformColorToFXColor(paint.getColor()));
                gc.fillRect(rectangle.getLeft(), rectangle.getTop(), rectangle.getRight(), rectangle.getBottom());
            }
        }

        @Override
        public void drawCircle(ConstantPoint center, double radius, Paint paint) {
            GraphicsContext gc = this.getGraphicsContext2D();
            if(paint.getStyle() == Paint.Style.FILL) {
                gc.setFill(transformColorToFXColor(paint.getColor()));
                gc.fillOval(center.getX() - radius, center.getY() - radius, radius, radius);
            }
        }

        @Override
        public void drawText(String text, ConstantPoint center, Paint paint) {
            GraphicsContext gc = this.getGraphicsContext2D();
//            gc.setFill(transformColorToFXColor(paint.getColor()));
            gc.setFill(javafx.scene.paint.Color.color(1, 1, 1));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(text, center.getX(), center.getY());
        }

        private javafx.scene.paint.Color transformColorToFXColor(final nl.marayla.Xara.Renderer.Color color) {
            return javafx.scene.paint.Color.color(
                    (double)color.getRed() / 255,
                    (double)color.getGreen() / 255,
                    (double)color.getBlue() / 255
            );
        }
        private XaraView xaraView = null;
    }

    @Override
    public void start(Stage primaryStage) {
        final MyCanvas canvas = new MyCanvas(250, 250);

        Group root = new Group();
        Scene scene = new Scene(root, 300, 275, javafx.scene.paint.Color.BLUE);
        initKeyEventHandler(scene);

        root.getChildren().add(canvas);
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        canvas.widthProperty().addListener( (observable) -> { canvas.onSizeChanged(); } );
        canvas.heightProperty().addListener( (observable) -> { canvas.onSizeChanged(); } );

        primaryStage.setTitle("Xara");
        primaryStage.setScene(scene);
        primaryStage.show();

        //create a timeline for moving the circle
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                canvas.onDraw();
            }
        };

        xaraApplication = new XaraApplication();
        canvas.setXaraView(xaraApplication.getView());

        timer.start();
        timeline.play();
    }

    private void initKeyEventHandler(Scene scene) {
        final MultiplePressedKeysEventHandler keyHandler = new MultiplePressedKeysEventHandler(
            (multiKeyEvent) -> {
                MotionEvent motionEvent = new MotionEvent();
                if (multiKeyEvent.isPressed(KeyCode.LEFT)  || multiKeyEvent.isPressed(KeyCode.A)) {
                    motionEvent.set(ConstantMotionEvent.MotionEventAction.ACTION_LEFT);
                } else if (multiKeyEvent.isPressed(KeyCode.RIGHT) || multiKeyEvent.isPressed(KeyCode.D)) {
                    motionEvent.set(ConstantMotionEvent.MotionEventAction.ACTION_RIGHT);
                }
                xaraApplication.getView().onMotionEvent(motionEvent);

                if (multiKeyEvent.isPressed(KeyCode.UP) || multiKeyEvent.isPressed(KeyCode.W)) {
                    motionEvent.set(ConstantMotionEvent.MotionEventAction.ACTION_UP);
                } else if (multiKeyEvent.isPressed(KeyCode.DOWN) || multiKeyEvent.isPressed(KeyCode.S)) {
                    motionEvent.set(ConstantMotionEvent.MotionEventAction.ACTION_DOWN);
                }
                xaraApplication.getView().onMotionEvent(motionEvent);
            }
        );

        scene.setOnKeyPressed(keyHandler);
        scene.setOnKeyReleased(keyHandler);
    }

    private XaraApplication xaraApplication = null;
}
