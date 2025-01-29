FROM openjdk:17
COPY build/libs/*.jar app.jar
COPY src/main/resources/application.properties /app/application.properties

ENTRYPOINT ["java", "-jar", "/app.jar"]
