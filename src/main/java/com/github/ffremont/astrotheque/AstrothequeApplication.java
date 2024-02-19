package com.github.ffremont.astrotheque;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.FrontendContext;
import com.github.ffremont.astrotheque.core.httpserver.JsonContext;
import com.github.ffremont.astrotheque.web.PictureResource;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import static com.github.ffremont.astrotheque.core.httpserver.JsonRoute.get;


@Slf4j
public class AstrothequeApplication {

    private final static Path HTML_DIR = Paths.get("./dist").toAbsolutePath();

    /**
     * @param args
     * @throws IOException
     * @see https://gist.github.com/JensWalter/0f19780d131d903879a2
     */
    public static void main(String[] args) throws IOException {
        /**
         * PICTURE_DIR
         */
        //HttpServer server = SimpleFileServer.createFileServer(new InetSocketAddress(8080), Paths.get("./dist").toAbsolutePath(), SimpleFileServer.OutputLevel.INFO);
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        final var ioc = new IoC();

        var pictureRessource = ioc.get(PictureResource.class);
        ioc.started();

        server.createContext("/", new FrontendContext(HTML_DIR));
        server.createContext("/api", JsonContext.with(
                get("/pictures/hello", pictureRessource::hello)
        ));

        log.info("Astrotheque started !");
        server.start();
    }

}
