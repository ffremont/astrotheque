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

        try {
            final BasicHDU<?> hdu = (new Fits(fitFile.toAbsolutePath().toFile())).readHDU();

            // from fit header or creation file date
            var dateObs = Optional.ofNullable(
                            hdu.getHeader()).map(header -> Optional.ofNullable(header.findCard("DATE-OBS")).orElse(header.findCard("DATE"))).map(c -> c.getValue())
                    .filter(Predicate.not(String::isEmpty))
                    .map(LocalDateTime::parse);
            ;

            final var exposure = Optional.ofNullable(hdu.getHeader().findCard("EXPOSURE"))
                    .map(HeaderCard::getValue)
                    .map(Float::parseFloat)
                    .orElse(1F);
            final var exptime = Optional.ofNullable(hdu.getHeader().findCard("EXPTIME"))
                    .map(HeaderCard::getValue)
                    .map(Float::parseFloat)
                    .orElse(exposure);

            var fitBuilder = FitData.builder()
                    .object(Optional.ofNullable(hdu.getHeader().findCard("OBJECT"))
                            .map(HeaderCard::getValue)
                            .orElse(null))
                    .tempFile(fitFile)
                    .gain(Optional.ofNullable(hdu.getHeader().findCard("GAIN")).map(HeaderCard::getValue).map(Double::valueOf).map(Double::intValue).orElse(null))
                    .dateObs(dateObs.orElse(LocalDateTime.now()))
                    .instrume(Optional.ofNullable(hdu.getHeader().findCard("INSTRUME")).map(HeaderCard::getValue).orElse(""))
                    .exposure(exptime)
                    .temp(
                            Optional.ofNullable(hdu.getHeader().findCard("CCD-TEMP")).map(HeaderCard::getValue).map(Float::parseFloat).orElse(null)
                    );

            var stackCnt = Optional.ofNullable(hdu.getHeader().findCard("STACKCNT")).map(HeaderCard::getValue).map(Integer::valueOf);
            var expTime = Optional.ofNullable(hdu.getHeader().findCard("EXPTIME")).map(HeaderCard::getValue).map(Float::parseFloat);

            expTime.ifPresent(expTimeValue -> {
                float cnt = expTimeValue / exptime;
                fitBuilder.stackCnt((int) cnt);
            });
            stackCnt.ifPresent(fitBuilder::stackCnt);

            return fitBuilder.build();
        } catch (FitsException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
