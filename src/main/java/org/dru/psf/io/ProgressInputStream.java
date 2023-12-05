package org.dru.psf.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.DoubleConsumer;

public class ProgressInputStream extends InputStream {
    private final InputStream is;
    private final long bytesToRead;
    private final DoubleConsumer callback;
    private long bytesRead;

    public ProgressInputStream(final InputStream is, final long bytesToRead, final DoubleConsumer callback) {
        Objects.requireNonNull(is, "is");
        Objects.requireNonNull(callback, "callback");
        this.is = is;
        this.bytesToRead = bytesToRead;
        this.callback = callback;
    }

    @Override
    public int read() throws IOException {
        final int c = is.read();
        if (c != -1) {
            update(1);
        }
        return c;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int n = is.read(b, off, len);
        if (n > 0) {
            update(n);
        }
        return n;
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public synchronized void mark(final int readlimit) {
        is.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    private void update(final long n) {
        bytesRead += n;
        callback.accept(bytesRead / (double) bytesToRead);
    }
}
