FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/*.jar /app/app.jar

EXPOSE 8080

RUN apt-get update && apt-get install -y ffmpeg

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.additional-location=file:/app/config/"]
