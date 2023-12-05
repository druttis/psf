package org.dru.psf.scene;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Objects;

public final class StaticText extends Node {
    private String text;
    private Font font;
    private Color color;

    public StaticText(final String text, final Font font, final Color color) {
        setAlign(0.5, 0.5);
        setText(text);
        setFont(font);
        setColor(color);
    }

    public StaticText() {
        this(null, null, null);
    }

    @Override
    protected Shape createShape() {
        final String text = getText();
        final Font font = getFont();
        if (text != null && font != null) {
            final FontRenderContext frc = new FontRenderContext(null, isAntialias(), false);
            final GlyphVector gv = font.createGlyphVector(frc, text);
            return gv.getOutline(0, (float) gv.getVisualBounds().getHeight());
        }
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    protected void paint(final Graphics2D graphics) {
        final Color saveColor = graphics.getColor();
        try {
            final Color color = getColor();
            if (color != null) {
                graphics.setColor(color);
            }
            graphics.fill(getShape());
        } finally {
            graphics.setColor(saveColor);
        }
    }

    public String getText() {
        return text;
    }

    public StaticText setText(final String newText) {
        final String oldText = text;
        text = newText;
        if (!Objects.equals(newText, oldText)) {
            invalidateShape();
            invalidateLocalTransform();
        }
        return this;
    }

    public Font getFont() {
        return font;
    }

    public StaticText setFont(final Font newFont) {
        final Font oldFont = font;
        font = newFont;
        if (!Objects.equals(newFont, oldFont)) {
            invalidateShape();
            invalidateLocalTransform();
        }
        return this;
    }

    public Color getColor() {
        return color;
    }

    public StaticText setColor(final Color newColor) {
        color = newColor;
        return this;
    }
}
