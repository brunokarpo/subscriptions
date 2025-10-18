FROM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY ./infra/app/target/app-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]