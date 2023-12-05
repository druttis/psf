package org.dru.psf.job;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public abstract class JoinJob<T> extends CompletableJob<List<T>> {
    protected JoinJob(final Collection<Job<?>> jobs) {
        if (jobs == null) {
            reject(new NullPointerException("jobs"));
        } else {
            final StringJoiner sj = new StringJoiner("][", "[", "] in jobs");
            int index = 0;
            int count = 0;
            for (final Job<?> job : jobs) {
                if (job == null) {
                    sj.add(String.valueOf(index));
                    count++;
                }
                index++;
            }
            if (count > 0) {
                reject(new NullPointerException(sj.toString()));
            } else {
                perform(List.copyOf(jobs));
            }
        }
    }

    protected abstract void perform(List<Job<?>> jobs);
}
