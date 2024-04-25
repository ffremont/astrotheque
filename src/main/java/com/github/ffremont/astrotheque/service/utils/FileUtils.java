package com.github.ffremont.astrotheque.service.utils;

import java.util.function.Function;
import java.util.function.Predicate;

public class FileUtils {

    private FileUtils() {
    }

    public static final Predicate<String> isFit = (String filename) -> filename.toLowerCase().endsWith(".fit") || filename.toLowerCase().endsWith(".fits");

    public static final Function<String, String> extensionOf = (String filename) -> filename.substring(filename.lastIndexOf(".") + 1);
    public static final Predicate<String> isImage = (String filename) -> filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".jpeg") || filename.toLowerCase().endsWith(".jpg");
    public static final Predicate<String> isJpeg = (String filename) -> filename.toLowerCase().endsWith(".jpeg") || filename.toLowerCase().endsWith(".jpg");

}
