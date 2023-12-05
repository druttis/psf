package org.dru.psf.loader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public final class BufferedImageLoader extends FileLoader<BufferedImage> {
    public BufferedImageLoader() {
    }

    @Override
    public Class<BufferedImage> getType() {
        return BufferedImage.class;
    }

    @Override
    protected void extensions(final List<String> dest) {
        dest.addAll(Arrays.asList(ImageIO.getReaderFileSuffixes()));
    }

    @Override
    protected BufferedImage read(final InputStream is) throws IOException {
        return ImageIO.read(is);
    }
}
