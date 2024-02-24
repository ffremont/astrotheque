package com.github.ffremont.astrotheque.core.httpserver.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.ffremont.astrotheque.AstrothequeApplication.HTML_DIR;

@Slf4j
public class FrontendContext implements HttpHandler {

    private final HttpHandler frontendHandler;

    private final String indexHtml;
    private final Path htmlDir;


    public FrontendContext() {
        try {
            this.indexHtml = Files.readString(HTML_DIR.resolve("index.html"));
            this.htmlDir = HTML_DIR;
        } catch (IOException e) {
            throw new RuntimeException("Index.html introuvable dans " + HTML_DIR, e);
        }
        frontendHandler = SimpleFileServer.createFileHandler(HTML_DIR.toAbsolutePath());
    }


    /**
     * Retourne l'index si
     * - fichier non trouvé
     * - la recherche ne se fait pas dans htmlDir
     * - on veut /
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path resourcePath = htmlDir.resolve(exchange.getRequestURI().getPath().substring(1));
        if (exchange.getHttpContext().getPath().equals(exchange.getRequestURI().getPath())
                || !htmlDir.resolve(exchange.getRequestURI().getPath().substring(1)).toFile().exists()
                || !resourcePath.toFile().getCanonicalPath().startsWith(htmlDir.toFile().getCanonicalPath())) {
            try (exchange) {
                log.debug("Retour de la page index");
                OutputStream outputStream = exchange.getResponseBody();
                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, indexHtml.length());
                outputStream.write(indexHtml.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        } else {
            frontendHandler.handle(exchange);
        }
    }


}
