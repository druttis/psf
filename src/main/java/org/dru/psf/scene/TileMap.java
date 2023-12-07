package org.dru.psf.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class TileMap extends Node {
    private final TileSet tileSet;
    private final int rowCount;
    private final int columnCount;
    private final int tileCount;
    private final int[] tiles;
    private final int width;
    private final int height;
    private final BufferedImage offScreenImage;

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
        width = columnCount * tileSet.getTileWidth();
        height = rowCount * tileSet.getTileHeight();
        offScreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintTileMap(0, 0, rowCount - 1, columnCount - 1);
    }

    @Override
    protected Shape createShape() {
        return new Rectangle(width, height);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        paintTileMap(graphics, 0, 0, rowCount - 1, columnCount - 1);
//        graphics.drawImage(offScreenImage, 0, 0, null);
    }

    public int getTile(final int row, final int column) {
        return tiles[row * columnCount + column];
    }

    public void setTile(final int row, final int column, final int tile) {
        final int index = row * columnCount + column;
        final int old = tiles[index];
        tiles[index] = tile;
        if (tile != old) {
            paintTile(row, column);
        }
    }

    private void paintTile(final Graphics2D graphics, final int x, final int y, final int row, final int column) {
        tileSet.paint(graphics, x, y, getTile(row, column));
    }

    private void paintTileMap(final Graphics2D graphics, final int firstRow, final int firstColumn,
                              final int lastRow, final int lastColumn) {
        int y = 0;
        for (int row = firstRow; row <= lastRow; row++, y += tileSet.getTileHeight()) {
            int x = 0;
            for (int column = firstColumn; column <= lastColumn; column++, x += tileSet.getTileWidth()) {
                paintTile(graphics, x, y, row, column);
            }
        }
    }

    private void paintTileMap(final int firstRow, final int firstColumn, final int lastRow, final int lastColumn) {
        final Graphics2D graphics = offScreenImage.createGraphics();
        try {
            paintTile(graphics, firstRow, firstColumn, lastRow, lastColumn);
        } finally {
            graphics.dispose();
        }
    }

    private void paintTile(final int row, final int column) {
        paintTileMap(row, column, row, column);
    }
}
