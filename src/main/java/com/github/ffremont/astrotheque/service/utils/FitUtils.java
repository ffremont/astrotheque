package com.github.ffremont.astrotheque.service.utils;


import com.github.ffremont.astrotheque.service.model.FitData;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.HeaderCard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.Predicate;

public class FitUtils {
    private FitUtils() {
    }

    public static FitData analyze(Path fitFile) {
        BasicHDU<?> hdu = null;
        try {
            hdu = (new Fits(fitFile.toAbsolutePath().toFile())).readHDU();

            // from fit header or creation file date
            var dateObs = Optional.ofNullable(hdu.getHeader().findCard("DATE-OBS").getValue())
                    .filter(Predicate.not(String::isEmpty))
                    .map(LocalDateTime::parse)
                    .orElseGet(() -> {
                        try {
                            BasicFileAttributes attr = Files.readAttributes(fitFile, BasicFileAttributes.class);
                            return LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return FitData.builder()
                    .tempFile(fitFile)
                    .gain(Integer.valueOf(Optional.ofNullable(hdu.getHeader().findCard("GAIN")).map(HeaderCard::getValue).orElse("120")))
                    .stackCnt(Integer.valueOf(
                            Optional.ofNullable(hdu.getHeader().findCard("STACKCNT")).map(HeaderCard::getValue).orElse("1"))
                    )
                    .dateObs(dateObs)
                    .instrume(Optional.ofNullable(hdu.getHeader().findCard("INSTRUME")).map(HeaderCard::getValue).orElse(""))
                    .exposure(
                            Optional.ofNullable(hdu.getHeader().findCard("EXPOSURE"))
                                    .map(HeaderCard::getValue)
                                    .map(Float::parseFloat)
                                    .orElse(1F)
                    )
                    .temp(Float.parseFloat(
                            Optional.ofNullable(hdu.getHeader().findCard("CCD-TEMP")).map(HeaderCard::getValue).orElse("20"))
                    )
                    .build();
        } catch (FitsException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
