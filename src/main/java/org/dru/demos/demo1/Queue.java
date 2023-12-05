package org.dru.demos.demo1;

import org.dru.psf.scene.Grid9Sprite;

public class Queue extends Grid9Sprite {
    public Queue() {
        super(Queue.class.getResource("/queue.png"));
        setInsets(7, 7, 7, 7);
        setSize(40, 100);
    }
}
