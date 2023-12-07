package org.dru.psf.ui;

import org.dru.psf.scene.Grid9Sprite;
import org.dru.psf.scene.StaticText;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button extends Widget {
    private Grid9Sprite background;
    private StaticText caption;

    public Button() {
        background = new Grid9Sprite();
        caption = new StaticText();
        addChild(background);
        addChild(caption);
    }

    @Override
    protected Dimension preferredSize(final Dimension dest) {
        dest.setSize(caption.getShape().getBounds().getSize());
        return dest;
    }

    @Override
    protected void doLayout(final int width, final int height) {
        final Dimension cs = caption.getShape().getBounds().getSize();
        background.setSize(width, height);
        caption.setPosition((width - cs.width) >> 1, ((height - cs.height) >> 1) - 3);
    }

    public BufferedImage getBackground() {
        return background.getImage();
    }

    public Button setBackground(final BufferedImage image) {
        background.setImage(image);
        return this;
    }

    public Insets getInsets() {
        return background.getInsets();
    }

    public Button setInsets(final int top, final int left, final int bottom, final int right) {
        background.setInsets(top, left, bottom, right);
        return this;
    }

    public Button setInsets(final Insets insets) {
        background.setInsets(insets);
        return this;
    }

    public String getCaption() {
        return caption.getText();
    }

    public Button setCaption(final String text) {
        caption.setText(text);
        return this;
    }

    public Font getFont() {
        return caption.getFont();
    }

    public Button setFont(final Font font) {
        caption.setFont(font);
        return this;
    }

    public Color getColor() {
        return caption.getColor();
    }

    public Button setColor(final Color color) {
        caption.setColor(color);
        return this;
    }
}
