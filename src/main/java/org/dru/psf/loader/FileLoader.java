package org.dru.psf.loader;

import org.dru.psf.io.ProgressInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.DoubleConsumer;

public abstract class FileLoader<T> extends Loader<T> {
    private final PathMatcher matcher;

    public FileLoader() {
        final List<String> dest = new ArrayList<>();
        extensions(dest);
        final StringJoiner stringJoiner = new StringJoiner(",", "glob:**.{", "}");
        dest.forEach(stringJoiner::add);
        matcher = FileSystems.getDefault().getPathMatcher(stringJoiner.toString());
    }

    @Override
    public boolean accepts(final Path path) {
        return matcher.matches(path);
    }

    @Override
    public final T load(final Path path, final DoubleConsumer callback) throws IOException {
        final int bytesToRead = (int) Files.size(path);
        try (final InputStream is = Files.newInputStream(path)) {
            try (final ProgressInputStream pis = new ProgressInputStream(is, bytesToRead, callback)) {
                final T result = read(pis);
                callback.accept(1.0);
                return result;
            }
        }
    }

    protected abstract void extensions(final List<String> dest);

    protected abstract T read(final InputStream is) throws IOException;
}
