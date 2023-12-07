package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.AStar;
import org.dru.psf.scene.Display;
import org.dru.psf.scene.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Robot extends Person {
    private Vector2 endPosition;

    public Robot(final AStar aStar) {
        super(new Color(255, 63, 63), aStar);
    }

    @Override
    protected List<AStar.Tile> createPathList() {
        return new ArrayList<>();
    }

    @Override
    protected Vector2 getEndPosition() {
        if (endPosition == null) {
            final int x = Application.nextInt(Demo.COLUMN_COUNT - 1) * Demo.TILE_WIDTH;
            final int y = Application.nextInt(Demo.ROW_COUNT - 1) * Demo.TILE_HEIGHT;
            final Vector2 v = new Vector2(x, y);
            if (!aStar.isClosed(x, y)) {
                endPosition = v;
            }
//           endPosition = Demo.PLAYER.getGlobalPosition();
        }
        return endPosition;
    }

    @Override
    protected void onReachedEndPosition() {
        endPosition = null;
    }
}
