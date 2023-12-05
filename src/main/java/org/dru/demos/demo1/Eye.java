package org.dru.demos.demo1;

import org.dru.psf.Application;
import org.dru.psf.scene.*;

public class Eye extends Tree {
    private static double lerp(final double source, final double target, final double fraction) {
        return source + (target - source) * fraction;
    }

    private static Vector2 lerp(final Vector2 source, final Vector2 target, final double fraction,
                                final Vector2 dest) {
        return dest.set(lerp(source.x(), target.x(), fraction), lerp(source.y(), target.y(), fraction));
    }

    private static Vector2 lerp(final Vector2 source, final Vector2 target, final double fraction) {
        return lerp(source, target, fraction, new Vector2());
    }

    private StaticSprite globe;
    private StaticSprite lid;
    private StaticSprite pupil;
    private Vector2 targetPosition;
    private double focus;
    private double targetFocus;

    public Eye() {
        globe = new StaticSprite(Eye.class.getResource("/globe.png"));
        lid = new StaticSprite(Eye.class.getResource("/lid.png"));
        lid.setVisible(false);
        pupil = new StaticSprite(Eye.class.getResource("/pupil.png"));
        setLookAt(null);
        setFocus(1.0);
        addChild(globe);
        addChild(lid);
        addChild(pupil);
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
        final Vector2 centerPosition = getGlobalPosition();
        final Vector2 difference = targetPosition.difference(centerPosition);
        double length = Math.min(4, difference.length() * 0.1);
        length *= focus;
        final Vector2 p = new Vector2(length, 0.0).rotate(difference.angle());
        pupil.setGlobalPosition(centerPosition.sum(p));
    }

    void setOpen(final boolean open) {
        globe.setVisible(open);
        lid.setVisible(!open);
        pupil.setVisible(open);
    }

    public void setLookAt(final Vector2 newTargetPosition) {
        targetPosition = (newTargetPosition != null ? newTargetPosition : this.getGlobalPosition());
    }

    public void setFocus(final double newFocus) {
        targetFocus = newFocus;
    }
}
