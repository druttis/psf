package org.dru.demos.demo1;

import org.apache.log4j.BasicConfigurator;
import org.dru.psf.Application;
import org.dru.psf.scene.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Demo {
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 64;
    public static final int COLUMN_COUNT = 16;
    public static final int ROW_COUNT = 13;
    public static final int WIDTH = TILE_WIDTH * COLUMN_COUNT;
    public static final int HEIGHT = TILE_HEIGHT * ROW_COUNT;

    public static final TileSet TILE_SET;
    public static final TileMap TILE_MAP;
    public static final AStar ASTAR;
    public static Player PLAYER;

    static {
        final BufferedImage image = new BufferedImage(TILE_WIDTH << 1, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(0, 0, 0));
        graphics.fillRect(0, 0, TILE_WIDTH, TILE_HEIGHT);
        graphics.setColor(new Color(191, 191, 191));
        graphics.fill3DRect(TILE_WIDTH, 0, TILE_WIDTH, TILE_HEIGHT, true);
        graphics.dispose();
        TILE_SET = new TileSet(TILE_WIDTH, TILE_HEIGHT, image);
        //
        TILE_MAP = new TileMap(TILE_SET, ROW_COUNT, COLUMN_COUNT, new int[]{
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1,
                1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1,
                1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1,
                1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1,
                1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1,
                1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
        });
        //
        ASTAR = new AStar(TILE_WIDTH, TILE_HEIGHT, ROW_COUNT, COLUMN_COUNT);
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int column = 0; column < COLUMN_COUNT; column++) {
                if (TILE_MAP.getTile(row, column) != 0) {
                    ASTAR.addClosed(column * TILE_WIDTH, row * TILE_HEIGHT);
                }
            }
        }
    }

    private static double remaining = 0;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        final Tree scene = new Tree();
        PLAYER = new Player(ASTAR);
        PLAYER.setPositionToTile(1, 1);
        final Robot robot1 = new Robot(ASTAR);
        robot1.setPositionToTile(1, 14);
        final Robot robot2 = new Robot(ASTAR);
        robot2.setPositionToTile(11, 1);
        final Robot robot3 = new Robot(ASTAR);
        robot3.setPositionToTile(11, 14);
        scene.addChild(TILE_MAP);
        scene.addChild(ASTAR);
        scene.addChild(robot1);
        scene.addChild(robot2);
        scene.addChild(robot3);
        scene.addChild(PLAYER);
        Display.setRoot(scene);
        Display.show("Test", WIDTH, HEIGHT, new Color(0, 0, 0));
        Application.start();
    }
}
