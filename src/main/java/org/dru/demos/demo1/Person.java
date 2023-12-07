package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.AStar;
import org.dru.psf.scene.Display;
import org.dru.psf.scene.Tree;
import org.dru.psf.scene.Vector2;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public abstract class Person extends Tree {
    public static final Vector2 TILE_CENTER = new Vector2(Demo.TILE_WIDTH >> 1, Demo.TILE_HEIGHT >> 1);
    protected final Head head;
    protected final AStar aStar;
    protected final List<AStar.Tile> path;
    protected AStar.Tile closed;
    protected Vector2 targetPos;

    public Person(final Color color, final AStar aStar) {
        head = new Head(color);
        this.aStar = aStar;
        final Rectangle2D headBounds = head.getShape().getBounds2D();
        head.translatePosition(-headBounds.getCenterX(), -headBounds.getCenterY());
        addChild(head);
        path = createPathList();
    }

    @Override
    protected final void update() {
        // Disintegrate from the world.
        if (closed != null) {
            aStar.removeClosed(closed);
        }
        //
        final Vector2 currentPos = getGlobalPosition();
        final Vector2 endPosition = getEndPosition();
        if (endPosition != null) {
            // Find path!
            aStar.setStartTile(currentPos);
            aStar.setEndTile(endPosition);
            aStar.findPath(path);
        }
        // Move along.
        final Vector2 tp = aStar.getNext(path);
        if (tp != null) {
            targetPos = tp.sum(TILE_CENTER);
        }
        if (targetPos != null) {
            head.setLookAt(endPosition);
            head.setFocus(1);
            final Vector2 diff = targetPos.difference(currentPos);
            final double length = diff.length();
            diff.scale(1.0 / length);
            diff.scale(Application.delta() * 200, Application.delta() * 200);
            translateGlobalPosition(diff);
            if (length < 1.0) {
                setGlobalPosition(targetPos);
                aStar.removeFirst(path);
                targetPos = null;
                onReachedEndPosition();
            }
        } else {
            head.setFocus(0);
        }
        // Claim space in the world.
        closed = aStar.addClosed(getGlobalPosition());
    }

    public void setPositionToTile(final int row, final int column) {
        setGlobalPosition(Demo.TILE_WIDTH * column + TILE_CENTER.x(), Demo.TILE_HEIGHT * row + TILE_CENTER.y());
    }

    protected abstract List<AStar.Tile> createPathList();

    protected abstract Vector2 getEndPosition();

    protected abstract void onReachedEndPosition();
}
