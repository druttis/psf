package org.dru.demos.demo1;

import org.apache.log4j.BasicConfigurator;
import org.dru.psf.Application;
import org.dru.psf.scene.*;

import java.awt.*;

public class Demo1 {
    private static final int TILE_WIDTH = 64;
    private static final int TILE_HEIGHT = 64;
    private static final int COLUMN_COUNT = 1024 / TILE_WIDTH;
    private static final int ROW_COUNT = 768 / TILE_WIDTH;
    private static final int WIDTH = TILE_WIDTH * COLUMN_COUNT;
    private static final int HEIGHT = TILE_HEIGHT * ROW_COUNT;

    private static double remaining = 0;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        final Tree scene = new Tree();
        final AStar aStar = new AStar(TILE_WIDTH, TILE_HEIGHT, COLUMN_COUNT, ROW_COUNT);
        for (int i = 0; i < 50; i++) {
            aStar.addClosed(Application.nextInt(WIDTH - 1), Application.nextInt(HEIGHT - 1));
        }
        final Head head = new Head();
        head.setPosition(TILE_WIDTH * 4 + 32, TILE_HEIGHT * 4 + 32);
        aStar.removeClosed(head.getPosition());
        scene.addChild(aStar);
        scene.addChild(head);
        final Car car = new Car();
        car.setPosition(200, 200);
        scene.addChild(car);
        Display.setRoot(scene);
        Display.show("Test", WIDTH, HEIGHT, new Color(0, 0, 0));
        Application.attach(() -> {
            car.setDirection(car.getDirection() + Application.delta() * 360.0);
            head.setLookAt(Display.getMouse());
            remaining -= Application.delta();
            if (remaining <= 0) {
                remaining = 0.1;
                aStar.setStart(head.getPosition());
                aStar.setEnd(Display.getMouse());
                aStar.findPath();
            }
            final Vector2 currentPos = head.getGlobalPosition();
            final Vector2 targetPos = aStar.getNext(currentPos);
            if (targetPos != null) {
                final Vector2 diff = targetPos.difference(currentPos);
                final double length = diff.length();
                if (length > 1.0) {
                    diff.scale(1.0 / length);
                    diff.scale(Application.delta() * 200, Application.delta() * 200);
                    head.translateGlobalPosition(diff);
                    head.setFocus(1);
                } else {
                    head.setGlobalPosition(targetPos);
                    head.setFocus(0);
                }
            } else {
                head.setFocus(0);
            }
        });
        Application.start();
    }
}
