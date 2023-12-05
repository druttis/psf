package org.dru.psf.job;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public final class CompletableJob<T> extends AbstractJob<T> {
    private final Object monitor;
    private AbstractJob<T> job;

    public CompletableJob() {
        monitor = new Object();
        job = new PendingJob<>();
    }

    @Override
    public JobState getState() {
        return job.getState();
    }

    @Override
    public AbstractJob<T> resolve(final T result) {
        synchronized (monitor) {
            job = job.resolve(result);
        }
        return this;
    }

    @Override
    public AbstractJob<T> reject(final Exception reason) {
        synchronized (monitor) {
            job = job.reject(reason);
        }
        return this;
    }

    @Override
    public AbstractJob<T> update(final double progress) {
        synchronized (monitor) {
            job.update(progress);
        }
        return this;
    }

    @Override
    protected void onResolved(final Consumer<? super T> callback, final Executor executor) {
        synchronized (monitor) {
            job.onResolved(callback, executor);
        }
    }

    @Override
    protected void onRejected(final Consumer<? super Exception> callback, final Executor executor) {
        synchronized (monitor) {
            job.onRejected(callback, executor);
        }
    }

    @Override
    protected void onUpdated(final DoubleConsumer callback, final Executor executor) {
        synchronized (monitor) {
            job.onUpdated(callback, executor);
        }
    }
}
