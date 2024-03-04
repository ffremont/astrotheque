package com.github.ffremont.astrotheque;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.httpserver.context.FrontendContext;
import com.github.ffremont.astrotheque.core.httpserver.context.SimpleContext;
import com.github.ffremont.astrotheque.core.httpserver.route.StreamingRoute;
import com.github.ffremont.astrotheque.core.security.AstroAuthenticator;
import com.github.ffremont.astrotheque.dao.DeepSkyCatalogDAO;
import com.github.ffremont.astrotheque.dao.PictureDAO;
import com.github.ffremont.astrotheque.service.AccountService;
import com.github.ffremont.astrotheque.service.DynamicProperties;
import com.github.ffremont.astrotheque.service.model.Configuration;
import com.github.ffremont.astrotheque.service.model.Picture;
import com.github.ffremont.astrotheque.web.*;
import com.github.ffremont.astrotheque.web.model.Empty;
import com.github.ffremont.astrotheque.web.model.LoginRequest;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;

import static com.github.ffremont.astrotheque.core.httpserver.route.JsonRoute.*;


@Slf4j
public class AstrothequeApplication {

    public final static Path HTML_DIR = Paths.get("./dist").toAbsolutePath();

    /**
     * @param args
     * @throws IOException
     * @see https://gist.github.com/JensWalter/0f19780d131d903879a2
     */
    public static void main(String[] args) throws IOException {
        /**
         * PICTURE_DIR
         */
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.setExecutor(Executors.newFixedThreadPool(
                Optional.ofNullable(System.getenv("WEB_THREAD_POOL")).map(Integer::valueOf).orElse(10)
        ));
        final var ioc = new IoC();

        ioc.load(DynamicProperties.class, DeepSkyCatalogDAO.class, PictureDAO.class, AccountService.class);
        var pictureRessource = ioc.get(PictureResource.class);
        var imageResource = ioc.get(ImageResource.class);
        var loginResource = ioc.get(LoginResource.class);
        var meResource = ioc.get(MeResource.class);
        var confResource = ioc.get(ConfigurationResource.class);
        var obsResource = ioc.get(ObservationResource.class);

        server.createContext("/", ioc.get(FrontendContext.class));
        server.createContext("/login", SimpleContext.with(
                post("", loginResource::login, LoginRequest.class)
        ));
        server.createContext("/install", SimpleContext.with(
                post("", confResource::install, Configuration.class),
                get("", confResource::isInstalled)
        ));
        server.createContext("/logout", SimpleContext.with(
                        post("", loginResource::logout, Empty.class)
                ))
                .setAuthenticator(ioc.get(AstroAuthenticator.class));
        server.createContext("/api/observation", obsResource)
                .setAuthenticator(ioc.get(AstroAuthenticator.class));
        server.createContext("/api", SimpleContext.with(
                        get("/config", confResource::getConfig),
                        get("/me", meResource::myProfil),
                        get("/pictures$", pictureRessource::all),
                        delete("/pictures/([\\w\\-]+)", pictureRessource::delete),
                        put("/pictures/([\\w\\-]+)", pictureRessource::update, Picture.class),

                        StreamingRoute.get("/pictures/raw/([\\w\\-]+)$", imageResource::raw, "image/fits"),
                        StreamingRoute.get("/pictures/thumb/([\\w\\-]+)$", imageResource::thumb, "image/jpeg"),
                        StreamingRoute.get("/pictures/image/([\\w\\-]+)$", imageResource::image, "image/jpeg"),
                        StreamingRoute.get("/pictures/annotated/([\\w\\-]+)$", imageResource::annotated, "image/jpeg")
                ))
                .setAuthenticator(ioc.get(AstroAuthenticator.class));

        log.info("Astrotheque started !");
        server.start();
    }

}
