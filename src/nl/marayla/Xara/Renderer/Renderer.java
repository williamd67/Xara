package nl.marayla.Xara.Renderer;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.Platform.XaraLog;

import nl.marayla.Xara.Levels.Level;
import nl.marayla.Xara.Player;

public class Renderer {
    public final void initialize(final Player player, final Level level) {
        this.player = player; // TODO Remove all
        this.level = level;
    }

    public final void renderFrame(final Canvas canvas) {
        renderInfoArea(canvas);
        renderMainArea(canvas);
        renderInputArea(canvas);
    }

    public final void sizeChanged(final int width, final int height) {
        mainArea.set(0, 0, width, (height * 9) / 10);
        infoArea.set(0, mainArea.getBottom() + 1, width, height);
        inputArea.set(0, infoArea.getTop(), infoArea.height(), infoArea.getBottom());
    }

    private ConstantRectangle hotspot(final int hotspotArea) {
        switch(hotspotArea) {
            case H_MAINAREA:
                return mainArea;
            case H_INFOAREA:
                return infoArea;
            case H_INPUTAREA:
                return inputArea;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void renderMainArea(final Canvas canvas) {
        XaraLog.log.v(getClass().getName() + ".renderMainArea", "Render " + (renders++));
        level.render(new RenderData(canvas, hotspot(H_MAINAREA), level.getSize()));

/*
        String text = "";
        if (frameCount < GAME_RUNNING) {
            // Show 3, 2, 1
            if (frameCount <= GAME_THREE) {
                text = "3";
            }
            else if (frameCount <= GAME_TWO) {
                text = "2";
            }
            else if (frameCount <= GAME_ONE) {
                text = "1";
            }
            else {
                // TODO Throw case not handled exception
                assert(false);
            }
        }
        else if (frameCount >= GAME_FINISHED) {
            if ((frameCount & 1) == 1) {
                int score = player.getScore();
                if (score >= highScore) {
                    text = "HIGHSCORE!\n\nScore " + score;
                }
                else {
                    text = "GAME OVER\n\nScore " + score;
                }
            }
        }
        if (text.length() > 0) {
            area.inset (0, area.height() / 5);
            canvas.drawText (text, area.centerX(), area.centerY(), paint);
        }
        */
    }
    private int renders = 0;

    private void renderInfoArea(final Canvas canvas) {
        ConstantRectangle area = hotspot(H_INFOAREA);
        // Clear
        Paint paint = new Paint(Paint.Flag.DITHER_FLAG);
        paint.setColor(Color.rgb(102, 153, 51));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRectangle(area, paint);

        double height = area.height();

        Point center = new Point(area.centerX(), area.centerY());
        paint.setColor(Color.rgb(0, 128, 255));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center, (height / 2) - 1, paint);

        String text = String.format("%d", player.getXaras());
        paint.setColor(Color.BLACK);
        canvas.drawText(text, center, paint);

        center.set(area.getRight() - ((7 * height) / 2), area.getTop() + height / 2);
        paint.setColor(Color.rgb(255, 255, 0));
        canvas.drawCircle(center, (height / 2) - 1, paint);

//      text = String.format ("%d", field.level.getBonuses()); // TODO move to level
//      paint.setColor(Color.BLACK);
//      canvas.drawText (text, center.x, center.y, paint);

//      Rect textRect = area; // TODO move to level
//      text = "Time: " + level.getTime();
//      canvas.drawText (text, textRect.left + height, textRect.centerY(), paint);
    }

    private void renderInputArea(final Canvas canvas) {
        /*
        Rect area = hotspot (H_INPUTAREA);
        switch (inputState) {
            case INPUT_IDLE:
                paint.setColor(0x80F0F0F0);
                canvas.drawCircle(area.centerX(), area.centerY(), area.width() / 4, paint);
                break;
            case INPUT_ACTIVE:
                paint.setColor(0x80FFFFFF);
                canvas.drawCircle(area.centerX(), area.centerY(), area.width() / 2, paint);
                break;
            case INPUT_LEFT:
                paint.setColor(0x40FF0000);
                canvas.drawCircle(area.centerX(), area.centerY(), area.width() / 2, paint);
                break;
            case INPUT_RIGHT:
                paint.setColor(0x400000FF);
                canvas.drawCircle(area.centerX(), area.centerY(), area.width() / 2, paint);
                break;
            default:
                // Todo Throw case not handled exception
                assert(false);
                break;
        }
        inputHandler.renderFeedback(canvas);
         */
    }

    private Player player; // TODO Remove
    private Level level; // TODO Remove

    private final Rectangle mainArea = new Rectangle();
    private final Rectangle infoArea = new Rectangle();
    private final Rectangle inputArea = new Rectangle();

    // Hotspots
    private static final int H_MAINAREA = 10;
    private static final int H_INFOAREA = 11;
    private static final int H_INPUTAREA = 12;
}