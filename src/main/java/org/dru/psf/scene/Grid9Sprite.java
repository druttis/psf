package org.dru.psf.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Grid9Sprite extends Sprite {
    private int width;
    private int height;
    private int top;
    private int left;
    private int bottom;
    private int right;
    private final int[] sx = new int[4];
    private final int[] sy = new int[4];
    private final int[] dx = new int[4];
    private final int[] dy = new int[4];

    public Grid9Sprite() {
    }

    public Grid9Sprite(final BufferedImage image) {
        super(image);
    }

    public Grid9Sprite(final URL url) {
        super(url);
    }

    @Override
    protected Shape createShape() {
        final BufferedImage image = getImage();
        if (image != null) {
            sx[1] = dx[1] = left;
            sy[1] = dy[1] = top;
            sx[3] = image.getWidth();
            sy[3] = image.getHeight();
            sx[2] = sx[3] - right;
            sy[2] = sy[3] - bottom;
            dx[3] = width;
            dy[3] = height;
            dx[2] = dx[3] - right;
            dy[2] = dy[3] - right;
        }
        return super.createShape();
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        final BufferedImage image = getImage();
        if (image != null) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    graphics.drawImage(image, dx[c], dy[r], dx[c + 1], dy[r + 1], sx[c], sy[r], sx[c + 1], sy[r + 1], null);
                }
            }
        }
    }

    @Override
    public Dimension getSize(final Dimension dest) {
        dest.setSize(width, height);
        return dest;
    }

    public Grid9Sprite setSize(final int newWidth, final int newHeight) {
        final int oldWidth = width;
        final int oldHeight = height;
        width = newWidth;
        height = newHeight;
        if (newWidth != oldWidth || newHeight != oldHeight) {
            invalidateShape();
            invalidateLocalTransform();
        }
        return this;
    }

    public Grid9Sprite setSize(final Dimension size) {
        return setSize(size.width, size.height);
    }

    public Insets getInsets(final Insets dest) {
        dest.set(top, left, bottom, right);
        return dest;
    }

    public Insets getInsets() {
        return new Insets(top, left, bottom, right);
    }

    public Grid9Sprite setInsets(final int newTop, final int newLeft, final int newBottom, final int newRight) {
        top = newTop;
        left = newLeft;
        bottom = newBottom;
        right = newRight;
        return this;
    }

    public Grid9Sprite setInsets(final Insets newInsets) {
        return setInsets(newInsets.top, newInsets.left, newInsets.bottom, newInsets.right);
    }
}
