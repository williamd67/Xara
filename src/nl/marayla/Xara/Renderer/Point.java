package nl.marayla.Xara.Renderer;

public class Point implements ConstantPoint {
    public Point() {
        set(0, 0);
    }

    public Point (double x, double y) {
        set(x, y);
    }

    public final void set(final ConstantPoint other) {
        set(other.getX(), other.getY());
    }

    public final void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public final double getX() { return x; }

    public final double getY() { return y; }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConstantPoint)) {
            return false;
        }
        ConstantPoint other = (ConstantPoint) o;
        return ((x == other.getX()) && (y == other.getY()));
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return "Point (" + x + ", " + y + ")";
    }

    private double x = 0;
    private double y = 0;
}