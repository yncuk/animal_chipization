package chipization.model.geometry;

public class Dot {
    public double x;
    public double y;

    public Dot(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dot dot = (Dot) o;
        return dot.x == x && dot.y == y;
    }
}