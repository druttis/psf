package org.dru.demos.demo2;

import org.apache.log4j.BasicConfigurator;
import org.dru.demos.demo1.Head;
import org.dru.psf.Application;
import org.dru.psf.scene.Display;
import org.dru.psf.scene.Tree;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManyThings {
    private static final int TILE_WIDTH = 40;
    private static final int TILE_HEIGHT = 40;
    private static final int COLUMN_COUNT = 1024 / TILE_WIDTH;
    private static final int ROW_COUNT = 768 / TILE_WIDTH;
    private static final int WIDTH = TILE_WIDTH * COLUMN_COUNT;
    private static final int HEIGHT = TILE_HEIGHT * ROW_COUNT;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        final Tree scene = new Tree();
        final List<Head> heads = new ArrayList<>();
        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COLUMN_COUNT; c++) {
                final Head head = new Head();
                head.setPosition(c * TILE_WIDTH + (TILE_WIDTH >> 1), r * TILE_HEIGHT + (TILE_HEIGHT >> 1));
                heads.add(head);
                scene.addChild(head);
            }
        }
        Display.setRoot(scene);
        Display.show("ManyThings (Yes, the eyes blinks)", WIDTH, HEIGHT, new Color(0, 0, 0));
        Application.attach(() -> {
            for (final Head head : heads) {
                head.rotate(Application.delta() * 360, true);
                head.setLookAt(Display.getMouse());
            }
        });
        Application.start();
    }
}
