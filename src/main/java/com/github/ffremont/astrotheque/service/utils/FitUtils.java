package com.github.ffremont.astrotheque.service.utils;


import com.github.ffremont.astrotheque.service.model.FitData;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.HeaderCard;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
            var dateObs = Optional.ofNullable(
                            hdu.getHeader()).map(header -> Optional.ofNullable(header.findCard("DATE-OBS")).orElse(header.findCard("DATE"))).map(c -> c.getValue())
                    .filter(Predicate.not(String::isEmpty))
                    .map(LocalDateTime::parse);
            ;

            return FitData.builder()
                    .tempFile(fitFile)
                    .gain(Optional.ofNullable(hdu.getHeader().findCard("GAIN")).map(HeaderCard::getValue).map(Integer::valueOf).orElse(null))
                    .stackCnt(Integer.valueOf(
                            Optional.ofNullable(hdu.getHeader().findCard("STACKCNT")).map(HeaderCard::getValue).orElse("1"))
                    )
                    .dateObs(dateObs.orElse(null))
                    .instrume(Optional.ofNullable(hdu.getHeader().findCard("INSTRUME")).map(HeaderCard::getValue).orElse(""))
                    .exposure(
                            Optional.ofNullable(hdu.getHeader().findCard("EXPOSURE"))
                                    .map(HeaderCard::getValue)
                                    .map(Float::parseFloat)
                                    .orElse(1F)
                    )
                    .temp(
                            Optional.ofNullable(hdu.getHeader().findCard("CCD-TEMP")).map(HeaderCard::getValue).map(Float::parseFloat).orElse(null)
                    )
                    .build();
        } catch (FitsException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
