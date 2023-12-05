package org.dru.psf.signal;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public interface Signal<T> {
    void addCallback(final Consumer<? super T> callback, final Executor executor);

    void addCallback(final Consumer<? super T> callback);

    void removeCallback(final Consumer<? super T> callback, final Executor executor);

    void removeCallback(final Consumer<? super T> callback);
}
