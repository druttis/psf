package org.dru.psf.scene;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class Tree extends Node {
    private final List<Node> children;

    public Tree() {
        children = new ArrayList<>();
    }

    public final List<Node> getChildren(final List<Node> dest) {
        dest.addAll(children);
        return dest;
    }

    public final List<Node> getChildren() {
        return getChildren(new ArrayList<>());
    }

    public final int getChildCount() {
        return children.size();
    }

    public final Node getChild(final int index) {
        return children.get(index);
    }

    public final int getChildIndex(final Node child) {
        return children.indexOf(child);
    }

    public final void addChild(final int index, final Node child) {
        final Tree parent = child.getParent();
        if (parent != null) {
            parent.removeChild(child);
        }
        children.add(index, child);
        child.setParent(this);
        invalidateShape();
        invalidateLocalTransform();
        child.invalidateGlobalTransform();
    }

    public final void addChild(final Node child) {
        addChild(getChildCount(), child);
    }

    public final Node removeChild(final int index) {
        final Node child = children.remove(index);
        child.setParent(null);
        invalidateShape();
        invalidateLocalTransform();
        return child;
    }

    public final int removeChild(final Node child) {
        final int index = getChildIndex(child);
        if (index >= 0) {
            removeChild(index);
        }
        return index;
    }

    public final void removeAllChildren() {
        int childCount;
        while ((childCount = getChildCount()) > 0) {
            removeChild(childCount - 1);
        }
    }

    @Override
    protected void invalidateChildrenGlobalTransform() {
        super.invalidateChildrenGlobalTransform();
        for (final Node child : children) {
            child.invalidateGlobalTransform();
        }
    }

    @Override
    protected Shape createShape() {
        final Path2D path = new Path2D.Double();
        for (final Node child : children) {
            path.append(child.getLocalShape(), false);
        }
        return path;
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        for (final Node child : children) {
            child.paintInternal(graphics);
        }
    }

    @Override
    void updateInternal() {
        super.updateInternal();
        for (final Node child : children) {
            child.updateInternal();
        }
    }
}
