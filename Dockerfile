FROM openjdk:21-jdk-slim
COPY target/BookRatingSystem-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "--enable-preview", "-jar", "/app.jar"]

