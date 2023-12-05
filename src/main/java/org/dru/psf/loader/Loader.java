package org.dru.psf.loader;

import org.dru.psf.async.Async;
import org.dru.psf.job.CompletableJob;
import org.dru.psf.job.Job;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

public abstract class Loader<T> {
    private static final Object MONITOR = new Object();
    private static final Map<Class<?>, Set<Loader<?>>> LOADERS = new HashMap<>();

    static {
        register(BufferedImageLoader.class);
    }

    public static void register(final Class<? extends Loader<?>> loaderClass) {
        Objects.requireNonNull(loaderClass, "loaderClass");
        final Loader<?> loader;
        try {
            loader = loaderClass.getConstructor().newInstance();
        } catch (final Exception exception) {
            throw new RuntimeException(exception);
        }
        final Class<?> type = loader.getType();
        synchronized (MONITOR) {
            if (!LOADERS.computeIfAbsent(type, $ -> new HashSet<>()).add(loader)) {
                throw new IllegalArgumentException("Already registered: " + loaderClass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Loader<T> lookup(final Class<T> type, final Path path) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(path, "path");
        final List<Loader<T>> result = new ArrayList<>();
        final Set<Loader<?>> loaders;
        synchronized (MONITOR) {
            loaders = LOADERS.get(type);
        }
        if (loaders != null) {
            for (final Loader<?> loader : loaders) {
                if (loader.accepts(path)) {
                    result.add((Loader<T>) loader);
                }
            }
        }
        if (result.isEmpty()) {
            throw new RuntimeException("No suitable loader exist: type=" + type + ", path=" + path);
        } else if (result.size() > 1) {
            throw new RuntimeException("Duplicate suitable loaders exist: type=" + type + ", path=" + path);
        }
        return result.get(0);
    }

    public static Loader<?> lookup(final Path path) {
        Objects.requireNonNull(path, "path");
        final List<Loader<?>> result = new ArrayList<>();
        final List<Loader<?>> loaders;
        synchronized (MONITOR) {
            loaders = LOADERS.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        for (final Loader<?> loader : loaders) {
            if (loader.accepts(path)) {
                result.add(loader);
            }
        }
        if (result.isEmpty()) {
            throw new RuntimeException("No suitable loader exist: path=" + path);
        } else if (result.size() > 1) {
            throw new RuntimeException("Duplicate suitable loaders exist: path=" + path);
        }
        return result.get(0);
    }


    public static <T> Job<T> load(final Path path, final Class<T> type) {
        final Loader<T> loader = lookup(type, path);
        final CompletableJob<T> destination = new CompletableJob<>();
        Async.getBackgroundExecutor().execute(() -> {
            try {
                destination.resolve(loader.load(path, destination::update));
            } catch (final Exception exception) {
                destination.reject(exception);
            }
        });
        return destination;
    }

    @SuppressWarnings("unchecked")
    public static <T> Job<T> load(final Path path) {
        final CompletableJob<T> destination = new CompletableJob<>();
        Job.resolved(path)
                .then($ -> (Loader<T>) lookup(path))
                .then(loader -> loader.load(path, destination::update))
                .onResolved(destination::resolve)
                .onRejected(destination::reject);
        return destination;
    }

    public abstract Class<T> getType();

    public abstract boolean accepts(Path path);

    public abstract T load(Path path, DoubleConsumer callback) throws IOException;
}
