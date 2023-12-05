package org.dru.psf.job;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

final class RejectedJob<T> extends SettledJob<T> {
    private final Exception reason;

    RejectedJob(final double progress, final Exception reason) {
        super(progress);
        Objects.requireNonNull(reason, "reason");
        this.reason = reason;
    }

    @Override
    public JobState getState() {
        return JobState.REJECTED;
    }

    @Override
    protected void onResolved(final Consumer<? super T> callback, final Executor executor) {
    }

    @Override
    protected void onRejected(final Consumer<? super Exception> callback, final Executor executor) {
        executor.execute(() -> callback.accept(reason));
    }
}
