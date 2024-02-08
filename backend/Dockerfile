FROM openjdk:21
COPY ./target/astrotheque.jar /app/
COPY ./public /app/public
WORKDIR /app
ENTRYPOINT ["java", "-jar", "astrotheque.jar"]

EXPOSE 8080