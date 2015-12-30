package nl.marayla.Xara.Renderer;

public interface ConstantRectangle {
    double getLeft();
    double getTop();
    double getRight();
    double getBottom();
    double width();
    double height();
    double centerX();
    double centerY();
    ConstantPoint center();
}