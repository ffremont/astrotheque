package com.github.ffremont.astrotheque;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.http.JsonContext;
import com.github.ffremont.astrotheque.web.PictureResource;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import static com.github.ffremont.astrotheque.core.http.JsonRoute.get;


@Slf4j
public class AstrothequeApplication {

    /**
     * @param args
     * @throws IOException
     * @see https://gist.github.com/JensWalter/0f19780d131d903879a2
     */
    public static void main(String[] args) throws IOException {
        /**
         * PICTURE_DIR
         */
        HttpServer server = SimpleFileServer.createFileServer(new InetSocketAddress(8080), Paths.get("./public").toAbsolutePath(), SimpleFileServer.OutputLevel.INFO);
        final var ioc = new IoC();

        var pictureRessource = ioc.get(PictureResource.class);
        ioc.started();

        server.createContext("/api", JsonContext.with(
                get("/pictures/hello", pictureRessource::hello)
        ));

        log.info("Astrotheque started !");
        server.start();
    }

}
