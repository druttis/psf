package org.dru.psf.job;

import org.dru.psf.util.ThrowingFunction;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface Job<T> {
    static <T> Job<T> resolve(T value) {
        return new ResolvedJob<>(1.0, value);
    }

    static <T> Job<T> reject(final Exception reason) {
        return new RejectedJob<>(0.0, reason);
    }


    static Job<List<Object>> all(final Collection<Job<?>> jobs) {
        return new AllJob(jobs);
    }

    static Job<List<Object>> all(final Job<?>... jobs) {
        return all(List.of(jobs));
    }

    <R> Job<R> then(ThrowingFunction<? super T, ? extends R> mapper);

    Job<T> trap(ThrowingFunction<? super Exception, ? extends T> mapper);

    Job<T> onResolved(Consumer<? super T> callback);

    Job<T> onRejected(Consumer<? super Exception> callback);

    Job<T> onUpdated(DoubleConsumer callback);
}
