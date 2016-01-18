# Xara
"Simple game"-engine (Java) based on JavaFX

The goal of this project the create a very simple game-engine that can be used to create very simple games/puzzles such as Minesweeper, Boulder dash, Fort Apocalypse or Pacman.

Aim is that creating a level with specific game-play is as simple as possible.

It is clearly a project in progress so there is a lot todo:

 *  Introduce ball (including bounce-horizontal and bounce-vertical)
 *  Introduce Neutral element-collision and use it as default in Field.java and SimpleLevel1.java
 *  Make resulting fusion-direction part of fusion
 *  Introduce Field.RelativePosition as derived from ConstantDirection
 *  Introduce move Field in multiple directions at once (use Direction)
 *  Bounce one frame delay (move element as well in frame it bounces)
 *  Rename Field.TopLinePosition with a clearer name
 *  Test all Field.TopLinePosition's with FigureGameElement
 *  Remove render-method from Field
 *  Reconnect Widener again and solve issues
 *  Introduce two (figure)GameElements connected to each other moving together (like a snake)
 *  Introduce speed (currently everything moves with the same speed)
 *  Introduce in-between render-frames (with interpolation)
 *  Improve rendering to high frame-rate (50 FPS)
 *    Possible solution: create bitmap with all static GameElement's and reuse bitmap</li>
 *  Introduce reading of bitmap to create level
 *  Introduce proper documentation (JavaDoc)
 *  Introduce proper count-down GameState to start level
 *  Make InputHandler level-dependent
 *  Introduce 3D-fields
 *  Re-introduce rendering of debug-grid
 *  FRAME_TIME (GameStateMachine) cannot be a static as it is GameState and level-dependent;
 *      should be set from outside
 *  Renderer contains some TODO's
