FROM docker.stx/openjdk-21:1.19-1
COPY target/tasklist*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]