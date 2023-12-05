package org.dru.psf.job;

import org.dru.psf.util.ThrowingFunction;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface Job<T> {
    static <T> Job<T> resolved(T value) {
        return new ResolvedJob<>(1.0, value);
    }

    static <T> Job<T> rejected(final Exception reason) {
        return new RejectedJob<>(0.0, reason);
    }

    <R> Job<R> then(ThrowingFunction<? super T, ? extends R> mapper);

    Job<T> trap(ThrowingFunction<? super Exception, ? extends T> mapper);

    Job<T> onResolved(Consumer<? super T> callback);

    Job<T> onRejected(Consumer<? super Exception> callback);

    Job<T> onUpdated(DoubleConsumer callback);
}
