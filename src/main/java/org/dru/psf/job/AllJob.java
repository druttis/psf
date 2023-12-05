package org.dru.psf.job;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;

public final class AllJob extends JoinJob<Object> {
    AllJob(final Collection<Job<?>> jobs) {
        super(jobs);
    }

    @Override
    protected void perform(final List<Job<?>> jobs) {
        final int numJobs = jobs.size();
        final Object[] results = new Object[numJobs];
        final AtomicInteger numResolved = new AtomicInteger();
        final double[] progresses = new double[numJobs];
        int index = 0;
        for (final Job<?> job : jobs) {
            if (getState() == JobState.REJECTED) {
                break;
            }
            final int finalIndex = index;
            job.onResolved((result) -> {
                results[finalIndex] = result;
                if (numResolved.incrementAndGet() == numJobs) {
                    resolve(List.of(results));
                }
            });
            job.onRejected(this::reject);
            job.onUpdated(progress -> {
                progresses[finalIndex] = progress;
                update(DoubleStream.of(progresses).sum() / numJobs);
            });
            index++;
        }
    }
}
