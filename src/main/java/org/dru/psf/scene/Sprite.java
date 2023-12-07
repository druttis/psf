package org.dru.psf.scene;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public abstract class Sprite extends Node {
    public static BufferedImage readImage(final String path) {
        try {
            return ImageIO.read(Sprite.class.getResource(path));
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private BufferedImage image;

    public Sprite() {
    }

    public Sprite(final BufferedImage image) {
        setImage(image);
    }

    public Sprite(final String path) {
        this(readImage(path));
    }

    @Override
    protected Shape createShape() {
        final Dimension2D size = getSize();
        return new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight());
    }

    public final Dimension getSize() {
        return getSize(new Dimension());
    }

    public abstract Dimension getSize(Dimension dest);

    public final BufferedImage getImage() {
        return image;
    }

    public final void setImage(final BufferedImage newImage) {
        final BufferedImage oldImage = image;
        image = newImage;
        if (!Objects.equals(newImage, oldImage)) {
            invalidateShape();
            invalidateLocalTransform();
        }
    }
}
