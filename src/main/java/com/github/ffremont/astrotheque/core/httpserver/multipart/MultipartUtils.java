package com.github.ffremont.astrotheque.core.httpserver.multipart;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.ParameterParser;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.function.Predicate.not;

public class MultipartUtils {
    private MultipartUtils() {

    }

    /**
     * Efface les fichiers temporaire sur les upload de documents
     *
     * @param parts
     */
    public static void clear(List<Part> parts) {
        parts.stream().filter(part -> Objects.nonNull(part.file())).forEach(part -> {
            try {
                Files.deleteIfExists(part.file());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Stream le flux multipart pour créer les fichiers temp. si nécessaire
     *
     * @param httpExchange
     * @return
     */
    public static List<Part> from(HttpExchange httpExchange) {
        final String contentTypePrefix = "Content-Type: ";
        Headers headers = httpExchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);

        MultipartStream multipartStream = new MultipartStream(httpExchange.getRequestBody(), boundary.getBytes(StandardCharsets.UTF_8));

        List<Part> parts = new ArrayList<>();
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        try {
            boolean nextPart = multipartStream.skipPreamble();

            while (nextPart) {
                /**
                 * Content-Disposition: form-data; name="feuille"; filename="feuille-exercice-phases-lunaires.pdf"
                 * Content-Type: application/pdf
                 */
                String header = multipartStream.readHeaders();
                final String contentTypeOfPart = header.lines()
                        .filter(not(String::isBlank))
                        .filter(line -> line.indexOf(contentTypePrefix) > -1)
                        .map(ct -> ct.substring(14)).findFirst().orElse("text/plain");
                Map<String, String> fields = header.lines()
                        .filter(not(String::isBlank))
                        .filter(not(contentTypePrefix::startsWith))
                        .map(line -> parser.parse(line, new char[]{';'}))
                        .findFirst()
                        .orElse(Collections.emptyMap());

                final String filename = fields.getOrDefault("filename", "null");
                Part.PartBuilder partBuilder = Part.builder()
                        .name(fields.getOrDefault("name", "unknown"))
                        .filename(filename)
                        .contentType(contentTypeOfPart);

                if (Objects.nonNull(filename)) {
                    Path tmpFile = Files.createTempFile("upload-",
                            filename.contains(".") ?
                                    filename.substring(filename.lastIndexOf(".")) : ".unknown");
                    multipartStream.readBodyData(new FileOutputStream(tmpFile.toFile()));
                    partBuilder.file(tmpFile);
                } else {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    multipartStream.readBodyData(output);
                    partBuilder.value(output.toString("UTF-8"));
                }

                parts.add(partBuilder.build());
                nextPart = multipartStream.readBoundary();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parts;
    }
}
