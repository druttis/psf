package org.dru.psf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final Object MONITOR = new Object();
    private static final List<Runnable> PENDING_TASKS = new ArrayList<>();
    private static final List<Runnable> ATTACHED_TASKS = new ArrayList<>();
    private static ApplicationThread THREAD;
    private static ExecutorService EXECUTOR_SERVICE;
    private static volatile double start;
    private static volatile double time;
    private static volatile double delta;
    private static volatile double fps;
    private static int frame;
    private static double stamp;

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static double nextDouble(final double min, final double max) {
        return nextDouble() * (max - min) + min;
    }

    public static double nextDouble(final double max) {
        return nextDouble(0.0, max);
    }

    public static int nextInt(final int min, final int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static int nextInt(final int max) {
        return nextInt(0, max);
    }

    public static void execute(final Runnable task) {
        Objects.requireNonNull(task, "task");
        synchronized (MONITOR) {
            PENDING_TASKS.add(task);
        }
    }

    public static void attach(final Runnable task) {
        Objects.requireNonNull(task, "task");
        synchronized (MONITOR) {
            ATTACHED_TASKS.add(task);
        }
    }

    public static void detach(final Runnable task) {
        if (task != null) {
            synchronized (MONITOR) {
                ATTACHED_TASKS.remove(task);
            }
        }
    }

    public static boolean isRunning() {
        synchronized (MONITOR) {
            return THREAD != null;
        }
    }

    public static void start() {
        synchronized (MONITOR) {
            if (THREAD != null) {
                throw new IllegalStateException("Application is already running");
            }
            THREAD = new ApplicationThread();
            THREAD.start();
            EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        }
    }

    public static void stop() {
        final Thread thread;
        final ExecutorService executorService;
        synchronized (MONITOR) {
            thread = THREAD;
            executorService = EXECUTOR_SERVICE;
            if (thread == null) {
                throw new IllegalStateException("Application is not running");
            }
            THREAD = null;
        }
        try {
            thread.join();
        } catch (final InterruptedException exception) {
            throw new RuntimeException(exception);
        } finally {
            executorService.shutdown();
        }
    }

    public static double time() {
        return time;
    }

    public static double delta() {
        return delta;
    }

    public static double fps() {
        return fps;
    }

    private Application() {
        throw new UnsupportedOperationException();
    }

    private static final class ApplicationThread extends Thread {
        private ApplicationThread() {
            super("ApplicationThread");
        }

        @Override
        public void run() {
            final Set<Runnable> erroneousTasks = new HashSet<>();
            while (THREAD == this) {
                final double now = System.currentTimeMillis() / 1000.0 - start;
                if (start == 0.0) {
                    start = now;
                    continue;
                }
                delta = now - time;
                time = now;
                frame++;
                stamp += delta;
                if (stamp >= 1.0) {
                    fps = frame  / stamp;
                    frame = 0;
                    stamp -= 1.0;
                }
                final List<Runnable> pendingTasks;
                final List<Runnable> attachedTasks;
                synchronized (MONITOR) {
                    pendingTasks = new ArrayList<>(PENDING_TASKS);
                    PENDING_TASKS.clear();
                    if (!erroneousTasks.isEmpty()) {
                        ATTACHED_TASKS.removeIf(erroneousTasks::contains);
                        erroneousTasks.clear();
                    }
                    attachedTasks = new ArrayList<>(ATTACHED_TASKS);
                }
                for (final Runnable pendingTask : pendingTasks) {
                    try {
                        pendingTask.run();
                    } catch (final RuntimeException exception) {
                        LOGGER.error("Unhandled pendingTask RuntimeException caught", exception);
                    }
                }
                for (final Runnable attachedTask : attachedTasks) {
                    try {
                        attachedTask.run();
                    } catch (final RuntimeException exception) {
                        erroneousTasks.add(attachedTask);
                        LOGGER.error("Unhandled attachedTask RuntimeException caught, detaching", exception);
                    }
                }
            }
        }
    }
}
