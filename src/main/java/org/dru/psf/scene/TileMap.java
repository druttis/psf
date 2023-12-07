package org.dru.psf.scene;

import java.awt.*;
import java.util.Objects;

public class TileMap extends Node {
    private final TileSet tileSet;
    private final int rowCount;
    private final int columnCount;
    private final int tileCount;
    private final int[] tiles;

    public TileMap(final TileSet tileSet, final int rowCount, final int columnCount, final int[] tiles) {
        Objects.requireNonNull(tileSet, "tileSet");
        this.tileSet = tileSet;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        tileCount = rowCount * columnCount;
        Objects.requireNonNull(tiles, "tiles");
        if (tiles.length != tileCount) {
            throw new IllegalArgumentException("tiles.length differs from tileCount");
        }
        this.tiles = tiles;
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

    public int getTile(final int row, final int column) {
        return tiles[row * columnCount + column];
    }
}
