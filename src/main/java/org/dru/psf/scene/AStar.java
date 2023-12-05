package org.dru.psf.scene;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Todo: 1) Optimize/Reduce path to make a linear wise straighter flow.
 * Todo: 2) Create an abstract class out of this, e.g. abstract PathFinder extends Node
 * Mehods:
 *      setStart(globalPoint : Vector2D)
 *      setEnd(globalPoint : Vector2D);
 *      generatePath()
 *      getNext(globalPoint : Vector2D) <-- current
 *
 *      or similar.
 */
public class AStar extends Node {
    private final int tileWidth;
    private final int tileHeight;
    private final int width;
    private final int height;
    private final Set<Tile> open;
    private final Set<Tile> closed;
    private final List<Tile> path;
    private Tile start;
    private Tile end;

    public AStar(final int tileWidth, final int tileHeight, final int columnCount, final int rowCount) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        width = tileWidth * columnCount;
        height = tileHeight * rowCount;
        open = new HashSet<>();
        closed = new HashSet<>();
        path = new ArrayList<>();
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
        if (start != null) {
            graphics.fillRect(start.x, start.y, tileWidth, tileHeight);
        }
        graphics.setColor(Color.orange);
        for (final Tile tile : path) {
            graphics.fillRect(tile.x + (tileWidth >> 2), tile.y + (tileHeight >> 2), tileWidth >> 1, tileHeight >> 1);
        }
    }

    public Vector2 getNext(final Vector2 source) {
        if (path.isEmpty()) {
            return null;
        } else {
            final Tile first = path.get(0);
            final Vector2 result = new Vector2(first.x + (tileWidth >> 1), first.y + (tileHeight >> 1));
            final double distance = result.distance(source);
            if (distance < tileWidth * 0.1 || distance < tileHeight * 0.1) {
                if (path.size() > 1) {
                    path.remove(0);
                }
            }
            return result;
        }
    }

    public void removeFirst() {
        if (!path.isEmpty()) {
            path.remove(0);
        }
    }

    private Tile createTile(final int x, final int y) {
        return new Tile((x / tileWidth) * tileWidth, (y / tileHeight) * tileHeight);
    }

    public void setStart(final int x, final int y) {
        start = createTile(x, y);
    }

    public void setStart(final double x, final double y) {
        setStart((int) Math.round(x), (int) Math.round(y));
    }

    public void setStart(final Vector2 v) {
        setStart(v.x(), v.y());
    }

    public Tile getEnd() {
        return end;
    }

    public void setEnd(final int x, final int y) {
        end = createTile(x, y);
    }

    public void setEnd(final double x, final double y) {
        setEnd((int) Math.round(x), (int) Math.round(y));
    }

    public void setEnd(final Vector2 v) {
        setEnd(v.x(), v.y());
    }

    public void addClosed(final int x, final int y) {
        closed.add(createTile(x, y));
    }

    public void addClosed(final double x, final double y) {
        addClosed((int) Math.round(x), (int) Math.round(y));
    }

    public void addClosed(final Vector2 v) {
        addClosed(v.x(), v.y());
    }

    public void removeClosed(final int x, final int y) {
        closed.remove(new Tile((x / tileWidth) * tileWidth, (y / tileHeight) * tileHeight));
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

    private boolean isClosed(final int x, final int y) {
        return closed.contains(new Tile(x, y));
    }

    public void findPath() {
        if (closed.contains(start)) {
            return;
        }
        path.clear();
        open.clear();
        open.add(start);
        final Set<Tile> closed = new HashSet<>(this.closed);
        while (!open.isEmpty()) {
            final Map<Tile, Double> costs = new HashMap<>();
            for (Tile tile : open) {
                costs.put(tile, tile.getCost());
            }
            Tile current = Collections.min(costs.entrySet(), Map.Entry.comparingByValue()).getKey();
            open.remove(current);
            closed.add(current);
            if (current.equals(end)) {
                while (!current.equals(start)) {
                    path.add(current);
                    current = current.parent;
                }
                if (path.size() < 1) {
                    path.add(current);
                }
                Collections.reverse(path);
                Tile a = current;
                for (int index = 0; index < path.size(); index++) {
                    final Tile b = path.get(index);
                    //   A*   *A   BX,AY   |    A   A    AX,BY
                    //    B   B            |   *B   B*
                    if (a.x != b.x && a.y != b.y) {
                        if (isClosed(b.x, a.y) && !isClosed(a.x, b.y)) {
                            path.add(index, createTile(a.x, b.y));
                        } else if (isClosed(a.x, b.y) && !isClosed(b.x, a.y)) {
                            path.add(index, createTile(b.x, a.y));
                        }
                    }
                    a = b;
                }
                return;
            }
            for (final Tile adjacent : current.getAdjacentTiles()) {
                if (closed.contains(adjacent) || !inBound(adjacent)) {
                    continue;
                }
                if (current.x != adjacent.x && current.y != adjacent.y) {
                    if (isClosed(current.x, adjacent.y) && isClosed(adjacent.x, current.y)) {
                        continue;
                    }
                }
                double newG = current.g + adjacent.getDistanceTo(current);
                if (newG < adjacent.g || !open.contains(adjacent)) {
                    adjacent.g = newG;
                    adjacent.h = adjacent.getDistanceTo(end);
                    adjacent.parent = current;
                    open.add(adjacent);
                }
            }
        }
    }

    public final class Tile {
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
            final int dx = Math.abs(other.x - x);
            final int dy = Math.abs(other.y - y);
            return Math.sqrt(dx * dx + dy * dy);
        }

        public List<Tile> getAdjacentTiles() {
            return List.of(
                    new Tile(x + tileWidth, y + tileHeight),
                    new Tile(x + tileWidth, y),
                    new Tile(x + tileWidth, y - tileHeight),
                    new Tile(x, y - tileHeight),
                    new Tile(x - tileWidth, y - tileHeight),
                    new Tile(x - tileWidth, y),
                    new Tile(x - tileWidth, y + tileHeight),
                    new Tile(x, y + tileHeight)
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
