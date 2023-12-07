package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.StaticSprite;
import org.dru.psf.scene.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Head extends StaticSprite {
    private Eye leftEye;
    private Eye rightEye;
    private Slots slots;
    private double remaining;
    private boolean open;

    public Head(final Color color) {
        final BufferedImage image = new BufferedImage(33, 39, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gr = image.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setColor(Color.black);
        gr.fillOval(0, 0, 33, 39);
        gr.setColor(color);
        gr.fillOval(1, 1, 31, 37);
        gr.dispose();
        setImage(image);
        leftEye = new Eye();
        leftEye.setPosition(-3, 7);
        rightEye = new Eye();
        rightEye.setPosition(18, 7);
        slots = new Slots();
        slots.setPosition(17 - 8, 24.0);
        addChild(leftEye);
        addChild(rightEye);
//        addChild(slots);
    }

    public Head() {
        this(new Color(255, 127, 0));
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

    public void setVisibleEyes(final boolean s) {
        leftEye.setVisible(s);
        rightEye.setVisible(s);
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
