package org.dru.psf.async;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class Async {
    private static final Object MONITOR = new Object();
    private static final Executor DEFAULT_EXECUTOR = EventQueue::invokeLater;

    private static volatile Executor applicationExecutor = DEFAULT_EXECUTOR;
    private static volatile Executor graphicsExecutor = DEFAULT_EXECUTOR;
    private static volatile Executor audioExecutor = DEFAULT_EXECUTOR;
    private static volatile Executor backgroundExecutor = DEFAULT_EXECUTOR;

    public static Executor getApplicationExecutor() {
        return applicationExecutor;
    }

    public static void setApplicationExecutor(final Executor applicationExecutor) {
        Objects.requireNonNull(applicationExecutor, "applicationExecutor");
        synchronized (MONITOR) {
            if (applicationExecutor != Async.applicationExecutor) {
                requireDefaultExecutor(Async.applicationExecutor, "applicationExecutor");
                Async.applicationExecutor = applicationExecutor;
            }
        }
    }

    public static Executor getGraphicsExecutor() {
        return graphicsExecutor;
    }

    public static void setGraphicsExecutor(final Executor graphicsExecutor) {
        Objects.requireNonNull(graphicsExecutor, "graphicsExecutor");
        synchronized (MONITOR) {
            if (graphicsExecutor != Async.graphicsExecutor) {
                requireDefaultExecutor(Async.graphicsExecutor, "graphicsExecutor");
                Async.graphicsExecutor = graphicsExecutor;
            }
        }
    }

    public static Executor getAudioExecutor() {
        return audioExecutor;
    }

    public static void setAudioExecutor(final Executor audioExecutor) {
        Objects.requireNonNull(audioExecutor, "audioExecutor");
        synchronized (MONITOR) {
            if (audioExecutor != Async.audioExecutor) {
                requireDefaultExecutor(Async.audioExecutor, "audioExecutor");
                Async.audioExecutor = audioExecutor;
            }
        }
    }

    public static Executor getBackgroundExecutor() {
        return backgroundExecutor;
    }

    public static void setBackgroundExecutor(final Executor backgroundExecutor) {
        Objects.requireNonNull(backgroundExecutor, "backgroundExecutor");
        synchronized (MONITOR) {
            if (backgroundExecutor != Async.backgroundExecutor) {
                requireDefaultExecutor(Async.backgroundExecutor, "backgroundExecutor");
                Async.backgroundExecutor = backgroundExecutor;
            }
        }
    }

    private static void requireDefaultExecutor(final Executor executor, final String name) {
        if (executor != DEFAULT_EXECUTOR) {
            throw new IllegalStateException(name + " already set");
        }
    }

    private Async() {
        throw new UnsupportedOperationException();
    }
}
