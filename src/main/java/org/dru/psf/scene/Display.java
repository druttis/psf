package org.dru.psf.scene;

import org.dru.psf.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.Objects;

public final class Display {
    private static final Logger LOGGER = LoggerFactory.getLogger(Display.class);
    private static final AffineTransform GDK_SCALE_TRANSFORM;

    static {
        final String gdkScaleString = System.getenv("GDK_SCALE");
        double gdkScale = 1.0;
        if (gdkScaleString != null) {
            try {
                gdkScale = Double.parseDouble(gdkScaleString);
                LOGGER.info("Using environment variable GDK_SCALE");
            } catch (final Exception exception) {
                LOGGER.warn("Could not parse GDK_SCALE, using default 1.0", exception);
            }
        } else {
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice gd = ge.getDefaultScreenDevice();
            final DisplayMode dm = gd.getDisplayMode();
            if (dm.getWidth() > 1920 || dm.getHeight() > 1500) {
                LOGGER.info("The screen may be a HDPI screen, consider settings GDK_SCALE=2 in env");
            }
        }
        LOGGER.info("Settings GDK_SCALE_TRANSFORM to {}", gdkScale);
        GDK_SCALE_TRANSFORM = AffineTransform.getScaleInstance(gdkScale, gdkScale);
    }

    public static AffineTransform getGdkScaleTransform(final AffineTransform dest) {
        dest.setTransform(GDK_SCALE_TRANSFORM);
        return dest;
    }

    public static AffineTransform getGdkScaleTransform() {
        return getGdkScaleTransform(new AffineTransform());
    }

    static AffineTransform getGdkScaleTransformInternal() {
        return GDK_SCALE_TRANSFORM;
    }

    private static final WindowHandler WINDOW_HANDLER = new WindowHandler();
    private static final MouseHandler MOUSE_HANDLER = new MouseHandler();

    private static final Frame FRAME = new Frame();
    private static final Canvas CANVAS = new Canvas();
    private static Node root;
    private static final Point2D mouse = new Point2D.Double();

    static {
        SwingUtilities.invokeLater(() -> {
            FRAME.addWindowListener(WINDOW_HANDLER);
            CANVAS.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            CANVAS.setForeground(Color.green);
            CANVAS.addMouseListener(MOUSE_HANDLER);
            CANVAS.addMouseMotionListener(MOUSE_HANDLER);
            CANVAS.addMouseWheelListener(MOUSE_HANDLER);
            FRAME.setIgnoreRepaint(true);
            CANVAS.setIgnoreRepaint(true);
            FRAME.add(CANVAS);
        });
    }

    public static void show(final String title, final int width, final int height, final Color backgroundColor) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(backgroundColor, "backgroundColor");
        SwingUtilities.invokeLater(() -> {
            if (!FRAME.isVisible()) {
                FRAME.setTitle(title);
                CANVAS.setPreferredSize(new Dimension(width, height));
                CANVAS.setBackground(backgroundColor);
                FRAME.pack();
                FRAME.setLocationRelativeTo(null);
                FRAME.setVisible(true);
            } else {
                LOGGER.warn("Frame already set visible");
            }
        });
    }

    public static FontMetrics getFontMetrics(final Font font) {
        Objects.requireNonNull(font, "font");
        return CANVAS.getFontMetrics(font);
    }

    public static Node getRoot() {
        return root;
    }

    public static void setRoot(final Node root) {
        Application.execute(() -> Display.root = root);
    }

    public static Vector2 getMouse() {
        return new Vector2(mouse);
    }

    public static int getWidth() {
        return CANVAS.getWidth();
    }

    public static int getHeight() {
        return CANVAS.getHeight();
    }

    private static void render() {
        if (!FRAME.isVisible() || FRAME.getWidth() == 0 || FRAME.getHeight() == 0) {
            return;
        }
        if (CANVAS.getBufferStrategy() == null) {
            CANVAS.createBufferStrategy(2);
        }
        final BufferStrategy bufferStrategy = CANVAS.getBufferStrategy();
        final Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            graphics.setColor(CANVAS.getBackground());
            graphics.fillRect(0, 0, CANVAS.getWidth(), CANVAS.getHeight());
            final Node root = getRoot();
            if (root != null) {
                root.updateInternal();
                root.paintInternal(graphics);
                graphics.setFont(CANVAS.getFont());
                final FontMetrics fm = graphics.getFontMetrics();
                graphics.setColor(new Color(0, 127, 0, 159));
                final String fpsStr = "-=[ FPS: %.2f, NODE COUNT: %d ]=-".formatted(Application.fps(), countNodes(root, 0));
                graphics.fillRect(0, 0, fm.stringWidth(fpsStr), fm.getHeight());
                graphics.setColor(Color.white);
                graphics.drawString(fpsStr, 0, fm.getAscent());
            }
        } finally {
            graphics.dispose();
        }
        bufferStrategy.show();
        Toolkit.getDefaultToolkit().sync();
    }

    private static int countNodes(Node node, int count) {
        count++;
        if (node instanceof Tree tree) {
            for (int index = 0; index < tree.getChildCount(); index++) {
                count = countNodes(tree.getChild(index), count);
            }
        }
        return count;
    }

    private Display() {
        throw new UnsupportedOperationException();
    }

    private static final class WindowHandler extends WindowAdapter {
        @Override
        public void windowOpened(final WindowEvent e) {
            Application.attach(Display::render);
        }

        @Override
        public void windowClosing(final WindowEvent e) {
            FRAME.setVisible(false);
            FRAME.dispose();
        }

        @Override
        public void windowClosed(final WindowEvent e) {
            Application.stop();
        }
    }

    private static final class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            super.mouseClicked(e);
            updateMouse(e);
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            super.mousePressed(e);
            updateMouse(e);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            super.mouseReleased(e);
            updateMouse(e);
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            super.mouseEntered(e);
            updateMouse(e);
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            super.mouseExited(e);
            updateMouse(e);
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            updateMouse(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            super.mouseDragged(e);
            updateMouse(e);
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            super.mouseMoved(e);
            updateMouse(e);
        }

        private void updateMouse(final MouseEvent e) {
            mouse.setLocation(e.getX(), e.getY());
        }
    }
}
