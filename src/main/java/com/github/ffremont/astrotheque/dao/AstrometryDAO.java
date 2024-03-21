package com.github.ffremont.astrotheque.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.NovaInfo;
import com.github.ffremont.astrotheque.service.model.NovaLogin;
import com.github.ffremont.astrotheque.service.model.NovaSubmission;
import com.github.ffremont.astrotheque.service.model.NovaUpload;
import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MultipartBodyPublisher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

@Slf4j
public class AstrometryDAO {

    final String baseurl;


    private final static ObjectMapper JSON = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());
    private final HttpClient httpClient;

    public AstrometryDAO(String baseurl) {
        this.baseurl = baseurl;
        this.httpClient = Methanol.create();
    }

    public AstrometryDAO(IoC ioC) {
        this(ioC.get(DynamicProperties.class).getAstrometryNovaBaseUrl());
    }


    /**
     * Permet de créer une session Astrometry Nova
     *
     * @param apiKey
     * @return
     */
    public String createLoginSession(String apiKey) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/api/login"))
                    .timeout(Duration.ofSeconds(15))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("""
                            request-json={"apikey": "$APIKEY"}
                            """.replace("$APIKEY", apiKey)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            final String body = response.body();
            String session = JSON.readValue(body, NovaLogin.class).session();
            if (Objects.isNull(session)) {
                log.error("Nova Astrometry session invalid {}", body);
            }
            return session;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param sessionId
     * @param file
     * @return submission Id
     */
    public Integer upload(String sessionId, Path file) {
        try {
            MultipartBodyPublisher multipartBody = MultipartBodyPublisher.newBuilder()
                    .textPart("request-json", """
                                {
                                "publicly_visible": "n",
                                "allow_modifications": "d",
                                "session": "$SESSION",
                                "allow_commercial_use": "n"                          
                            }
                            """.replace("$SESSION", sessionId))
                    .filePart("file", file)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseurl + "/api/upload"))
                    .timeout(Duration.ofMinutes(5))
                    .headers("Content-Type", "multipart/form-data")
                    .POST(multipartBody)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            final String body = response.body();
            NovaUpload upload = JSON.readValue(response.body(), NovaUpload.class);
            if (Objects.isNull(upload.subid())) {
                log.error("Submission id introuvable {}", body);
            }
            return upload.subid();

        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère l'image annotée
     *
     * @param jobId
     * @return
     */
    public InputStream getAnnotatedImage(Integer jobId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/annotated_display/" + jobId))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * retourne le format fit normalisé par astrometry nova
     *
     * @param jobId
     * @return
     */
    public InputStream getFit(Integer jobId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/new_fits_file/" + jobId))
                    .timeout(Duration.ofMinutes(3))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retourne JPG image
     *
     * @param imageId
     * @return
     */
    public InputStream getImage(Integer imageId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/image/" + imageId))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build();
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String reportUrlOf(NovaSubmission subInfo) {
        return "https://nova.astrometry.net/user_images/" + subInfo.user_images().stream().findFirst().orElseThrow();
    }

    /**
     * Récupère des infos sur la soumission de l'analyse
     *
     * @param submissionId
     * @return
     */
    public NovaSubmission getSubInfo(Integer submissionId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/api/submissions/" + submissionId))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JSON.readValue(response.body(), NovaSubmission.class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère des infos sur l'aanlyse
     *
     * @param jobId
     * @return
     */
    public NovaInfo info(Integer jobId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(this.baseurl + "/api/jobs/" + jobId + "/info/"))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return JSON.readValue(response.body(), NovaInfo.class);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
