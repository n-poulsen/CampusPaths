package n.poulsen.campuspaths.model;

import java.util.Objects;

/**
 * <b>Coordinates</b> is an immutable representation of a coordinate.
 *
 * <b>Specification fields</b>:
 *   @spec.specfield x: the coordinates x axis value
 *   @spec.specfield y: the coordinates x axis value
 */
public class Coordinates {

    /**
     * This coordintes x and y values
     */
    private final double x, y;

    // Abstraction function:
    //    Coordinates c represents coordinates c = (x, y)
    //
    // Representation invariant for every Coordinates c:
    //    //
    //

    /**
     * Constructs a new coordinates
     *
     * @param x the x-axis value of this coordinate
     * @param y the y-axis value of this coordinate
     * @spec.effects Constructs a new Coordinate with x-axis value x and
     * y-axis value y.
     */
    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-axis value of this coordinate
     *
     * @return the x-axis value of this coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-axis value of this coordinate
     *
     * @return the y-axis value of this coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Standard equality operation.
     *
     * @param obj The object to be compared for equality.
     * @return true iff 'obj' is an instance of a Path and 'this' and 'obj' represent
     * the same path.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            Coordinates c = (Coordinates) obj;
            return c.x == this.x && c.y == this.y;
        }
        return false;
    }

    /**
     * Standard hashCode function.
     *
     * @return an int that all objects equal to this will also return.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}