package org.dru.psf.scene;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Todo: 1) Optimize/Reduce path to make a linear wise straighter flow.
 * Todo: 2) Create an abstract class out of this, e.g. abstract PathFinder extends Node
 * Mehods:
 *      setStart(globalPoint : Vector2D)
 *      setEnd(globalPoint : Vector2D);
 *      generatePath()
 *      getNext(globalPoint : Vector2D) <-- current
 * <p>
 * or similar.
 */
public class AStar extends Node {
    private final int tileWidth;
    private final int tileHeight;
    private final int width;
    private final int height;
    private final Set<Tile> closed;
    private final List<Tile> commonPath;
    private Tile startTile;
    private Tile endTile;

    public AStar(final int tileWidth, final int tileHeight, final int rowCount, final int columnCount) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        width = tileWidth * columnCount;
        height = tileHeight * rowCount;
        closed = ConcurrentHashMap.newKeySet();
        commonPath = new ArrayList<>();
        setAlpha(0.3);
    }

    @Override
    protected Shape createShape() {
        return new Rectangle(0, 0, width, height);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        graphics.setColor(Color.GRAY);
        for (int y = 0; y < height; y += tileHeight) {
            graphics.drawLine(0, y, width - 1, y);
        }
        for (int x = 0; x < width; x += tileWidth) {
            graphics.drawLine(x, 0, x, height - 1);
        }
        for (final Tile tile : closed) {
            graphics.fillRect(tile.x, tile.y, tileWidth, tileHeight);
        }
        graphics.setColor(Color.green);
        if (startTile != null) {
            graphics.fillRect(startTile.x, startTile.y, tileWidth, tileHeight);
        }
        graphics.setColor(Color.orange);
        for (final Tile tile : commonPath) {
            graphics.fillRect(tile.x + (tileWidth >> 2), tile.y + (tileHeight >> 2), tileWidth >> 1, tileHeight >> 1);
        }
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public List<Tile> getCommonPath() {
        return commonPath;
    }

    public Vector2 getNext(final List<Tile> path) {
        if (path.isEmpty()) {
            return null;
        } else {
            final Tile first = path.get(0);
            return new Vector2(first.x, first.y);
        }
    }

    public void removeFirst(final List<Tile> path) {
        if (!path.isEmpty()) {
            path.remove(0);
        }
    }

    private Tile createTile(final int x, final int y) {
        return new Tile((x / tileWidth) * tileWidth, (y / tileHeight) * tileHeight);
    }

    public void setStart(final int x, final int y) {
        startTile = createTile(x, y);
    }

    public void setStart(final double x, final double y) {
        setStart((int) Math.round(x), (int) Math.round(y));
    }

    public void setStartTile(final Vector2 v) {
        setStart(v.x(), v.y());
    }

    public Tile getEndTile() {
        return endTile;
    }

    public void setEnd(final int x, final int y) {
        endTile = createTile(x, y);
    }

    public void setEnd(final double x, final double y) {
        setEnd((int) Math.round(x), (int) Math.round(y));
    }

    public void setEndTile(final Vector2 v) {
        setEnd(v.x(), v.y());
    }

    public boolean isClosed(final int x, final int y) {
        return closed.contains(createTile(x, y));
    }

    public boolean isClosed(final double x, final double y) {
        return isClosed((int) Math.round(x), (int) Math.round(y));
    }

    public boolean isClosed(final Vector2 v) {
        return isClosed(v.x(), v.y());
    }

    public Tile addClosed(final int x, final int y) {
        final Tile tile = createTile(x, y);
        return closed.add(tile) ? tile : null;
    }

    public Tile addClosed(final double x, final double y) {
        return addClosed((int) Math.round(x), (int) Math.round(y));
    }

    public Tile addClosed(final Vector2 v) {
        return addClosed(v.x(), v.y());
    }

    public void removeClosed(final Tile tile) {
        closed.remove(tile);
    }

    public void removeClosed(final int x, final int y) {
        removeClosed(createTile(x, y));
    }

    public void removeClosed(final double x, final double y) {
        removeClosed((int) x, (int) y);
    }

    public void removeClosed(final Vector2 v) {
        removeClosed(v.x(), v.y());
    }

    private boolean inBound(final Tile tile) {
        return (tile.x >= 0 && tile.y >= 0) && (tile.x < width && tile.y < height);
    }

    private boolean isClosed(final Set<Tile> closed, final int x, final int y) {
        return closed.contains(new Tile(x, y));
    }

    public void findPath(final List<Tile> path) {
        if (closed.contains(startTile)) {
            return;
        }
        path.clear();
        final Set<Tile> open = new HashSet<>();
        open.add(startTile);
        final Set<Tile> closed = new HashSet<>(this.closed);
        Tile closest = startTile;
        while (!open.isEmpty()) {
            final Map<Tile, Double> costs = new HashMap<>();
            for (Tile tile : open) {
                costs.put(tile, tile.getCost());
            }
            Tile current = Collections.min(costs.entrySet(), Map.Entry.comparingByValue()).getKey();
            open.remove(current);
            closed.add(current);
            if (current.equals(endTile)) {
                while (!current.equals(startTile)) {
                    path.add(current);
                    current = current.parent;
                }
                if (path.isEmpty()) {
                    path.add(current);
                    return;
                }
                Collections.reverse(path);
                Tile a = startTile;
                for (int index = 0; index < path.size(); index++) {
                    final Tile b = path.get(index);
                    //   A*   *A   BX,AY   |    A   A    AX,BY
                    //    B   B            |   *B   B*
                    if (a.x != b.x && a.y != b.y) {
                        if (isClosed(closed, b.x, a.y) && !isClosed(closed, a.x, b.y)) {
                            path.add(index, createTile(a.x, b.y));
                        } else if (isClosed(closed, a.x, b.y) && !isClosed(closed, b.x, a.y)) {
                            path.add(index, createTile(b.x, a.y));
                        }
                    }
                    a = b;
                }
                return;
            }
            if (current.getDistanceTo(endTile) < closest.getDistanceTo(endTile)) {
                closest = current;
            }
            for (final Tile adjacent : current.getAdjacentTiles(this)) {
                if (closed.contains(adjacent) || !inBound(adjacent)) {
                    continue;
                }
                // Don't allow passing through vertical closed tiles.
                if (current.x != adjacent.x && current.y != adjacent.y) {
                    if (isClosed(closed, current.x, adjacent.y) && isClosed(closed, adjacent.x, current.y)) {
                        continue;
                    }
                }
                double newG = current.g + adjacent.getDistanceTo(current);
                if (newG < adjacent.g || !open.contains(adjacent)) {
                    adjacent.g = newG;
                    adjacent.h = adjacent.getDistanceTo(endTile);
                    adjacent.parent = current;
                    open.add(adjacent);
                }
            }
            if (open.isEmpty()) {
                endTile = closest;
                open.add(endTile);
            }
        }
    }

    public static final class Tile {
        private final int x;
        private final int y;
        private Tile parent;
        private double g;
        private double h;

        public Tile(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public double getCost() {
            return g + h;
        }

        public double getDistanceTo(final Tile other) {
            final int dx = other.x - x;
            final int dy = other.y - y;
            return Math.sqrt(dx * dx + dy * dy);
        }

        public List<Tile> getAdjacentTiles(AStar aStar) {
            return List.of(
                    new Tile(x + aStar.tileWidth, y + aStar.tileHeight),
                    new Tile(x + aStar.tileWidth, y),
                    new Tile(x + aStar.tileWidth, y - aStar.tileHeight),
                    new Tile(x, y - aStar.tileHeight),
                    new Tile(x - aStar.tileWidth, y - aStar.tileHeight),
                    new Tile(x - aStar.tileWidth, y),
                    new Tile(x - aStar.tileWidth, y + aStar.tileHeight),
                    new Tile(x, y + aStar.tileHeight)
            );
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof final Tile tile)) return false;
            if (x != tile.x) return false;
            return y == tile.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}
