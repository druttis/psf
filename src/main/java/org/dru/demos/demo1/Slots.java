package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.Node;

import java.awt.*;

public class Slots extends Node {
    private static final Color[] COLORS = {
            new Color(95, 95, 95),
            new Color(0, 0, 159),
            new Color(0, 159, 0),
            new Color(0, 159, 159),
            new Color(159, 0, 0),
            new Color(159, 0, 159),
            new Color(159, 159, 0),
            new Color(159, 159, 159),
            new Color(0, 0, 0),
            new Color(0, 0, 255),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(255, 0, 0),
            new Color(255, 0, 255),
            new Color(255, 255, 0),
            new Color(255, 255, 255)
    };

    private static final Color BG_COLOR = new Color(0, 0, 0, 63);
    private final double[] targetAlphas = new double[16];
    private final double[] alphas = new double[16];

    @Override
    protected Shape createShape() {
        return new Rectangle(0, 0, 16, 16);
    }

    @Override
    protected void update() {
        final double delta = Application.delta();
        for (int index = 0; index < 16; index++) {
            final double dist = targetAlphas[index] - alphas[index];
            if (dist < 0.01) {
                alphas[index] = targetAlphas[index];
            } else {
                alphas[index] += dist * delta;
            }
        }
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        final RenderingHints saveHints = graphics.getRenderingHints();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics.setColor(BG_COLOR);
        graphics.fill(getShape());
        int index = 0;
        for (int y = 0; y < 16; y += 4) {
            for (int x = 0; x < 16; x += 4) {
                graphics.setColor(alpha(COLORS[index], alphas[index]));
                graphics.fillRect(x, y, 4, 4);
                index++;
            }
        }
        graphics.setRenderingHints(saveHints);
    }

    public double getAlpha(final int index) {
        return targetAlphas[index];
    }

    public void setAlpha(final int index, final double alpha) {
        targetAlphas[index] = Math.max(0.0, Math.min(1.0, alpha));
    }

    private static Color alpha(final Color c, final double a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (a * 255.0));
    }
}
