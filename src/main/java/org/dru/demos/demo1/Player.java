package org.dru.demos.demo1;

import org.dru.psf.scene.AStar;
import org.dru.psf.scene.Display;
import org.dru.psf.scene.Vector2;

import java.awt.*;
import java.util.List;

public class Player extends Person {
    public Player(final AStar aStar) {
        super(new Color(0, 191, 127), aStar);
    }

    @Override
    protected List<AStar.Tile> createPathList() {
        return aStar.getCommonPath();
    }

    @Override
    protected Vector2 getEndPosition() {
        return Display.getMouse();
    }

    @Override
    protected void onReachedEndPosition() {
    }
}
