package org.dru.psf.job;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

final class ResolvedJob<T> extends SettledJob<T> {
    private final T value;

    ResolvedJob(final double progress, final T value) {
        super(progress);
        this.value = value;
    }

    @Override
    public JobState getState() {
        return JobState.RESOLVED;
    }

    @Override
    protected void onResolved(final Consumer<? super T> callback, final Executor executor) {
        executor.execute(() -> callback.accept(value));
    }

    @Override
    protected void onRejected(final Consumer<? super Exception> callback, final Executor executor) {
    }
}
