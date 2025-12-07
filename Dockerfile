FROM eclipse-temurin:17-jdk

COPY build/libs/*.jar app.jar
COPY src/main/resources/application.properties /app/application.properties

ENTRYPOINT ["java", "-jar", "/app.jar"]
