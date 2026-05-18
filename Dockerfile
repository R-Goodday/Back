FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y ffmpeg

COPY build/libs/*.jar /app/app.jar

EXPOSE 8080


ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.additional-location=file:/app/config/"]
