FROM docker.stx/eclipse-temurin:21.0.7_6-jre-ubi9-minimal
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY target/tasklist*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]