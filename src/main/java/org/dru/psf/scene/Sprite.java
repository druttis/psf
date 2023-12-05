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
    private BufferedImage image;

    public Sprite() {
        setAlign(0.5, 0.5);
    }

    public Sprite(final BufferedImage image) {
        this();
        setImage(image);
    }

    public Sprite(URL url) {
        this();
        try {
            setImage(ImageIO.read(url));
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
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
