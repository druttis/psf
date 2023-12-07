package org.dru.psf.scene;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Todo: Pry in Tree into Node
 * Why?
 * Example
 * Node node = new Grid9Sprite(jadda, jadda, jadda);
 * node.addChild(new StatixText());
 * node.addChild(new UserInputNode()); // since children has access to parents and siblings, easy to modify parent self.
 * <p>
 * Todo: Create more handlerMethods, e.g. doLayout etc.
 * Todo: Create a smart way to deal with createShape() when mixing child nodes and other things.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Node {
    private double positionX;
    private double positionY;
    private double scaleX;
    private double scaleY;
    private double rotation;
    private double shearX;
    private double shearY;
    private boolean visible;
    private double alpha;
    private boolean antialias;
    private Node clip;
    private transient Tree parent;
    private transient boolean validLocalTransform;
    private transient final AffineTransform localTransform;
    private transient boolean validGlobalTransform;
    private transient final AffineTransform globalTransform;
    private transient Shape shape;
    private transient Shape localShape;
    private transient Shape globalShape;
    private transient Composite composite;

    public Node() {
        localTransform = new AffineTransform();
        globalTransform = new AffineTransform();
        setScale(1.0, 1.0);
        setVisible(true);
        setAlpha(1.0);
    }

    public final Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }

    public final Node setPosition(final double newPositionX, final double newPositionY) {
        final double oldPositionX = positionX;
        final double oldPositionY = positionY;
        positionX = newPositionX;
        positionY = newPositionY;
        if (newPositionX != oldPositionX || newPositionY != oldPositionY) {
            invalidateLocalTransform();
        }
        return this;
    }

    public final Node setPosition(final Vector2 newPosition) {
        return setPosition(newPosition.x(), newPosition.y());
    }

    public final Node translatePosition(final double deltaX, final double deltaY) {
        return setPosition(positionX + deltaX, positionY + deltaY);
    }

    public final Node translatePosition(final Vector2 delta) {
        return translatePosition(delta.x(), delta.y());
    }

    public final Vector2 getGlobalPosition(final double localX, final double localY) {
        final Point2D point = new Point2D.Double(localX, localY);
        getGlobalTransformInternal().transform(point, point);
        return new Vector2(point);
    }

    public final Vector2 getGlobalPosition(final Vector2 local) {
        return getGlobalPosition(local.x(), local.y());
    }

    public final Vector2 getGlobalPosition() {
        return getGlobalPosition(0.0, 0.0);
    }

    public final Node setGlobalPosition(final double newPositionX, final double newPositionY) {
        final Tree parent = getParent();
        if (parent != null) {
            final Point2D point = new Point2D.Double(newPositionX, newPositionY);
            try {
                parent.getGlobalTransformInternal().inverseTransform(point, point);
            } catch (final NoninvertibleTransformException exception) {
                throw new RuntimeException(exception);
            }
            return setPosition(point.getX(), point.getY());
        } else {
            return setPosition(newPositionX, newPositionY);
        }
    }

    public final Node setGlobalPosition(final Vector2 newPosition) {
        return setGlobalPosition(newPosition.x(), newPosition.y());
    }

    public final Node translateGlobalPosition(final double deltaX, final double deltaY) {
        return setGlobalPosition(getGlobalPosition().translate(deltaX, deltaY));
    }

    public final Node translateGlobalPosition(final Vector2 delta) {
        return translateGlobalPosition(delta.x(), delta.y());
    }

    public final Vector2 getScale() {
        return new Vector2(scaleX, scaleY);
    }

    public final Node setScale(final double newScaleX, final double newScaleY) {
        final double oldScaleX = scaleX;
        final double oldScaleY = scaleY;
        scaleX = newScaleX;
        scaleY = newScaleY;
        if (newScaleX != oldScaleX || newScaleY != oldScaleY) {
            invalidateLocalTransform();
        }
        return this;
    }

    public final Node setScale(final Vector2 newScale) {
        return setScale(newScale.x(), newScale.y());
    }

    public final Node scale(final double deltaX, final double deltaY) {
        return setScale(scaleX + deltaX, scaleY + deltaY);
    }

    public final Node scale(final Vector2 delta) {
        return scale(delta.x(), delta.y());
    }

    public final Node scale(final double delta) {
        return scale(delta, delta);
    }

    public final double getRotation(final boolean asDegrees) {
        final double rotation = this.rotation;
        return asDegrees ? Math.toDegrees(rotation) : rotation;
    }

    public final Node setRotation(final double newRotation, final boolean usingDegrees) {
        final double useRotation = usingDegrees ? Math.toRadians(newRotation) : newRotation;
        final double oldRotation = rotation;
        rotation = useRotation;
        if (useRotation != oldRotation) {
            invalidateLocalTransform();
        }
        return this;
    }

    public final Node rotate(final double angle, final boolean inDegrees) {
        return setRotation(rotation + (inDegrees ? Math.toRadians(angle) : angle), false);
    }

    public final double getGlobalRotation(final boolean asDegrees) {
        double rotation = getRotation(asDegrees);
        Tree tree = getParent();
        while (tree != null) {
            rotation += tree.getRotation(asDegrees);
            tree = tree.getParent();
        }
        return rotation;
    }

    public final Node setGlobalRotation(final double newRotation, final boolean inDegrees) {
        final Tree parent = getParent();
        if (parent != null) {
            return setRotation(newRotation - parent.getGlobalRotation(false), inDegrees);
        } else {
            return setRotation(newRotation, inDegrees);
        }
    }

    public final Vector2 getShear() {
        return new Vector2(shearX, shearY);
    }

    public final Node setShear(final double newShearX, final double newShearY) {
        final double oldShearX = shearX;
        final double oldShearY = shearY;
        shearX = newShearX;
        shearY = newShearY;
        if (newShearX != oldShearX || newShearY != oldShearY) {
            invalidateLocalTransform();
        }
        return this;
    }

    public final Node setShear(final Vector2 newShear) {
        return setShear(newShear.x(), newShear.y());
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(final boolean newVisible) {
        visible = newVisible;
    }

    public final double getAlpha() {
        return alpha;
    }

    public final void setAlpha(final double newAlpha) {
        final double useAlpha = Math.max(0.0, Math.min(1.0, newAlpha));
        final double oldAlpha = alpha;
        alpha = useAlpha;
        if (useAlpha != oldAlpha) {
            invalidateComposite();
        }
    }

    public boolean isAntialias() {
        return antialias;
    }

    public void setAntialias(final boolean newAntialias) {
        final boolean oldAntialias = antialias;
        antialias = newAntialias;
        if (newAntialias != oldAntialias) {
            // TODO: something?
        }
    }

    public final Node getClip() {
        return clip;
    }

    public final void setClip(final Node newClip) {
        clip = newClip;
    }

    public final Tree getParent() {
        return parent;
    }

    final void setParent(final Tree parent) {
        this.parent = parent;
    }

    protected final void invalidateLocalTransform() {
        if (validLocalTransform) {
            validLocalTransform = false;
            invalidateGlobalTransform();
            invalidateLocalShape();
        }
    }

    final AffineTransform getLocalTransformInternal() {
        if (!validLocalTransform) {
            validLocalTransform = true;
            final Shape shape = getShape();
            final Rectangle2D bounds = shape.getBounds2D();
            localTransform.setToTranslation(positionX, positionY);
            localTransform.scale(scaleX, scaleY);
            localTransform.translate(bounds.getCenterX(), bounds.getCenterY());
            localTransform.rotate(rotation);
            localTransform.translate(-bounds.getCenterX(), -bounds.getCenterY());
            localTransform.shear(shearX, shearY);
        }
        return localTransform;
    }

    public final AffineTransform getLocalTransform(final AffineTransform dest) {
        dest.setTransform(getLocalTransformInternal());
        return dest;
    }

    public final AffineTransform getLocalTransform() {
        return getLocalTransform(new AffineTransform());
    }

    protected final void invalidateGlobalTransform() {
        if (validGlobalTransform) {
            validGlobalTransform = false;
            invalidateGlobalShape();
            invalidateChildrenGlobalTransform();
        }
    }

    final AffineTransform getGlobalTransformInternal() {
        if (!validGlobalTransform) {
            validGlobalTransform = true;
            final Node parent = getParent();
            if (parent != null) {
                parent.getGlobalTransform(globalTransform);
            } else {
                globalTransform.setToIdentity();
            }
            globalTransform.concatenate(getLocalTransformInternal());
        }
        return globalTransform;
    }

    public final AffineTransform getGlobalTransform(final AffineTransform dest) {
        dest.setTransform(getGlobalTransformInternal());
        return dest;
    }

    public final AffineTransform getGlobalTransform() {
        return getGlobalTransform(new AffineTransform());
    }

    protected final void invalidateShape() {
        if (shape != null) {
            shape = null;
            invalidateLocalShape();
        }
    }

    public final Shape getShape() {
        if (shape == null) {
            shape = createShape();
        }
        return shape;
    }

    protected final void invalidateLocalShape() {
        if (localShape != null) {
            localShape = null;
            invalidateGlobalShape();
        }
    }

    public final Shape getLocalShape() {
        if (localShape == null) {
            localShape = getLocalTransformInternal().createTransformedShape(getShape());
        }
        return localShape;
    }

    protected final void invalidateGlobalShape() {
        if (globalShape != null) {
            globalShape = null;
        }
    }

    public final Shape getGlobalShape() {
        if (globalShape == null) {
            globalShape = getGlobalTransformInternal().createTransformedShape(getShape());
        }
        return globalShape;
    }

    protected final void invalidateComposite() {
        if (composite != null) {
            composite = null;
        }
    }

    public final Composite getComposite() {
        if (composite == null) {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha);
        }
        return composite;
    }

    final void paintInternal(final Graphics2D graphics) {
        if (visible) {
            final AffineTransform saveTransform = graphics.getTransform();
            final Composite saveComposite = graphics.getComposite();
            final Shape saveClip = graphics.getClip();
            final Object saveAntialias = graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            try {
                final Node clip = getClip();
                if (clip != null) {
                    graphics.setTransform(Display.getGdkScaleTransformInternal());
                    graphics.clip(clip.getGlobalShape());
                    graphics.setTransform(saveTransform);
                }
                graphics.transform(getLocalTransformInternal());
                final Shape shape = getShape();
                final Rectangle bounds = shape.getBounds();
                if (graphics.hitClip(bounds.x, bounds.y, bounds.width, bounds.height)) {
                    graphics.setComposite(getComposite());
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias ?
                            RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
                    paint(graphics);
                }
            } finally {
                graphics.setTransform(saveTransform);
                graphics.setComposite(saveComposite);
                graphics.setClip(saveClip);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, saveAntialias);
            }
        }
    }

    protected void invalidateChildrenGlobalTransform() {
    }

    protected abstract Shape createShape();

    protected abstract void paint(final Graphics2D graphics);

    void updateInternal() {
        update();
    }

    protected void update() {
    }
}
