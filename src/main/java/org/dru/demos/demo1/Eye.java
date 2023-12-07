package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.Node;
import org.dru.psf.scene.StaticSprite;
import org.dru.psf.scene.Vector2;

import java.awt.image.BufferedImage;

public class Eye extends StaticSprite {
    private BufferedImage globe;
    private BufferedImage lid;
    private Node pupilCenter;
    private StaticSprite pupil;
    private Vector2 targetPosition;
    private double focus;
    private double targetFocus;

    public Eye() {
        super();
        setImage(globe = readImage("/globe.png"));
        lid = readImage("/lid.png");
        pupilCenter = new Node();
        pupilCenter.setPosition(6, 6);
        pupil = new StaticSprite("/pupil.png");
        setLookAt(null);
        setFocus(1.0);
        addChild(pupilCenter);
        pupilCenter.addChild(pupil);
    }

    @Override
    protected void update() {
        super.update();
        pupil.setGlobalRotation(0.0, false);
        if (focus < targetFocus) {
            focus += Application.delta() * 5.0;
            if (focus > targetFocus) {
                focus = targetFocus;
            }
        } else if (focus > targetFocus) {
            focus -= Application.delta() * 5.0;
            if (focus < targetFocus) {
                focus = targetFocus;
            }
        }
        final Vector2 centerPosition = pupilCenter.getGlobalPosition();
        final Vector2 difference = targetPosition.difference(centerPosition);
        double length = Math.min(4, difference.length() * 0.1);
        length *= focus;
        final Vector2 p = new Vector2(length, 0.0).rotate(difference.angle());
        pupil.setGlobalPosition(centerPosition.sum(p));
    }

    void setOpen(final boolean open) {
        setImage(open ? globe : lid);
        pupil.setVisible(open);
    }

    public void setLookAt(final Vector2 newTargetPosition) {
        targetPosition = (newTargetPosition != null ? newTargetPosition : this.getGlobalPosition());
    }

    public void setFocus(final double newFocus) {
        targetFocus = newFocus;
    }
}
