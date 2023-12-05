package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Head extends Tree {
    private BufferedImage image;
    private Eye leftEye;
    private Eye rightEye;
    private Slots slots;
    private double remaining;
    private boolean open;

    public Head() {
        image = new BufferedImage(33, 39, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gr = image.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setColor(Color.black);
        gr.fillOval(0, 0, 33, 39);
        gr.setColor(Color.orange);
        gr.fillOval(1, 1, 31, 37);
        gr.dispose();
        leftEye = new Eye();
        leftEye.setPosition(-10, -5);
        rightEye = new Eye();
        rightEye.setPosition(10, -5);
        slots = new Slots();
        slots.setAlign(0.5, 0.5);
        slots.setPosition(0.0, 10.0);
        addChild(leftEye);
        addChild(rightEye);
//        addChild(slots);
    }

    @Override
    protected Shape createShape() {
        final Path2D path = (Path2D) super.createShape();
        path.append(new Ellipse2D.Double(-17, -20, 33, 39), false);
        return path;
    }

    @Override
    protected void update() {
        super.update();
        remaining -= Application.delta();
        if (remaining <= 0.0) {
            open = !open || Application.nextDouble() < 0.9;
            remaining = (open ? 1.0 : 0.2);
            leftEye.setOpen(open);
            rightEye.setOpen(open);
        }
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        graphics.drawImage(image, -17, -20, null);
        super.paint(graphics);
    }

    public double getSlotsAlpha(final int index) {
        return slots.getAlpha(index);
    }

    public void setSlotsAlpha(final int index, final double alpha) {
        slots.setAlpha(index, alpha);
    }

    public void setLookAt(final Vector2 newTargetPosition) {
        final Vector2 useTargetPosition = (newTargetPosition != null ? newTargetPosition : getGlobalPosition());
        leftEye.setLookAt(useTargetPosition);
        rightEye.setLookAt(useTargetPosition);
    }

    public void setFocus(final double focus) {
        leftEye.setFocus(focus);
        rightEye.setFocus(focus);
    }
}
