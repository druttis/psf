package org.dru.psf.ui;

import org.dru.psf.scene.Tree;
import org.dru.psf.scene.Vector2;

import java.awt.*;

public abstract class Widget extends Tree {
    private int width;
    private int height;

    @Override
    protected Shape createShape() {
        doLayout(width, height);
        return super.createShape();
    }

    public final Point getLocation(final Point dest) {
        final Vector2 position = getPosition();
        dest.setLocation(position.x(), position.y());
        return dest;
    }

    public final Point getLocation() {
        return getLocation(new Point());
    }

    public final int getX() {
        return getLocation().x;
    }

    public final int getY() {
        return getLocation().y;
    }

    public final Widget setLocation(final int x, final int y) {
        setPosition(x, y);
        return this;
    }

    public final Widget setLocation(final Point p) {
        return setLocation(p.x, p.y);
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final Dimension getSize(final Dimension dest) {
        dest.setSize(width, height);
        return dest;
    }

    public final Dimension getSize() {
        return new Dimension(width, height);
    }

    public final void setSize(final int newWidth, final int newHeight) {
        final int oldWidth = width;
        final int oldHeight = height;
        width = newWidth;
        height = newHeight;
        if (newWidth != oldWidth || newHeight != oldHeight) {
            invalidateShape();
            invalidateLocalTransform();
        }
    }

    protected abstract Dimension preferredSize(Dimension dest);

    protected abstract void doLayout(int width, int height);
}
