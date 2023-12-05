package org.dru.psf.signal;

import org.dru.psf.Application;
import org.dru.psf.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class SignalSupport<T> implements Signal<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalSupport.class);

    private static final Executor DEFAULT_EXECUTOR = Application::execute;

    private final List<Tuple<Consumer<? super T>, Executor>> tuples;

    public SignalSupport() {
        tuples = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addCallback(final Consumer<? super T> callback, final Executor executor) {
        Objects.requireNonNull(executor, "executor");
        addCallbackInternal(callback, executor);
    }

    @Override
    public void addCallback(final Consumer<? super T> callback) {
        addCallbackInternal(callback, DEFAULT_EXECUTOR);
    }

    @Override
    public void removeCallback(final Consumer<? super T> callback, final Executor executor) {
        Objects.requireNonNull(executor, "executor");
        removeCallbackInternal(callback, executor);
    }

    @Override
    public void removeCallback(final Consumer<? super T> callback) {
        removeCallback(callback, DEFAULT_EXECUTOR);
    }

    public void dispatch(final T value) {
        final Map<Executor, List<Consumer<? super T>>> callbacksByExecutor = new HashMap<>();
        for (final Tuple<Consumer<? super T>, Executor> tuple : tuples) {
            callbacksByExecutor.computeIfAbsent(tuple.right(), $ -> new ArrayList<>()).add(tuple.left());
        }
        callbacksByExecutor.forEach((executor, consumers) -> executor.execute(() -> {
            for (final Consumer<? super T> consumer : consumers) {
                try {
                    consumer.accept(value);
                } catch (final RuntimeException exception) {
                    LOGGER.error("Unhandled callback RuntimeException caught", exception);
                }
            }
        }));
    }

    private void addCallbackInternal(final Consumer<? super T> callback, final Executor executor) {
        Objects.requireNonNull(callback, "callback");
        final Tuple<Consumer<? super T>, Executor> tuple = new Tuple<>(callback, executor);
        tuples.add(tuple);
    }

    private void removeCallbackInternal(final Consumer<? super T> callback, final Executor executor) {
        Objects.requireNonNull(callback, "callback");
        final Tuple<Consumer<? super T>, Executor> tuple = new Tuple<>(callback, executor);
        tuples.remove(tuple);
    }
}
