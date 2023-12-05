package org.dru.psf.scene;

import java.awt.*;

public class TileMap extends Node {
    private final TileSet tileSet;
    private final int rowCount;
    private final int columnCount;
    private final int tileCount;
    private final int[] tiles;

    public TileMap(final TileSet tileSet, final int rowCount, final int columnCount) {
        this.tileSet = tileSet;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        tileCount = rowCount * columnCount;
        tiles = new int[tileCount];
    }

    @Override
    protected Shape createShape() {
        return new Rectangle(tileSet.getTileWidth() * columnCount, tileSet.getTileHeight() * rowCount);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        for (int row = 0; row < rowCount; row++) {
            final int y = row * tileSet.getTileHeight();
            for (int column = 0; column < columnCount; column++) {
                final int x = column * tileSet.getTileWidth();
                tileSet.paint(graphics, x, y, tiles[row * columnCount + column]);
            }
        }
    }
}
