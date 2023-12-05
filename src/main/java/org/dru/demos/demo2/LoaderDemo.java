package org.dru.demos.demo2;

import org.dru.psf.job.Job;
import org.dru.psf.loader.Loader;

import java.net.URL;
import java.nio.file.Path;

public class LoaderDemo {
    public static void main(String[] args) throws Exception {
        Job.resolved(LoaderDemo.class.getResource("/queue.png"))
                .then(URL::toURI)
                .then(Path::of)
                .then(Loader::load)
                .onResolved(loader ->
                        loader.onUpdated(System.out::println)
                                .onResolved(System.out::println)
                                .onRejected(System.err::println))
                .onRejected(System.err::println);
    }
}
