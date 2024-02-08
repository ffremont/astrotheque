package com.github.ffremont.astrotheque;

import com.github.ffremont.astrotheque.core.IoC;
import com.github.ffremont.astrotheque.core.http.JsonRoute;
import com.github.ffremont.astrotheque.web.PictureResource;
import com.sun.net.httpserver.HttpHandlers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.List;

import static com.github.ffremont.astrotheque.core.http.JsonRoute.Method.*;


@Slf4j
public class AstrothequeApplication {

	/**
	 * @see https://gist.github.com/JensWalter/0f19780d131d903879a2
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/**
		 * PICTURE_DIR
		 */

		HttpServer server = SimpleFileServer.createFileServer(new InetSocketAddress(8080), Paths.get("./public").toAbsolutePath(), SimpleFileServer.OutputLevel.INFO);
		final var ioc = new IoC();

		var pictureRessource = ioc.get(PictureResource.class);
		List<JsonRoute> routes = List.of(
				new JsonRoute(GET, "/pictures/hello", pictureRessource::hello)
		);
		ioc.started();

		server.createContext("/api",exchange -> {
			try {
				routes.stream().filter(jsonRoute -> jsonRoute.test(exchange))
						.findFirst()
						.ifPresentOrElse(route -> route.handle(exchange), () -> {
							try {
								exchange.sendResponseHeaders(404,0);
							} catch (IOException e) {
								throw new RuntimeException("NotFound in error", e);
							}
						});
			}catch(Exception e){
				log.error("Server error occurs",e);
				exchange.sendResponseHeaders(500,0);
			}
		});

		log.info("Astrotheque started !");
		server.start();
	}

}
