package org.dru.psf.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class StaticSprite extends Sprite {
    public StaticSprite() {
    }

    public StaticSprite(final BufferedImage image) {
        super(image);
    }

    public StaticSprite(final URL url) {
        super(url);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        final BufferedImage image = getImage();
        if (image != null) {
            graphics.drawImage(image, 0, 0, null);
        }
    }

    @Override
    public Dimension getSize(final Dimension dest) {
        final BufferedImage image = getImage();
        if (image != null) {
            dest.setSize(image.getWidth(), image.getHeight());
        } else {
            dest.setSize(0.0, 0.0);
        }
        return dest;
    }
}
