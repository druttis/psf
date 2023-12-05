package org.dru.psf.job;

import java.util.concurrent.Executor;
import java.util.function.DoubleConsumer;

abstract class SettledJob<T> extends AbstractJob<T> {
    private final double progress;

    SettledJob(final double progress) {
        this.progress = progress;
    }

    @Override
    public final AbstractJob<T> resolve(final T result) {
        return this;
    }

    @Override
    public final AbstractJob<T> reject(final Exception reason) {
        return this;
    }

    @Override
    public final AbstractJob<T> update(final double progress) {
        return this;
    }

    @Override
    protected void onUpdated(final DoubleConsumer callback, final Executor executor) {
        executor.execute(() -> callback.accept(progress));
    }
}
