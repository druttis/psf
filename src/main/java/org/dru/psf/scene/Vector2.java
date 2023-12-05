package org.dru.psf.scene;

import java.awt.geom.Point2D;
import java.util.StringJoiner;

public class Vector2 {
    private double x;
    private double y;

    public Vector2(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(final Vector2 v) {
        this(v.x(), v.y());
    }

    public Vector2(final Point2D p) {
        this(p.getX(), p.getY());
    }

    public Vector2() {
        this(0.0, 0.0);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Vector2 vector)) return false;
        if (Double.compare(x, vector.x) != 0) return false;
        return Double.compare(y, vector.y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Vector2.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .toString();
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double distanceSq(final Vector2 v) {
        final double dx = v.x - x;
        final double dy = v.y - y;
        return dx * dx + dy * dy;
    }

    public double distance(final Vector2 v) {
        return Math.sqrt(distanceSq(v));
    }

    public double lengthSq() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public Vector2 set(final double x, final double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(final Vector2 v) {
        return set(v.x, v.y);
    }

    public Vector2 translate(final double x, final double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 translate(final Vector2 v) {
        return translate(v.x, v.y);
    }

    public Vector2 scale(final double x, final double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 scale(final Vector2 v) {
        return scale(v.x, v.y);
    }

    public Vector2 scale(final double s) {
        return scale(s, s);
    }

    public Vector2 sum(final Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 difference(final Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 rotate(final double radians) {
        final double c = Math.cos(radians);
        final double s = Math.sin(radians);
        return set(c * x - s * y, s * x + c * y);
    }
}
