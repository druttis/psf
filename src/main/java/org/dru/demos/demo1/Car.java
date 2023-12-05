package org.dru.demos.demo1;

import org.dru.psf.scene.Node;
import org.dru.psf.scene.TileSet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Car extends Node {
    private static int floor(final double x) {
        return (int) (x < 0.0 ? x - 1.0 : x);
    }

    private static double fraction(final double x) {
        return x - floor(x);
    }

    private static double fraction(final double x, final double y) {
        return fraction(x / y) * y;
    }

    private static int tileIndex(final double x) {
        return (int) ((x * Math.PI / 360.0 * 12.0) % 48);
    }

    private final TileSet tileSet;
    private double direction;

    public Car() {
        setAlign(0.5, 0.5);
        try {
            tileSet = new TileSet(100, 100, ImageIO.read(Car.class.getResource("/TAXI_CLEAN_ALLD0000-sheet.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Shape createShape() {
        return new Rectangle(0, 0, 100, 100);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        tileSet.paint(graphics, 0, 0, tileIndex(direction));
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(final double direction) {
        this.direction = direction;
    }
}
