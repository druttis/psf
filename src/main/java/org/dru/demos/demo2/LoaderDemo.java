package org.dru.demos.demo2;

import org.dru.psf.async.Async;
import org.dru.psf.job.Job;
import org.dru.psf.loader.Loader;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoaderDemo {
    private static final URL QUEUE = LoaderDemo.class.getResource("/queue.png");
    private static final URL PUPIL = LoaderDemo.class.getResource("/pupil.png");

    public static Path path(final URL url) {
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Job<Object> load(final URL url) {
        final Path path = path(url);
        return Loader.load(path)
                .onUpdated(System.out::println)
                .onResolved(System.out::println)
                .onRejected(System.err::println);
    }

    public static Job<List<Object>> loadAll(final Collection<URL> urls) {
        final Collection<Path> paths = urls.stream().map(LoaderDemo::path).toList();
        return Loader.loadAll(paths)
                .onUpdated(System.out::println)
                .onResolved(System.out::println)
                .onRejected(System.err::println);
    }

    public static void main(String[] args) throws Exception {
        Async.setBackgroundExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1));
        // one file just
        load(QUEUE);
        loadAll(List.of(QUEUE, PUPIL));
    }
}
