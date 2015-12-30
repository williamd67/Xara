package nl.marayla.Xara.Renderer;

import org.jetbrains.annotations.Contract;

public class Rectangle implements ConstantRectangle {
    public Rectangle() {
        set(0, 0, 0, 0);
    }

    public Rectangle(double left, double top, double right, double bottom) {
        set(left, top, right, bottom);
    }

    public final void set(final ConstantRectangle other) {
        set(other.getLeft(), other.getTop(), other.getRight(), other.getBottom());
    }

    public final void set(final double left, final double top, final double right, final double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public final double getLeft() { return left; }
    public final double getTop() { return top; }
    public final double getRight() { return right; }
    public final double getBottom() { return bottom; }

    @Contract(pure = true)
    public final double width() { return right - left; }

    @Contract(pure = true)
    public final double height() { return bottom - top; }

    @Contract(pure = true)
    public final double centerX() { return (left + right) / 2; }

    @Contract(pure = true)
    public final double centerY() { return (top + bottom) / 2; }

    @Contract(" -> !null")
    public final ConstantPoint center() { return new Point(centerX(), centerY()); }

    @Contract(pure = true)
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantRectangle)) {
            return false;
        }
        ConstantRectangle other = (ConstantRectangle) o;
        return (
                (left == other.getLeft())
                && (top == other.getTop())
                && (right == other.getRight())
                && (bottom == other.getBottom())
        );
    }

    @Contract(" -> fail")
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Contract(pure = true)
    @Override
    public final String toString() {
        return "Rectangle (" + left + ", " + top + ", " + right + ", " + bottom + ")";
    }

    private double left = 0;
    private double top = 0;
    private double right = 0;
    private double bottom = 0;
}