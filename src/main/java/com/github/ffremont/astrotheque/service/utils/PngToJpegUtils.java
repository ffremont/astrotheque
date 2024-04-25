package com.github.ffremont.astrotheque.service.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;


public class PngToJpegUtils implements Function<Path, Path> {
    @Override
    public Path apply(Path input) {
        try {
            Path jpg = Files.createTempFile("astrotheque_image_", ".jpg");
            BufferedImage originalImage = ImageIO.read(input.toFile());
            BufferedImage newBufferedImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            // draw a white background and puts the originalImage on it.
            newBufferedImage.createGraphics()
                    .drawImage(originalImage,
                            0,
                            0,
                            Color.WHITE,
                            null);

            // save an image
            ImageIO.write(newBufferedImage, "jpg", jpg.toFile());

            return jpg;
        } catch (IOException e) {
            throw new IllegalArgumentException("Png invalide", e);
        }

    }
}
