package nl.marayla.Xara.Renderer;

public interface Canvas{
    void drawRectangle(ConstantRectangle rectangle, Paint paint);
    void drawCircle(ConstantPoint center, double radius, Paint paint);
    void drawText(String text, ConstantPoint center, Paint paint);
}