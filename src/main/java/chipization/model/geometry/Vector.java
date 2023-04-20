package chipization.model.geometry;

public class Vector {
    double x;
    double y;

    public Vector(Dot d1, Dot d2) {
        this.x = d2.x - d1.x;
        this.y = d2.y - d1.y;
    }

    public static double cross(Vector v1, Vector v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }
}
