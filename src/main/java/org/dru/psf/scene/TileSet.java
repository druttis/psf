package org.dru.psf.scene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileSet extends Node {
    private final int tileWidth;
    private final int tileHeight;
    private final BufferedImage image;
    private final int columnCount;
    private final int rowCount;
    private final int tileCount;

    public TileSet(final int tileWidth, final int tileHeight, final BufferedImage image) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.image = image;
        columnCount = image.getWidth() / tileWidth;
        rowCount = image.getHeight() / tileHeight;
        tileCount = columnCount * rowCount;
    }

    @Override
    protected Shape createShape() {
        return new Rectangle(image.getWidth(), image.getHeight());
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        graphics.drawImage(image, 0, 0, null);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void paint(final Graphics2D graphics, final int x, final int y, final int tileIndex) {
        final int row = tileIndex / columnCount;
        final int column = tileIndex - row * columnCount;
        final int tileX = column * tileWidth;
        final int tileY = row * tileHeight;
        graphics.drawImage(image,
                x, y, x + tileWidth, y + tileHeight,
                tileX, tileY, tileX + tileWidth, tileY + tileHeight,
                null);
    }
}
