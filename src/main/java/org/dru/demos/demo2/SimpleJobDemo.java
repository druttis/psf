package org.dru.demos.demo2;

import org.dru.psf.async.Async;
import org.dru.psf.job.Job;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

public class SimpleJobDemo {
    public static void main(String[] args) throws Exception {
        Async.setBackgroundExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1));
        Job.resolved(new URL("https://www.google.com/index.html"))
                .then(URL::openStream)
                .then(InputStream::readAllBytes)
                .then(bytes -> new String(bytes, Charset.defaultCharset()))
                .onResolved(System.out::println)
                .onRejected(System.err::println);
    }
}
