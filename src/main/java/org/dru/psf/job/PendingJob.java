package org.dru.psf.job;

import org.dru.psf.signal.SignalSupport;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

final class PendingJob<T> extends AbstractJob<T> {
    private final SignalSupport<T> resolvedSignal;
    private final SignalSupport<Exception> rejectedSignal;
    private final SignalSupport<Double> updatedSignal;
    private volatile double progress;

    PendingJob() {
        resolvedSignal = new SignalSupport<>();
        rejectedSignal = new SignalSupport<>();
        updatedSignal = new SignalSupport<>();
    }

    @Override
    public JobState getState() {
        return JobState.PENDING;
    }

    @Override
    public AbstractJob<T> resolve(final T result) {
        final ResolvedJob<T> job = new ResolvedJob<>(progress, result);
        resolvedSignal.dispatch(result);
        return job;
    }

    @Override
    public AbstractJob<T> reject(final Exception reason) {
        final RejectedJob<T> job = new RejectedJob<>(progress, reason);
        rejectedSignal.dispatch(reason);
        return job;
    }

    @Override
    public AbstractJob<T> update(final double progress) {
        final double old = this.progress;
        this.progress = progress;
        if (progress != old) {
            updatedSignal.dispatch(progress);
        }
        return this;
    }

    @Override
    protected void onResolved(final Consumer<? super T> callback, final Executor executor) {
        resolvedSignal.addCallback(callback, executor);
    }

    @Override
    protected void onRejected(final Consumer<? super Exception> callback, final Executor executor) {
        rejectedSignal.addCallback(callback, executor);
    }

    @Override
    protected void onUpdated(final DoubleConsumer callback, final Executor executor) {
        updatedSignal.addCallback(callback::accept, executor);
    }
}
