package org.dru.psf.job;

import org.dru.psf.async.Async;
import org.dru.psf.util.ThrowingFunction;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public abstract class AbstractJob<T> implements Job<T> {
    @Override
    public <R> Job<R> then(final ThrowingFunction<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        final CompletableJob<R> dest = new CompletableJob<>();
        onResolved(result -> {
            try {
                dest.resolve(mapper.apply(result));
            } catch (final Exception error) {
                dest.reject(error);
            }
        }, Async.getBackgroundExecutor());
        onRejected(dest::reject, Async.getBackgroundExecutor());
        return dest;
    }

    @Override
    public final Job<T> trap(final ThrowingFunction<? super Exception, ? extends T> mapper) {
        final CompletableJob<T> dest = new CompletableJob<>();
        onRejected(reason -> {
            try {
                dest.resolve(mapper.apply(reason));
            } catch (final Exception exception) {
                // TODO: nest reason & exception
                dest.reject(reason);
            }
        }, Async.getBackgroundExecutor());
        return dest;
    }

    @Override
    public final AbstractJob<T> onResolved(final Consumer<? super T> callback) {
        Objects.requireNonNull(callback, "callback");
        onResolved(callback, Async.getApplicationExecutor());
        return this;
    }

    @Override
    public final AbstractJob<T> onRejected(final Consumer<? super Exception> callback) {
        Objects.requireNonNull(callback, "callback");
        onRejected(callback, Async.getApplicationExecutor());
        return this;
    }

    @Override
    public final AbstractJob<T> onUpdated(final DoubleConsumer callback) {
        Objects.requireNonNull(callback, "callback");
        onUpdated(callback, Async.getApplicationExecutor());
        return this;
    }

    public abstract JobState getState();

    public abstract AbstractJob<T> resolve(T result);

    public abstract AbstractJob<T> reject(Exception reason);

    public abstract AbstractJob<T> update(double progress);

    protected abstract void onResolved(Consumer<? super T> callback, Executor executor);

    protected abstract void onRejected(Consumer<? super Exception> callback, Executor executor);

    protected abstract void onUpdated(DoubleConsumer callback, Executor executor);
}
