FROM openjdk:21
COPY ./target/astrotheque.jar /app/
COPY ./dist /app/dist
COPY ./dbs /app/dbs
WORKDIR /app
ENTRYPOINT ["java", "-jar", "astrotheque.jar"]

EXPOSE 8080